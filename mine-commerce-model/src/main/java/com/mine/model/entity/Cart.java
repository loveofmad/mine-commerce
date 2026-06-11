package com.mine.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_cart")
public class Cart implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long spuId;

    private Long skuId;

    private String spuName;

    private String skuTitle;

    private String skuImage;

    private BigDecimal price;

    private Integer quantity;

    private Integer checked;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
