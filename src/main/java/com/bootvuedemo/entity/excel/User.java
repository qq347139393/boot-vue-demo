package com.bootvuedemo.entity.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.planetProvide.easyExcelPlus.core.annotation.ExcelNotNull;
import com.planetProvide.easyExcelPlus.core.annotation.ExcelNumRange;
import com.planetProvide.easyExcelPlus.core.annotation.ExcelStrLength;
import com.planetProvide.easyExcelPlus.core.entity.BaseRowInterface;
import com.planetProvide.easyExcelPlus.core.entity.Msg;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class User  implements Serializable, BaseRowInterface<User> {

    private static final long serialVersionUID = 1L;

    //    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private Long id;

    //    @ApiModelProperty(value = "用户名")
    @ExcelProperty(value="用户名",index = 0)//使用我们的框架,要么都不写index,要么都写index
    @ExcelNotNull
    private String name;

    @ExcelProperty(value="密码",index = 1)
    @ExcelNotNull
    @ExcelStrLength(min=3,max = 18)
    private String password;

    @ExcelNumRange(max = 200)
    @ExcelProperty(value="年龄",index = 2)
    private Integer age;

    @ExcelProperty(value="性别",index = 3)
    private Integer gender;

    @ExcelStrLength(max = 60)
    @ExcelProperty(value="描述信息",index = 4)
    private String describ;

    private long rowOrder;
    @Override
    public long getRowOrder() {
        return rowOrder;
    }

    @Override
    public void setRowOrder(long rowOrder) {
        this.rowOrder=rowOrder;
    }

    private int rowCode;
    @Override
    public int getRowCode() {
        return rowCode;
    }

    @Override
    public void setRowCode(int rowCode) {
        this.rowCode=rowCode;
    }

    private List<Msg> rowMsgs;
    @Override
    public List<Msg> getRowMsgs() {
        return rowMsgs;
    }

    @Override
    public void setRowMsgs(List<Msg> rowMsgs) {
        this.rowMsgs=rowMsgs;
    }
}
