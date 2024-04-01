package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.bean.copier.CopyOptions.create;
import static com.hmdp.utils.RedisConstants.*;
import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Administrator
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 发送短信
     *
     * @param phone
     * @param session
     * @return
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        //1、校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            //2、如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        //3、符合，生产验证码
        String code = RandomUtil.randomNumbers(6);
        //4、保存验证码到session
        session.setAttribute("code",code);
        //5、发送验证码
        log.debug("发生短信验证码成功，验证码：{}", code);
        //返回ok
        return Result.ok();
    }

    /**
     * @param loginForm
     * @param session
     * @return
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        return null;
    }

    /**
     * 登录
     *
     * @param loginForm
     * @param session
     * @return
     */
    @Override
    public Result loginSession(LoginFormDTO loginForm, HttpSession session) {
        // 1.校验手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 3.校验验证码
        Object cacheCode = session.getAttribute("code");
        String code = loginForm.getCode();
        if (cacheCode == null || !cacheCode.toString().equals(code)) {
            //3.不一致，报错
            return Result.fail("验证码错误");
        }
        //一致，根据手机号查询用户.one()
        User user = query().eq("phone", phone).one();

        //5.判断用户是否存在
        if (user == null) {
            //不存在，则创建
            user = createUserWithPhone(phone);
        }
        //7.保存用户信息到session中
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        session.setAttribute("user", userDTO);
        return Result.ok();
    }

    /**
     * 接收手机号，创建用户。
     *
     * @param phone
     * @return
     */
    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        log.info("新建用户：" + user);
        save(user);
        return user;
    }

    /**
     * 查询当前用户的登录信息
     *
     * @param request
     */
    @Override
    public void me(HttpSession request) {
        request.removeAttribute("code");
    }
}
