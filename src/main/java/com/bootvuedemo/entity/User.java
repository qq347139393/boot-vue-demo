package com.bootvuedemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.bootvuedemo.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author Planet
 * @since 2022-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="User对象", description="")
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String password;

    private Integer age;

    private Integer gender;

    private String describ;

    @TableField(fill = FieldFill.INSERT)
    private Integer status;

    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private Date creatime;

    private String updator;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatime;

    @TableField(fill = FieldFill.INSERT)
    @Version
    private Long version;

    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleted;

    //前端判断token是否合法的参数
    @TableField(exist = false)
    private String jwtToken;


}
