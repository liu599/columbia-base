package base.ecs32.top.api.controller;

import base.ecs32.top.api.dto.FileQueryRequest;
import base.ecs32.top.api.service.FileService;
import base.ecs32.top.api.vo.FileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 接口1: 上传文件
     */
    @PostMapping("/upload")
    public FileVO upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("userId") Long userId,
                         @RequestParam(value = "metadata", required = false) String metadata) {
        return fileService.uploadFile(file, userId, metadata);
    }

    /**
     * 接口2: 查询文件
     */
    @PostMapping("/get")
    public FileVO getFile(@RequestBody FileQueryRequest request) {
        return fileService.getFile(request);
    }

    /**
     * 接口3: 更新文件元数据
     */
    @PostMapping("/update")
    public void updateMetadata(@RequestBody FileQueryRequest request) {
        fileService.updateFileMetadata(request);
    }

    /**
     * 接口4: 删除文件
     */
    @PostMapping("/delete")
    public void delete(@RequestBody FileQueryRequest request) {
        fileService.deleteFile(request.getFileUuid());
    }
}
