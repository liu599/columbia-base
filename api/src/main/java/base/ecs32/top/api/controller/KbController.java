package base.ecs32.top.api.controller;

import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.service.KbService;
import base.ecs32.top.api.vo.KbFileVO;
import base.ecs32.top.api.vo.KbVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kb")
@RequiredArgsConstructor
public class KbController {

    private final KbService kbService;

    // ==================== KB CRUD ====================

    /**
     * 创建知识库
     */
    @PostMapping("/create")
    public KbVO createKb(@RequestBody KbCreateRequest request) {
        return kbService.createKb(request);
    }

    /**
     * 更新知识库
     */
    @PostMapping("/update")
    public KbVO updateKb(@RequestBody KbUpdateRequest request) {
        return kbService.updateKb(request);
    }

    /**
     * 删除知识库
     */
    @PostMapping("/delete")
    public void deleteKb(@RequestBody KbDeleteRequest request) {
        kbService.deleteKb(request);
    }

    /**
     * 获取知识库详情
     */
    @PostMapping("/get")
    public KbVO getKb(@RequestBody KbGetRequest request) {
        return kbService.getKb(request);
    }

    /**
     * 获取用户的知识库列表
     */
    @PostMapping("/list")
    public List<KbVO> listKbs(@RequestBody KbListRequest request) {
        return kbService.listKbs(request);
    }

    // ==================== KB File Association ====================

    /**
     * 添加文件到知识库
     */
    @PostMapping("/file/add")
    public KbFileVO addFileToKb(@RequestBody KbFileAddRequest request) {
        return kbService.addFileToKb(request);
    }

    /**
     * 从知识库移除文件
     */
    @PostMapping("/file/remove")
    public void removeFileFromKb(@RequestBody KbFileRemoveRequest request) {
        kbService.removeFileFromKb(request);
    }

    /**
     * 更新知识库文件状态
     */
    @PostMapping("/file/updateStatus")
    public KbFileVO updateKbFileStatus(@RequestBody KbFileUpdateStatusRequest request) {
        return kbService.updateKbFileStatus(request);
    }

    /**
     * 获取知识库文件列表
     */
    @PostMapping("/file/list")
    public List<KbFileVO> listKbFiles(@RequestBody KbFileListRequest request) {
        return kbService.listKbFiles(request);
    }
}
