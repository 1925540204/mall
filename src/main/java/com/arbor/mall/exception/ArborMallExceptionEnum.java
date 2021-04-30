package com.arbor.mall.exception;

/**
 * 描述：异常枚举
 */
public enum ArborMallExceptionEnum {

    NEED_USER_NAME(10001, "用户名不能为空"),
    NEED_PASSWORD(10002, "密码不能为空"),
    PASSWORD_TOO_SHORT(10003, "密码不能低于8位"),
    NAME_EXISTED(10004, "不允许重名"),
    INSERT_FAILED(10005, "注册失败"),
    WRONG_PASSWORD(10006, "密码错误"),
    NEED_LOGIN(10007, "用户未登录"),
    UPDATE_FAILED(10008, "更新失败"),
    NEED_ADMIN(10009, "没有权限"),
    PARA_NOT_NULL(10010, "没有传入参数"),
    CREATE_FAILED(10011, "添加失败"),
    REQUEST_PARAM_ERROR(10012, "参数错误"),
    DELETE_FAILED(10013, "删除失败"),
    MKDIR_FAILED(10014, "文件夹创建失败"),
    UPLOAD_FAILED(10015, "图片上传失败"),
    NOT_SALE(10016, "商品状态不可售"),
    NOT_ENOUGH(10017, "商品库存不足"),
    CART_EMPTY(10018, "购物车中无商品"),
    NO_ENUM(10019, "未找到对应枚举"),
    NO_ORDER(10020, "找不到订单"),
    NOT_YOUR_ORDER(10021, "该账户无此订单"),
    WRONG_ORDER_STATUS(10022, "订单状态无法操作"),

    SYSTEM_ERROR(20000, "系统错误");


    /**
     * 异常码
     */
    Integer code;

    /**
     * 异常信息
     */
    String msg;

    ArborMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
