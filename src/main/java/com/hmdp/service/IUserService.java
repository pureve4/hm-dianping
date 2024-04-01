package com.hmdp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;

import javax.servlet.http.HttpSession;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Administrator
 */
public interface IUserService extends IService<User> {

    /**
     * 发送短信
     *
     * @param phone
     * @param session
     * @return
     */
    Result sendCode(String phone, HttpSession session);

    /**
     * 登录功能
     *
     * @param loginForm
     * @param session
     * @return
     */
    Result loginSession(LoginFormDTO loginForm, HttpSession session);

    /**
     * 基于Redis登录
     *
     * @param loginForm
     * @param session
     * @return
     */
    Result login(LoginFormDTO loginForm, HttpSession session);

    /**
     * 获取当前用户
     *
     * @param request
     */
    void me(HttpSession request);


}
