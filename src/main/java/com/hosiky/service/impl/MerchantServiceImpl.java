package com.hosiky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.MerchantDTO;
import com.hosiky.domain.po.Merchant;
import com.hosiky.domain.vo.MerchantVO;
import com.hosiky.mapper.CarMapper;
import com.hosiky.mapper.MerchantMapper;
import com.hosiky.service.IMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements IMerchantService {

    private CarMapper carMapper;

    private MerchantMapper merchantMapper;

    @Override
    public Result addMerchant(MerchantDTO merchantDTO) {
        if (merchantDTO == null) {
            return Result.errorMsg("添加信息不能为空");
        } else {
            Merchant merchant = new Merchant();
            BeanUtils.copyProperties(merchantDTO, merchant);
            merchant.setCreatedAt(LocalDateTime.now());
            merchant.setUpdatedAt(LocalDateTime.now());
            merchant.setDeleted(0);
            merchantMapper.insert(merchant);
        }
        return Result.ok();
    }

    @Override
    public MerchantVO getMerchantById(int merchantId) {
        return null;
    }

    @Override
    public Result deleteMerchant(int merchantId) {
        try {
            carMapper.deleteById(merchantId);
            boolean isSuccess = carMapper.deleteById(merchantId) > 0;
            if (isSuccess) {
                return Result.ok();
            } else {
                return Result.error();
            }
        } catch (Exception e) {
            log.error("批量删除车商失败");
            throw  new RuntimeException(e);
        }
    }
}
