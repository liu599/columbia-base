package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.service.KbService;
import base.ecs32.top.api.vo.KbFileVO;
import base.ecs32.top.api.vo.KbVO;
import base.ecs32.top.dao.FileMapper;
import base.ecs32.top.dao.KbFileMapper;
import base.ecs32.top.dao.KbMapper;
import base.ecs32.top.entity.File;
import base.ecs32.top.entity.Kb;
import base.ecs32.top.entity.KbFile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KbServiceImpl implements KbService {

    private final KbMapper kbMapper;
    private final KbFileMapper kbFileMapper;
    private final FileMapper fileMapper;

    // ==================== KB CRUD ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KbVO createKb(KbCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Kb kb = Kb.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .description(request.getDescription())
                .createTime(now)
                .updateTime(now)
                .build();
        kbMapper.insert(kb);
        return toKbVO(kb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KbVO updateKb(KbUpdateRequest request) {
        Kb kb = kbMapper.selectById(request.getKbId());
        if (kb == null) {
            throw new BusinessException(ResultCode.KB_NOT_FOUND, "知识库不存在");
        }
        if (!kb.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ResultCode.KB_NO_PERMISSION, "无权限修改此知识库");
        }

        kb.setName(request.getName());
        kb.setDescription(request.getDescription());
        kb.setUpdateTime(LocalDateTime.now());
        kbMapper.updateById(kb);

        return toKbVO(kb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteKb(KbDeleteRequest request) {
        Kb kb = kbMapper.selectById(request.getKbId());
        if (kb == null) {
            throw new BusinessException(ResultCode.KB_NOT_FOUND, "知识库不存在");
        }
        if (!kb.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ResultCode.KB_NO_PERMISSION, "无权限删除此知识库");
        }

        // Delete all kb_file associations
        kbFileMapper.delete(new LambdaQueryWrapper<KbFile>()
                .eq(KbFile::getKbId, request.getKbId()));

        // Delete kb
        kbMapper.deleteById(request.getKbId());
    }

    @Override
    public KbVO getKb(KbGetRequest request) {
        Kb kb = kbMapper.selectById(request.getKbId());
        if (kb == null) {
            throw new BusinessException(ResultCode.KB_NOT_FOUND, "知识库不存在");
        }
        if (!kb.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ResultCode.KB_NO_PERMISSION, "无权限访问此知识库");
        }
        return toKbVO(kb);
    }

    @Override
    public List<KbVO> listKbs(KbListRequest request) {
        List<Kb> kbs = kbMapper.selectList(new LambdaQueryWrapper<Kb>()
                .eq(Kb::getUserId, request.getUserId())
                .orderByDesc(Kb::getCreateTime));
        return kbs.stream()
                .map(this::toKbVO)
                .collect(Collectors.toList());
    }

    // ==================== KB File Association ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KbFileVO addFileToKb(KbFileAddRequest request) {
        // Validate kb exists and belongs to user
        Kb kb = kbMapper.selectById(request.getKbId());
        if (kb == null) {
            throw new BusinessException(ResultCode.KB_NOT_FOUND, "知识库不存在");
        }
        if (!kb.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ResultCode.KB_NO_PERMISSION, "无权限访问此知识库");
        }

        // Validate file exists
        File file = fileMapper.selectById(request.getFileId());
        if (file == null) {
            throw new BusinessException(ResultCode.FILE_NOT_FOUND, "文件不存在");
        }

        // Check if already exists
        KbFile existing = kbFileMapper.selectOne(new LambdaQueryWrapper<KbFile>()
                .eq(KbFile::getKbId, request.getKbId())
                .eq(KbFile::getFileId, request.getFileId()));
        if (existing != null) {
            throw new BusinessException(ResultCode.KB_FILE_ALREADY_EXISTS, "文件已存在于知识库中");
        }

        // Create association
        LocalDateTime now = LocalDateTime.now();
        KbFile kbFile = KbFile.builder()
                .kbId(request.getKbId())
                .fileId(request.getFileId())
                .status("uploaded") // Initial status
                .parserConfig(request.getParserConfig())
                .createTime(now)
                .updateTime(now)
                .build();
        kbFileMapper.insert(kbFile);

        return toKbFileVO(kbFile, file);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFileFromKb(KbFileRemoveRequest request) {
        // Validate kb exists and belongs to user
        Kb kb = kbMapper.selectById(request.getKbId());
        if (kb == null) {
            throw new BusinessException(ResultCode.KB_NOT_FOUND, "知识库不存在");
        }
        if (!kb.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ResultCode.KB_NO_PERMISSION, "无权限访问此知识库");
        }

        kbFileMapper.delete(new LambdaQueryWrapper<KbFile>()
                .eq(KbFile::getKbId, request.getKbId())
                .eq(KbFile::getFileId, request.getFileId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KbFileVO updateKbFileStatus(KbFileUpdateStatusRequest request) {
        // Validate kb exists and belongs to user
        Kb kb = kbMapper.selectById(request.getKbId());
        if (kb == null) {
            throw new BusinessException(ResultCode.KB_NOT_FOUND, "知识库不存在");
        }
        if (!kb.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ResultCode.KB_NO_PERMISSION, "无权限访问此知识库");
        }

        // Validate status
        String status = request.getStatus();
        if (!isValidStatus(status)) {
            throw new BusinessException(ResultCode.KB_FILE_STATUS_INVALID, "无效的文件状态");
        }

        // Find and update kb_file
        KbFile kbFile = kbFileMapper.selectOne(new LambdaQueryWrapper<KbFile>()
                .eq(KbFile::getKbId, request.getKbId())
                .eq(KbFile::getFileId, request.getFileId()));

        if (kbFile == null) {
            throw new BusinessException(ResultCode.KB_FILE_NOT_FOUND, "知识库文件关联不存在");
        }

        kbFile.setStatus(status);
        if (request.getParserConfig() != null) {
            kbFile.setParserConfig(request.getParserConfig());
        }
        kbFile.setUpdateTime(LocalDateTime.now());
        kbFileMapper.updateById(kbFile);

        // Fetch file info
        File file = fileMapper.selectById(request.getFileId());
        return toKbFileVO(kbFile, file);
    }

    @Override
    public List<KbFileVO> listKbFiles(KbFileListRequest request) {
        // Validate kb exists and belongs to user
        Kb kb = kbMapper.selectById(request.getKbId());
        if (kb == null) {
            throw new BusinessException(ResultCode.KB_NOT_FOUND, "知识库不存在");
        }
        if (!kb.getUserId().equals(request.getUserId())) {
            throw new BusinessException(ResultCode.KB_NO_PERMISSION, "无权限访问此知识库");
        }

        List<KbFile> kbFiles = kbFileMapper.selectList(new LambdaQueryWrapper<KbFile>()
                .eq(KbFile::getKbId, request.getKbId())
                .orderByDesc(KbFile::getCreateTime));

        return kbFiles.stream()
                .map(kbFile -> {
                    File file = fileMapper.selectById(kbFile.getFileId());
                    return toKbFileVO(kbFile, file);
                })
                .collect(Collectors.toList());
    }

    // ==================== Helper Methods ====================

    private KbVO toKbVO(Kb kb) {
        return KbVO.builder()
                .kbId(kb.getId())
                .userId(kb.getUserId())
                .name(kb.getName())
                .description(kb.getDescription())
                .createTime(kb.getCreateTime())
                .updateTime(kb.getUpdateTime())
                .build();
    }

    private KbFileVO toKbFileVO(KbFile kbFile, File file) {
        KbFileVO vo = KbFileVO.builder()
                .id(kbFile.getId())
                .kbId(kbFile.getKbId())
                .fileId(kbFile.getFileId())
                .status(kbFile.getStatus())
                .parserConfig(kbFile.getParserConfig())
                .createTime(kbFile.getCreateTime())
                .updateTime(kbFile.getUpdateTime())
                .build();

        if (file != null) {
            vo.setFileUuid(file.getFileUuid());
            vo.setFileSize(file.getFileSize());
            vo.setContentType(file.getContentType());
            // Extract filename from ossPath or metadata
            String fileName = file.getOssPath().substring(file.getOssPath().lastIndexOf("/") + 1);
            vo.setFileName(fileName);
        }

        return vo;
    }

    private boolean isValidStatus(String status) {
        return status != null && (
                status.equals("parsing") ||
                status.equals("success") ||
                status.equals("error") ||
                status.equals("uploaded")
        );
    }
}
