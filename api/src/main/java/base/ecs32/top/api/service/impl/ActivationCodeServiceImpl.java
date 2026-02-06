package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.service.ActivationCodeService;
import base.ecs32.top.api.vo.RedeemVO;
import base.ecs32.top.dao.ActivationCodeMapper;
import base.ecs32.top.dao.CreditBalanceMapper;
import base.ecs32.top.dao.CreditLogMapper;
import base.ecs32.top.dao.ProductMapper;
import base.ecs32.top.entity.ActivationCode;
import base.ecs32.top.entity.CreditBalance;
import base.ecs32.top.entity.CreditLog;
import base.ecs32.top.entity.Product;
import base.ecs32.top.enums.ActivationCodeStatus;
import base.ecs32.top.enums.CreditLogType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ActivationCodeServiceImpl implements ActivationCodeService {

    private final ActivationCodeMapper activationCodeMapper;
    private final ProductMapper productMapper;
    private final CreditBalanceMapper creditBalanceMapper;
    private final CreditLogMapper creditLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RedeemVO redeem(Long userId, String code) {
        // 1. Check activation code
        ActivationCode ac = activationCodeMapper.selectOne(
                new LambdaQueryWrapper<ActivationCode>().eq(ActivationCode::getCode, code)
        );
        if (ac == null) {
            throw new RuntimeException("激活码不存在");
        }
        if (ac.getStatus() != ActivationCodeStatus.UNUSED) {
            throw new RuntimeException("激活码已失效");
        }

        // 2. Fetch product
        Product product = productMapper.selectById(ac.getProductId());
        if (product == null) {
            throw new RuntimeException("关联产品不存在");
        }

        // 3. Update activation code status
        ac.setStatus(ActivationCodeStatus.USED);
        ac.setUserId(userId);
        ac.setUsedTime(LocalDateTime.now());
        activationCodeMapper.updateById(ac);

        // 4. Update credit balance
        Integer addedCredits = product.getBaseCredits() != null ? product.getBaseCredits() : 0;
        CreditBalance balance = creditBalanceMapper.selectById(userId);
        if (balance == null) {
            balance = new CreditBalance();
            balance.setUserId(userId);
            balance.setAvailableCredits(addedCredits);
            balance.setFrozenCredits(0);
            balance.setUpdateTime(LocalDateTime.now());
            creditBalanceMapper.insert(balance);
        } else {
            balance.setAvailableCredits(balance.getAvailableCredits() + addedCredits);
            balance.setUpdateTime(LocalDateTime.now());
            creditBalanceMapper.updateById(balance);
        }

        // 5. Record log (ACTIVATE is positive)
        CreditLog log = new CreditLog();
        log.setUserId(userId);
        log.setType(CreditLogType.ACTIVATE);
        log.setAmount(addedCredits);
        log.setDescription("激活产品: " + product.getName());
        log.setCreateTime(LocalDateTime.now());
        creditLogMapper.insert(log);

        // 6. Build response
        RedeemVO vo = new RedeemVO();
        vo.setProductName(product.getName());
        vo.setAddedCredits(addedCredits);
        vo.setCurrentBalance(balance.getAvailableCredits());
        return vo;
    }
}
