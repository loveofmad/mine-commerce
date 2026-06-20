package com.mine.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mine.model.entity.Sku;
import com.mine.model.entity.Spu;

import java.util.List;

public interface ProductService extends IService<Spu> {

    IPage<Spu> listSpuByPage(String keyword, Long categoryId, int pageNum, int pageSize);

    Spu getSpuDetail(Long id);

    List<Sku> listSkuBySpuId(Long spuId);

    Long addSpu(Spu spu);

    boolean updateSpu(Spu spu);

    boolean deleteSpu(Long id);

    boolean addSku(Sku sku);

    boolean updateSku(Sku sku);

    boolean deleteSku(Long id);

    boolean updateSpuStatus(Long id, Integer status);

    boolean updateSkuStatus(Long id, Integer status);
}
