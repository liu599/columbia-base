package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.vo.CreditBalanceVO;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.entity.CreditLog;

public interface CreditService {
    CreditBalanceVO getBalance(Long userId);
    PageResponse<CreditLog> getLogs(Long userId, SearchRequest request);
}
