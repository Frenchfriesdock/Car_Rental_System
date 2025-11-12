package com.hosiky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.common.Result;
import com.hosiky.domain.po.Brand;
import com.hosiky.mapper.BrandMapper;
import com.hosiky.mapper.CarMapper;
import com.hosiky.service.IBrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {


    private BrandMapper brandMapper;

    private CarMapper carMapper;

    /**
     * 这个用事务处理
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)   // 添加事务管理
    public Result deleteByIds(List<Long> ids) {
        if(ids == null || ids.isEmpty()) {
            return Result.errorMsg("删除的ID列表不能为空");
        }
        try {
//             先删除关联数据
            for(Long id : ids) {
                carMapper.deleteById(id);
            }

//            在删除品牌数据
            boolean isSuccess = removeBatchByIds(ids);
            if(isSuccess) {
                return Result.ok("批量删除成功");
            } else {
                return Result.errorMsg("批量删除失败");
            }
        }catch (Exception e) {
           log.error("批量删除品牌失败: ids={}", ids, e);
           throw  new RuntimeException(e);
        }
    }


    @Override
    public Page<Brand> getBrandsByPage(int page, int rows) {

        Page<Brand> brandPage = new Page<>(page, rows);
//        给予的查询条件
        LambdaQueryWrapper<Brand> brandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        brandLambdaQueryWrapper.eq(Brand::getDeleted,0);
        return this.page(brandPage, brandLambdaQueryWrapper);
    }
}
