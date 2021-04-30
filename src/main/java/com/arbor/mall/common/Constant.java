package com.arbor.mall.common;

import com.arbor.mall.exception.ArborMallException;
import com.arbor.mall.exception.ArborMallExceptionEnum;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 描述：常量值
 */
@Component
public class Constant {
    /**
     * MD5密码加密
     */
    public static final String SALT = "fs[ad/fll,rw;";

    /**
     * 登录的session的key
     */
    public static final String ARBOR_MALL_USER = "arbor_mall_user";

    /**
     * 上传图片的文件夹路径
     */
    public static String FILE_UPLOAD_DIR;

    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }


    /**
     * 前台商品列表排序规则
     */
    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }

    /**
     * 商品上下架状态
     */
    public interface SaleStatus {
        int NOT_SALE = 0;   // 商品下架状态
        int SALE = 1;   // 商品上架状态
    }

    /**
     * 购物车中被选中状态
     */
    public interface Cart {
        int UN_CHECKED = 0;   // 商品未被选中
        int CHECKED = 1;   // 商品被选中
    }

    public enum OrderStatusEnum {
        CANCELED(0, "已取消订单"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "交易完成");

        private int code;
        private String value;


        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new ArborMallException(ArborMallExceptionEnum.NO_ENUM);
        }


        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
