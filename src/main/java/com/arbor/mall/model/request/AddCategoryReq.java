package com.arbor.mall.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述：CategoryController的addCategory方法的参数
 */
public class AddCategoryReq {

    @NotNull(message = "分类名称不能为空")
    @Size(min = 2, max = 5)
    private String name;

    @NotNull(message = "分类级别不能为空")
    @Max(3)
    private Integer type;

    @NotNull(message = "上级目录ID不能为空")
    private  Integer parentId;

    @NotNull(message = "排序不能为空")
    private Integer orderNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "AddCategoryReq{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                '}';
    }
}
