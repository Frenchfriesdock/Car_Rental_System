package com.hosiky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.common.Result;
import com.hosiky.domain.po.Brand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IBrandService extends IService<Brand> {
    Result deleteByIds(List<Long> ids);

    Page<Brand> getBrandsByPage(int page, int rows);
}
