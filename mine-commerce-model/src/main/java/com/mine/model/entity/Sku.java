package com.mine.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_sku")
public class Sku implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long spuId;

    private String title;

    private String specs;

    private String image;

    private BigDecimal price;

    private Integer stock;

    private Integer sales;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
