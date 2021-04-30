package com.arbor.mall.model.dao;

import com.arbor.mall.model.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 用户名注册时重复校验
     * @param userName
     * @return
     */
    User selectByName(String userName);

    /**
     * 登录功能
     * @param userName
     * @param password
     * @return
     */
    User selectLogin(@Param("userName") String userName, @Param("password") String password);
}