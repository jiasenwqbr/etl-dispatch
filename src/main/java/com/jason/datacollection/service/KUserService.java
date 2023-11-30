package com.jason.datacollection.service;

import com.jason.datacollection.entity.KUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface KUserService {
    void add(KUser KUser);
    void delete(String id);
    void deleteBatch(List<String> ids);
    void update(KUser usereq);
    PageInfo<KUser> findUserListByPage(KUser query, Integer page, Integer rows);
    KUser getUserDetail(String id);
    KUser getUserByAccount(String account);

}
