package com.arbor.mall.util;

import com.arbor.mall.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 描述：MD5加密工具类
 */
public class MD5Utils {

    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest((strValue+ Constant.SALT).getBytes()));
    }


    /**
     * 测试MD5生成的值
     * @param args
     */
    public static void main(String[] args) {
        String md5 = null;
        try {
            md5 = getMD5Str("12345");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println(md5);


    }

}
