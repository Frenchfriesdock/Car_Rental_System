package com.hosiky.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements IMerchantService {

    private final CarMapper carMapper;

    private final MerchantMapper merchantMapper;

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
        Merchant merchant = merchantMapper.selectById(merchantId);
        MerchantVO merchantVO = new MerchantVO();
        BeanUtils.copyProperties(merchant, merchantVO);
        return merchantVO;
    }

    @Override
    public Result deleteMerchant(int merchantId) {

        return Result.ok(merchantMapper.deleteById(merchantId));
    }

    @Override
    public MerchantVO updateMerchantDTO(MerchantDTO merchantDto) {
        if(StringUtils.isEmpty(merchantDto.getId())){
            throw new RuntimeException("没有更新车商id，无法更新");
        }

        Merchant merchant = merchantMapper.selectById(merchantDto.getId());
        if(merchant == null){
            throw new RuntimeException("更新车商对象不存在");
        }

        LambdaUpdateWrapper<Merchant> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Merchant::getId, merchantDto.getId())
                .set(merchantDto.getCompany() != null, Merchant::getCompany,merchantDto.getCompany())
                .set(merchantDto.getUserId() != null, Merchant::getUserId,merchantDto.getUserId())
                .set(merchantDto.getLicenseNo() != null, Merchant::getLicenseNo,merchantDto.getLicenseNo())
                .set(merchantDto.getStatus() != null, Merchant::getStatus,merchantDto.getStatus())
                .set(Merchant::getUpdatedAt,LocalDateTime.now());

        boolean isSuccess = update(updateWrapper);
        if(!isSuccess){
            throw new RuntimeException("Car更新失败");
        }

        return getMerchantById(merchantDto.getId());
    }
}
