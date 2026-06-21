package com.mine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.common.exception.BusinessException;
import com.mine.mapper.SkuMapper;
import com.mine.mapper.SpuMapper;
import com.mine.model.entity.Sku;
import com.mine.model.entity.Spu;
import com.mine.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<SpuMapper, Spu> implements ProductService {

    @Autowired
    private SkuMapper skuMapper;

    @Override
    public IPage<Spu> listSpuByPage(String keyword, Long categoryId, String sortField, String sortOrder, int pageNum, int pageSize) {
        LambdaQueryWrapper<Spu> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Spu::getTitle, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Spu::getCategoryId, categoryId);
        }
        boolean asc = !"desc".equalsIgnoreCase(sortOrder);
        if ("create".equalsIgnoreCase(sortField)) {
            if (asc) wrapper.orderByAsc(Spu::getCreateTime); else wrapper.orderByDesc(Spu::getCreateTime);
        } else if ("sales".equalsIgnoreCase(sortField)) {
            if (asc) wrapper.orderByAsc(Spu::getSales); else wrapper.orderByDesc(Spu::getSales);
        } else if ("price".equalsIgnoreCase(sortField)) {
            if (asc) wrapper.orderByAsc(Spu::getPrice); else wrapper.orderByDesc(Spu::getPrice);
        } else {
            wrapper.orderByAsc(Spu::getSort).orderByDesc(Spu::getCreateTime);
        }
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public Spu getSpuDetail(Long id) {
        Spu spu = getById(id);
        if (spu == null) {
            throw new BusinessException("商品不存在");
        }
        return spu;
    }

    @Override
    public List<Sku> listSkuBySpuId(Long spuId) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getSpuId, spuId);
        return skuMapper.selectList(wrapper);
    }

    @Override
    public Long addSpu(Spu spu) {
        spu.setCreateTime(LocalDateTime.now());
        spu.setUpdateTime(LocalDateTime.now());
        spu.setDeleted(0);
        save(spu);
        return spu.getId();
    }

    @Override
    public boolean updateSpu(Spu spu) {
        spu.setUpdateTime(LocalDateTime.now());
        return updateById(spu);
    }

    @Override
    @Transactional
    public boolean deleteSpu(Long id) {
        removeById(id);
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getSpuId, id);
        skuMapper.delete(wrapper);
        return true;
    }

    @Override
    public boolean addSku(Sku sku) {
        sku.setCreateTime(LocalDateTime.now());
        sku.setUpdateTime(LocalDateTime.now());
        sku.setDeleted(0);
        skuMapper.insert(sku);
        return true;
    }

    @Override
    public boolean updateSku(Sku sku) {
        sku.setUpdateTime(LocalDateTime.now());
        skuMapper.updateById(sku);
        return true;
    }

    @Override
    public boolean deleteSku(Long id) {
        skuMapper.deleteById(id);
        return true;
    }

    @Override
    public boolean updateSpuStatus(Long id, Integer status) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setStatus(status);
        spu.setUpdateTime(LocalDateTime.now());
        return updateById(spu);
    }

    @Override
    public boolean updateSkuStatus(Long id, Integer status) {
        Sku sku = new Sku();
        sku.setId(id);
        sku.setStatus(status);
        sku.setUpdateTime(LocalDateTime.now());
        skuMapper.updateById(sku);
        return true;
    }
}
