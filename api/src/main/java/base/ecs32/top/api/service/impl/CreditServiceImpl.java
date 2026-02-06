package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.service.CreditService;
import base.ecs32.top.api.util.QueryWrapperUtils;
import base.ecs32.top.api.vo.CreditBalanceVO;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.dao.CreditBalanceMapper;
import base.ecs32.top.dao.CreditLogMapper;
import base.ecs32.top.entity.CreditBalance;
import base.ecs32.top.entity.CreditLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditBalanceMapper creditBalanceMapper;
    private final CreditLogMapper creditLogMapper;

    @Override
    public CreditBalanceVO getBalance(Long userId) {
        CreditBalance balance = creditBalanceMapper.selectById(userId);
        CreditBalanceVO vo = new CreditBalanceVO();
        if (balance != null) {
            vo.setAvailableCredits(balance.getAvailableCredits());
            vo.setFrozenCredits(balance.getFrozenCredits());
            vo.setUpdateTime(balance.getUpdateTime());
        } else {
            vo.setAvailableCredits(0);
            vo.setFrozenCredits(0);
        }
        return vo;
    }

    @Override
    public PageResponse<CreditLog> getLogs(Long userId, SearchRequest request) {
        Page<CreditLog> page = new Page<>(request.getCurrent(), request.getPageSize());
        QueryWrapper<CreditLog> wrapper = QueryWrapperUtils.buildWrapper(request, Collections.singletonList("description"));
        wrapper.eq("user_id", userId);
        
        creditLogMapper.selectPage(page, wrapper);
        
        return PageResponse.of(page.getRecords(), page.getTotal(), (int)page.getCurrent(), (int)page.getSize());
    }
}
