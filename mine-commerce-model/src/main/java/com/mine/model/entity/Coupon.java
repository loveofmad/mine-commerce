package com.mine.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_coupon")
public class Coupon implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer type;

    private BigDecimal amount;

    private BigDecimal minAmount;

    private Integer total;

    private Integer used;

    private Integer perLimit;

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
