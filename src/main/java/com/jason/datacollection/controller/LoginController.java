package com.jason.datacollection.controller;

import com.alibaba.fastjson.JSONObject;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.util.AssertUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dataCollection/login")
public class LoginController {

    /**
     * 用户登录
     *
     * @param jsonObject {@link JSONObject}
     * @return {@link Result}
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/in")
    Result loginIn(@RequestBody JSONObject jsonObject) {
        // 获取当前用户主体
        Subject currentUser = SecurityUtils.getSubject();
        // 根据用户名和密码获取token认证信息
        UsernamePasswordToken token = new UsernamePasswordToken(jsonObject.getString("username"), jsonObject.getString("password"));
        // 是否自动登录
        if (jsonObject.containsKey("rememberMe")?jsonObject.getBoolean("rememberMe"):false) {
            token.setRememberMe(true);
        }
        // 执行登录认证, 这里会抛出权限异常, 需要做全局异常处理
        currentUser.login(token);
        // 判断是否认证成功
        AssertUtil.state(currentUser.isAuthenticated(), "登录状态无效");
        return Result.ok();
    }

    /**
     * 用户退出
     *
     * @return {@link Result}
     */
    @ApiOperation(value = "用户退出")
    @GetMapping("/out")
    Result loginOut() {
        // 获取当前用户主体
        Subject currentUser = SecurityUtils.getSubject();
        // 执行退出
        currentUser.logout();
        return Result.ok();
    }
}
