package com.mine.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_operation_log")
public class OperationLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adminId;

    private String adminUsername;

    private String module;

    private String action;

    private String detail;

    private String ip;

    private LocalDateTime createTime;
}
