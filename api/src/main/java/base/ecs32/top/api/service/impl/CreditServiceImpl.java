package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.dto.CreditRechargeRequest;
import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.service.CreditService;
import base.ecs32.top.api.util.QueryWrapperUtils;
import base.ecs32.top.api.vo.CreditBalanceVO;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.dao.CreditBalanceMapper;
import base.ecs32.top.dao.CreditLogMapper;
import base.ecs32.top.entity.CreditBalance;
import base.ecs32.top.entity.CreditLog;
import base.ecs32.top.enums.CreditLogType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditBalanceMapper creditBalanceMapper;
    private final CreditLogMapper creditLogMapper;

    @Override
    public CreditBalanceVO getBalance(Long userId) {
        // ... (existing code)
        return null; // placeholder
    }

    @Override
    public PageResponse<CreditLog> getLogs(Long userId, SearchRequest request) {
        // ... (existing code)
        return null; // placeholder
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualRecharge(CreditRechargeRequest request) {
        CreditBalance balance = creditBalanceMapper.selectById(request.getTargetUserId());
        if (balance == null) {
            balance = new CreditBalance();
            balance.setUserId(request.getTargetUserId());
            balance.setAvailableCredits(request.getAmount());
            balance.setFrozenCredits(0);
            balance.setUpdateTime(LocalDateTime.now());
            creditBalanceMapper.insert(balance);
        } else {
            balance.setAvailableCredits(balance.getAvailableCredits() + request.getAmount());
            balance.setUpdateTime(LocalDateTime.now());
            creditBalanceMapper.updateById(balance);
        }

        CreditLog log = new CreditLog();
        log.setUserId(request.getTargetUserId());
        log.setType(CreditLogType.RECHARGE);
        log.setAmount(request.getAmount());
        log.setDescription("管理员人工充值: " + request.getDescription());
        log.setCreateTime(LocalDateTime.now());
        creditLogMapper.insert(log);
    }
}
