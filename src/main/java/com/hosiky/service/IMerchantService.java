package com.hosiky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.MerchantDTO;
import com.hosiky.domain.po.Merchant;
import com.hosiky.domain.vo.MerchantVO;
import org.springframework.stereotype.Service;

@Service
public interface IMerchantService extends IService<Merchant> {

    Result addMerchant(MerchantDTO merchantDTO);

    MerchantVO getMerchantById(int merchantId);

    Result deleteMerchant(int merchantId);

    MerchantVO updateMerchantDTO(MerchantDTO merchantDto);
}
