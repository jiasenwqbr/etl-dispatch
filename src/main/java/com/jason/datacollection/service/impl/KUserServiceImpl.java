package com.jason.datacollection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jason.datacollection.constant.Constant;
import com.jason.datacollection.core.exceptions.MyMessageException;
import com.jason.datacollection.entity.KUser;
import com.jason.datacollection.mapper.KUserMapper;
import com.jason.datacollection.service.KUserService;
import com.jason.datacollection.util.StringUtil;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class KUserServiceImpl implements KUserService {
    @Autowired
    KUserMapper kUserMapper;
    @Transactional(rollbackFor = Exception.class)
    public void add(KUser KUser) {
        // 密码加密
        KUser.setPassword(new Sha256Hash(KUser.getPassword(), Constant.salt, Constant.hashIterations).toString());
        KUser.setAddTime(new Date());
        KUser.setId(StringUtil.uuid());
        kUserMapper.insert(KUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        KUser KUser = kUserMapper.selectByPrimaryKey(id);
        if (KUser ==null) {
            throw new MyMessageException("删除对象不存在");
        }
        if ("admin".equals(KUser.getAccount())) {
            throw new MyMessageException("无法删除管理员账户");
        }
        kUserMapper.deleteByPrimaryKey(KUser.getId());
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<String> ids) {
        List<KUser> KUsers = kUserMapper.selectBatchIds(ids);
        List<KUser> collect = KUsers.stream().filter(user -> !"admin".equals(user.getAccount())).collect(Collectors.toList());
        kUserMapper.deleteBatchIds(collect);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(KUser usereq) {
        KUser KUser = kUserMapper.selectByPrimaryKey(usereq.getId());
        usereq.setEditTime(new Date());
        if (KUser !=null) {
            // 密码加密
            if (!StringUtil.isEmpty(usereq.getPassword())) {
                usereq.setPassword(new Sha256Hash(usereq.getPassword(), Constant.salt, Constant.hashIterations).toString());
            }
            kUserMapper.updateByPrimaryKeySelective(usereq);
        }
    }

    public PageInfo<KUser> findUserListByPage(KUser query, Integer page, Integer rows) {
//        // 排序
//        Sort sort = page.getSorts().isEmpty() ? Sort.by(Sort.Direction.DESC, "addTime") : page.getSorts();
//        // 查询
//        Page<KUser> pageList;
//        if (query != null) {
//            KUser user = BeanUtil.copyProperties(query, KUser.class);
//            ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreCase();
//            Example<KUser> example = Example.of(user, matcher);
//            pageList = userRepository.findAll(example, PageRequest.of(page.getNumber(), page.getSize(), sort));
//        } else {
//            pageList = userRepository.findAll(PageRequest.of(page.getNumber(), page.getSize(), sort));
//        }
//        // 封装数据
//        List<UserRes> collect = pageList.get().map(t -> BeanUtil.copyProperties(t, UserRes.class)).collect(Collectors.toList());
//        return new PageOut<>(collect, pageList.getNumber(), pageList.getSize(), pageList.getTotalElements());

        QueryWrapper<KUser> queryWrapper = new QueryWrapper<>();
        if ( query != null) {
            queryWrapper.orderByDesc("ADD_TIME");
        }
        List<KUser> list = kUserMapper.selectList(queryWrapper);
        PageInfo<KUser> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public KUser getUserDetail(String id) {
        KUser KUser =kUserMapper.selectById(id);
        if (KUser ==null){
            return null;
        }
        return KUser;
    }

    public KUser getUserByAccount(String account) {
        QueryWrapper<KUser> queryWrapper = new QueryWrapper<>();
        if ( account != null) {
            queryWrapper.eq("ACCOUNT",account);
        }
        return kUserMapper.selectOne(queryWrapper);
    }

    public static void main(String[] args) {
        System.out.println(new Sha256Hash("admin", "kettle-salt", 10).toString());
    }

}
