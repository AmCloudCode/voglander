package io.github.lunasaw.voglander.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author luna
 * @TableName tb_device_config
 */
@TableName(value = "tb_device_config")
@Data
public class DeviceConfigDO implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 状态 1正常 0删除
     */
    private Integer status;

    /**
     * deviceId = 0 是系统配置
     */
    private Long deviceId;

    /**
     *
     */
    private String configKey;

    /**
     *
     */
    private String configValue;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}