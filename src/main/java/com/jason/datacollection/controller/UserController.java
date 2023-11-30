package com.jason.datacollection.controller;

import com.jason.datacollection.core.exceptions.MyMessageException;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.entity.KUser;
import com.jason.datacollection.entity.QueryHelper;
import com.jason.datacollection.service.KUserService;
import com.jason.datacollection.util.StringUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理API
 *
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("dataCollection/user")
public class UserController {
    @Autowired
    KUserService kUserService;
    /**
     * 添加用户
     * @param KUser
     * @return
     */
    @ApiOperation(value = "添加用户")
    @PostMapping("/add.do")
    Result add(@RequestBody KUser KUser){
        kUserService.add(KUser);
        return Result.ok();
    }

    /**
     * 通过id删除用户
     *
     * @param id 要删除的数据的id
     * @return {@link Result}
     */
    @ApiOperation(value = "通过id删除用户")
    @DeleteMapping("/delete.do")
    Result delete(@RequestParam("id") String id){
        kUserService.delete(id);
        return Result.ok();
    }

    /**
     * 批量删除用户
     *
     * @param ids 要删除数据的{@link List}集
     * @return {@link Result}
     */
    @ApiOperation(value = "批量删除用户")
    @DeleteMapping("/deleteBatch.do")
    Result deleteBatch(@RequestBody List<String> ids){
        kUserService.deleteBatch(ids);
        return Result.ok();
    }

    /**
     * 更新用户
     * @param KUser
     * @return
     */
    @ApiOperation(value = "更新用户")
    @PutMapping("/update.do")
    Result update(@RequestBody KUser KUser){
        kUserService.update(KUser);
        return Result.ok();
    }

    /**
     * 根据条件查询用户列表
     *
     * @param req {@link QueryHelper}
     * @return {@link Result}
     */
    @ApiOperation(value = "根据条件查询用户列表")
    @PostMapping("/findUserListByPage.do")
    Result<PageInfo<KUser>> findUserListByPage(@RequestBody QueryHelper<KUser> req){
        return Result.ok(kUserService.findUserListByPage(req.getQuery(), req.getPage(),req.getRows()));

    }

    /**
     * 查询用户明细
     *
     * @param id 根据id查询
     * @return {@link Result}
     */
    @ApiOperation(value = "查询用户明细")
    @GetMapping("/getUserDetail.do")
    Result<KUser> getUserDetail(@RequestParam("id") String id){
        return Result.ok(kUserService.getUserDetail(id));
    }


    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return {@link Result}
     */
    @ApiOperation(value = "查询用户明细")
    @GetMapping("/getUserByUsername.do")
    Result<KUser> getUserByUsername(@RequestParam("username") String username){
        KUser KUser = kUserService.getUserByAccount(username);
        if (KUser != null) {
            return Result.ok(KUser);
        } else {
            throw new MyMessageException("用户信息不存在");
        }
    }

    /**
     * 验证账户是否存在
     *
     * @param username 账户名
     * @return 只能返回true或false
     */
    @ApiOperation(value = "验证账户是否存在")
    @PostMapping("/accountExist.do")
    String accountExist(String username){
        if (StringUtil.isEmpty(username)) {
            return "true";
        } else {
            KUser KUser = kUserService.getUserByAccount(username);
            if (KUser != null) {
                return "false";
            } else {
                return "true";
            }
        }
    }
}
