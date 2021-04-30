package com.arbor.mall.service;


import com.arbor.mall.exception.ArborMallException;
import com.arbor.mall.model.pojo.User;

/**
 * UserService
 */
public interface UserService {

    User getUser();

    /**
     * 注册新用户
     * @param userName
     * @param password
     */
    void register(String userName, String password) throws ArborMallException;

    /**
     * 登录功能
     * @param userName
     * @param password
     * @return
     * @throws ArborMallException
     */
    User login(String userName, String password) throws ArborMallException;

    /**
     * 更新个性签名
     * @param user
     */
    void updateInformation(User user) throws ArborMallException;


    /**
     * 判断登录用户是否为管理员
     * @param user
     * @return
     */
    boolean checkAdminRole(User user);
}
