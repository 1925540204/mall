package com.arbor.mall.controller;


import com.arbor.mall.common.ApiRestResponse;
import com.arbor.mall.common.Constant;
import com.arbor.mall.exception.ArborMallException;
import com.arbor.mall.exception.ArborMallExceptionEnum;
import com.arbor.mall.model.pojo.User;
import com.arbor.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 用户控制器
 */
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/test")
    @ResponseBody
    public User personalPage() {
        return userService.getUser();
    }


    /**
     * 注册新用户
     *
     * @param userName 用户名
     * @param password 密码
     */
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(@RequestParam("userName") String userName,
                                    @RequestParam("password") String password)
            throws ArborMallException {
        // 校验用户名是否为空
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_USER_NAME);
        }
        // 校验密码是否为空
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_PASSWORD);
        }
        // 校验密码长度是否低于8位
        if (password.length() < 8) {
            return ApiRestResponse.error(ArborMallExceptionEnum.PASSWORD_TOO_SHORT);
        }

        // 注册
        userService.register(userName, password);
        return ApiRestResponse.success();
    }

    /**
     * 登录功能
     *
     * @param userName
     * @param password
     * @param session
     * @return
     * @throws ArborMallException
     */
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse login(@RequestParam("userName") String userName,
                                 @RequestParam("password") String password,
                                 HttpSession session) throws ArborMallException {
        // 校验用户名是否为空
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_USER_NAME);
        }
        // 校验密码是否为空
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_PASSWORD);
        }
        // 登录
        User user = userService.login(userName, password);
        // 将用户信息存入session中时，不保存密码信息
        user.setPassword(null);
        session.setAttribute(Constant.ARBOR_MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    /**
     * 更新个性签名
     *
     * @param signature
     * @param session
     * @return
     * @throws ArborMallException
     */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(@RequestParam String signature,
                                          HttpSession session) throws ArborMallException {
        // 从session中获取登录信息
        User currentUser = (User) session.getAttribute(Constant.ARBOR_MALL_USER);
        // 如果没有为null则没有登录
        if (currentUser == null) {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setPersonalizedSignature(signature);
        user.setId(currentUser.getId());
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }


    /**
     * 登出
     *
     * @param session
     * @return
     */
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session) {
        session.removeAttribute(Constant.ARBOR_MALL_USER);
        return ApiRestResponse.success();
    }

    /**
     * 管理员登录功能
     *
     * @param userName
     * @param password
     * @param session
     * @return
     * @throws ArborMallException
     */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName,
                                      @RequestParam("password") String password,
                                      HttpSession session) throws ArborMallException {
        // 校验用户名是否为空
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_USER_NAME);
        }
        // 校验密码是否为空
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_PASSWORD);
        }
        // 登录
        User user = userService.login(userName, password);

        // 校验是否为管理员
        if (userService.checkAdminRole(user)) {
            // 是管理员时，执行操作
            // 将用户信息存入session中时，不保存密码信息
            user.setPassword(null);
            session.setAttribute(Constant.ARBOR_MALL_USER, user);
            return ApiRestResponse.success(user);
        }else {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_ADMIN);
        }

    }
}
