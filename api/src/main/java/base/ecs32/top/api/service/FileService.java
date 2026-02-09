package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.FileQueryRequest;
import base.ecs32.top.api.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 上传文件 (接口1)
     */
    FileVO uploadFile(MultipartFile file, Long userId, String metadata);

    /**
     * 查询文件 (接口2)
     */
    FileVO getFile(FileQueryRequest request);

    /**
     * 更新文件元数据 (接口3)
     */
    void updateFileMetadata(FileQueryRequest request);

    /**
     * 删除文件 (接口4)
     */
    void deleteFile(String fileUuid);
}
