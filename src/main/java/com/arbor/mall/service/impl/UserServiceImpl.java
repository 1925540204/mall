package com.arbor.mall.service.impl;

import com.arbor.mall.common.Constant;
import com.arbor.mall.exception.ArborMallException;
import com.arbor.mall.exception.ArborMallExceptionEnum;
import com.arbor.mall.model.dao.UserMapper;
import com.arbor.mall.model.pojo.User;
import com.arbor.mall.service.UserService;
import com.arbor.mall.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;


/**
 * UserService实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;


    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    /**
     * 注册新用户
     *
     * @param userName
     * @param password
     */
    @Override
    public void register(String userName, String password) throws ArborMallException {
        // 用户名重复校验
        User resilt = userMapper.selectByName(userName);
        if (resilt != null) {
            throw new ArborMallException(ArborMallExceptionEnum.NAME_EXISTED);
        }

        // 新增用户
        User user = new User();
        user.setUsername(userName);
        try {
            user.setPassword(MD5Utils.getMD5Str(password + Constant.SALT));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 返回的是修改成功的条数
        int count = userMapper.insertSelective(user);
        // 判断是否新增成功
        if (count == 0) {
            throw new ArborMallException(ArborMallExceptionEnum.INSERT_FAILED);
        }
    }


    /**
     * 登录功能
     * @param userName
     * @param password
     * @return
     * @throws ArborMallException
     */
    @Override
    public User login(String userName, String password) throws ArborMallException {
        // 将密码加密为MD5
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password + Constant.SALT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 查询用户
        User user = userMapper.selectLogin(userName, md5Password);
        // 判断是否查询成功
        if (user == null){
            throw new ArborMallException(ArborMallExceptionEnum.WRONG_PASSWORD);
        }

        return user;
    }

    /**
     * 更新个性签名
     *
     * @param user
     */
    @Override
    public void updateInformation(User user) throws ArborMallException {
        // 返回的是修改成功的条数
        int count = userMapper.updateByPrimaryKeySelective(user);
        if (count >1){
            throw new ArborMallException(ArborMallExceptionEnum.NEED_LOGIN);
        }
    }

    /**
     * 判断登录用户是否为管理员
     * @param user
     * @return
     */
    @Override
    public boolean checkAdminRole(User user){
        // 1是普通用户，2是管理员
        return user.getRole().equals(2);
    }
}
