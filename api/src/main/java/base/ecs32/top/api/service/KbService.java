package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.vo.KbFileVO;
import base.ecs32.top.api.vo.KbVO;

import java.util.List;

public interface KbService {

    /**
     * 创建知识库
     */
    KbVO createKb(KbCreateRequest request);

    /**
     * 更新知识库
     */
    KbVO updateKb(KbUpdateRequest request);

    /**
     * 删除知识库
     */
    void deleteKb(KbDeleteRequest request);

    /**
     * 获取知识库详情
     */
    KbVO getKb(KbGetRequest request);

    /**
     * 获取用户的所有知识库列表
     */
    List<KbVO> listKbs(KbListRequest request);

    /**
     * 添加文件到知识库
     */
    KbFileVO addFileToKb(KbFileAddRequest request);

    /**
     * 从知识库移除文件
     */
    void removeFileFromKb(KbFileRemoveRequest request);

    /**
     * 更新知识库文件状态
     */
    KbFileVO updateKbFileStatus(KbFileUpdateStatusRequest request);

    /**
     * 获取知识库中的文件列表
     */
    List<KbFileVO> listKbFiles(KbFileListRequest request);
}
