package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.dto.FileQueryRequest;
import base.ecs32.top.api.service.FileService;
import base.ecs32.top.api.util.OssUtils;
import base.ecs32.top.api.vo.FileVO;
import base.ecs32.top.dao.FileMapper;
import base.ecs32.top.entity.File;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;
    private final OssUtils ossUtils;
    private final ObjectMapper objectMapper;

    private static final long DEFAULT_EXPIRATION = 3600; // 1 hour

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO uploadFile(MultipartFile multipartFile, Long userId, String metadataStr) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String contentType = multipartFile.getContentType();
            long fileSize = multipartFile.getSize();
            byte[] bytes = multipartFile.getBytes();
            String md5 = DigestUtils.md5DigestAsHex(bytes);

            // 1. Check if the current user already has this file (including deleted)
            File existingUserFile = fileMapper.selectOne(new LambdaQueryWrapper<File>()
                    .eq(File::getUserId, userId)
                    .eq(File::getFileMd5, md5));

            if (existingUserFile != null) {
                if (existingUserFile.getIsDeleted() == 1) {
                    existingUserFile.setIsDeleted(0);
                    existingUserFile.setUpdateTime(LocalDateTime.now());
                    fileMapper.updateById(existingUserFile);
                }
                return toFileVO(existingUserFile);
            }

            // 2. Check if the file exists globally (deduplication)
            File globalFile = fileMapper.selectList(new LambdaQueryWrapper<File>()
                    .eq(File::getFileMd5, md5)
                    .last("LIMIT 1")).stream().findFirst().orElse(null);

            String bucketName;
            String ossPath;
            if (globalFile != null) {
                // Reuse existing OSS file
                bucketName = globalFile.getBucketName();
                ossPath = globalFile.getOssPath();
            } else {
                // Upload new file to OSS
                String suffix = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                LocalDateTime now = LocalDateTime.now();
                String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                String fileName = md5 + suffix;
                ossPath = String.format("storage/%s/%s", datePath, fileName);
                bucketName = ossUtils.getBucketName();
                
                ossUtils.uploadFile(ossPath, multipartFile.getInputStream(), contentType);
            }

            // 3. Prepare metadata
            ObjectNode metadataNode = parseMetadata(metadataStr, originalFilename);
            String finalMetadata = objectMapper.writeValueAsString(metadataNode);

            // 4. Save new record for current user
            String uuid = UUID.randomUUID().toString().replace("-", "");
            LocalDateTime now = LocalDateTime.now();
            File fileEntity = File.builder()
                    .fileUuid(uuid)
                    .fileMd5(md5)
                    .userId(userId)
                    .bucketName(bucketName)
                    .ossPath(ossPath)
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .metadata(finalMetadata)
                    .isDeleted(0)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            fileMapper.insert(fileEntity);

            return toFileVO(fileEntity);
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new BusinessException(ResultCode.SERVER_ERROR, "文件上传失败");
        }
    }

    private ObjectNode parseMetadata(String metadataStr, String originalFilename) throws JsonProcessingException {
        ObjectNode metadataNode;
        if (StringUtils.hasText(metadataStr)) {
            JsonNode node = objectMapper.readTree(metadataStr);
            if (node.isObject()) {
                metadataNode = (ObjectNode) node;
            } else {
                metadataNode = objectMapper.createObjectNode();
                metadataNode.put("data", metadataStr);
            }
        } else {
            metadataNode = objectMapper.createObjectNode();
        }
        metadataNode.put("originalName", originalFilename);
        return metadataNode;
    }

    private FileVO toFileVO(File fileEntity) {
        String signedUrl = ossUtils.generateSignedUrl(fileEntity.getBucketName(), fileEntity.getOssPath(), DEFAULT_EXPIRATION);
        
        Object metadataObj = null;
        if (StringUtils.hasText(fileEntity.getMetadata())) {
            try {
                metadataObj = objectMapper.readTree(fileEntity.getMetadata());
            } catch (JsonProcessingException e) {
                metadataObj = fileEntity.getMetadata();
            }
        }

        String fileName = fileEntity.getOssPath().substring(fileEntity.getOssPath().lastIndexOf("/") + 1);

        return FileVO.builder()
                .fileUuid(fileEntity.getFileUuid())
                .fileName(fileName)
                .signedUrl(signedUrl)
                .fileSize(fileEntity.getFileSize())
                .contentType(fileEntity.getContentType())
                .metadata(metadataObj)
                .bucketName(fileEntity.getBucketName())
                .ossPath(fileEntity.getOssPath())
                .createdAt(fileEntity.getCreateTime())
                .updatedAt(fileEntity.getUpdateTime())
                .build();
    }

    @Override
    public FileVO getFile(FileQueryRequest request) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(File::getIsDeleted, 0); // Only query non-deleted files
        
        if (StringUtils.hasText(request.getFileUuid())) {
            queryWrapper.eq(File::getFileUuid, request.getFileUuid());
        } else if (StringUtils.hasText(request.getOssPath())) {
            queryWrapper.eq(File::getOssPath, request.getOssPath());
        } else {
            throw new BusinessException(ResultCode.USER_ERROR, "文件标识不能为空");
        }

        File fileEntity = fileMapper.selectOne(queryWrapper);
        if (fileEntity == null) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND, "文件不存在");
        }

        return toFileVO(fileEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFileMetadata(FileQueryRequest request) {
        if (!StringUtils.hasText(request.getFileUuid())) {
            throw new BusinessException(ResultCode.USER_ERROR, "文件 UUID 不能为空");
        }

        File fileEntity = fileMapper.selectOne(new LambdaQueryWrapper<File>()
                .eq(File::getIsDeleted, 0)
                .eq(File::getFileUuid, request.getFileUuid()));
        
        if (fileEntity == null) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND, "文件不存在");
        }

        fileEntity.setMetadata(request.getNewMetadata());
        fileEntity.setUpdateTime(LocalDateTime.now());
        fileMapper.updateById(fileEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(String fileUuid) {
        File fileEntity = fileMapper.selectOne(new LambdaQueryWrapper<File>()
                .eq(File::getIsDeleted, 0)
                .eq(File::getFileUuid, fileUuid));
        
        if (fileEntity != null) {
            // Soft delete: only mark as deleted in DB, keep file in OSS
            fileEntity.setIsDeleted(1);
            fileEntity.setUpdateTime(LocalDateTime.now());
            fileMapper.updateById(fileEntity);
        }
    }
}
