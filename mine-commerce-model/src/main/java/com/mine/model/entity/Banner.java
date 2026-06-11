package com.mine.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_banner")
public class Banner implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String image;

    private String url;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
