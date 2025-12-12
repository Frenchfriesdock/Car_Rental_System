package com.hosiky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.common.PageParameter;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.BrandDTO;
import com.hosiky.domain.po.Brand;
import com.hosiky.domain.po.Car;
import com.hosiky.domain.vo.BrandVo;
import com.hosiky.mapper.BrandMapper;
import com.hosiky.mapper.CarMapper;
import com.hosiky.service.IBrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {


    private final BrandMapper brandMapper;

    private final CarMapper carMapper;
    private final CarServiceImpl carServiceImpl;

    /**
     * 这个用事务处理
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteByIds(List<String> ids) {
        if(ids == null || ids.isEmpty()) {
            return Result.errorMsg("删除的ID列表不能为空");
        }
        try {
            // 先删除关联数据
            for(String id : ids) {
                // 业务层使用更新方法实现逻辑删除
                LambdaUpdateWrapper<Car> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Car::getBrandId, id)
                        .set(Car::getDeleted, 1);
                carMapper.update(null, updateWrapper);
            }

//            实现逻辑删除后的时间更新
            Brand brand = new Brand();
            brand.setUpdatedAt(LocalDateTime.now());
            LambdaUpdateWrapper<Brand> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Brand::getDeleted,1);
            updateWrapper.in(Brand::getId, ids);

            boolean isSuccess = this.update(brand, updateWrapper);
            if(isSuccess) {
                return Result.ok("批量删除成功");
            } else {
                return Result.errorMsg("批量删除失败");
            }
        } catch (Exception e) {
            log.error("批量删除品牌失败: ids={}", ids, e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public Page<Brand> getBrandsByPage(PageParameter<BrandDTO> pageParameter) {

        Page<Brand> brandPage = new Page<>(pageParameter.getPage(), pageParameter.getLimit());
//        给予的查询条件
        LambdaQueryWrapper<Brand> brandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        brandLambdaQueryWrapper.eq(Brand::getDeleted,0);
        if(StringUtils.hasText(pageParameter.getData().getName())) {
            brandLambdaQueryWrapper.like(Brand::getName, pageParameter.getData().getName());
        }
        return this.page(brandPage, brandLambdaQueryWrapper);
    }

    @Override
    public BrandVo create(BrandDTO brandDto) {
        Brand brand = new Brand();
        BeanUtils.copyProperties(brandDto, brand);
        brand.setId((UUID.randomUUID().toString().replace("-", "")));
        brand.setDeleted(0);
        brand.setCreatedAt(LocalDateTime.now());
        brand.setUpdatedAt(LocalDateTime.now());
        brandMapper.insert(brand);
        BrandVo brandVo = new BrandVo();
        BeanUtils.copyProperties(brandDto, brandVo);
        return brandVo;
    }

    @Override
    public BrandVo getByBrandId(Integer id) {
        BrandVo brandVo = new BrandVo();
        Brand brand = brandMapper.selectById(id);
        BeanUtils.copyProperties(brand, brandVo);
        return brandVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteByIdsTrue(List<String> ids) {
        if(ids == null || ids.isEmpty()) {
            return Result.errorMsg("删除的ID列表不能为空");
        }
        try {
//             先删除关联数据
            for(String id : ids) {
               carServiceImpl.deleteCarById(id);
            }

//            在删除品牌数据
            boolean isSuccess = brandMapper.removeBatchByIdsTrue(ids);
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

}
