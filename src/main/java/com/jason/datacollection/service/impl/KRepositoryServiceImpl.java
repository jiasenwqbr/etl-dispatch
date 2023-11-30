package com.jason.datacollection.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jason.datacollection.core.enums.GlobalStatusEnum;
import com.jason.datacollection.core.exceptions.MyMessageException;
import com.jason.datacollection.core.povo.TreeDTO;
import com.jason.datacollection.core.repository.RepositoryUtil;
import com.jason.datacollection.entity.DiCanstant;
import com.jason.datacollection.entity.DiCategory;
import com.jason.datacollection.entity.DiScript;
import com.jason.datacollection.entity.KRepository;
import com.jason.datacollection.mapper.DiCategoryMapper;
import com.jason.datacollection.mapper.DiScriptMapper;
import com.jason.datacollection.mapper.KRepositoryMapper;
import com.jason.datacollection.service.KRepositoryService;
import com.jason.datacollection.util.StringUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KRepositoryServiceImpl extends ServiceImpl<KRepositoryMapper, KRepository> implements KRepositoryService {


    @Autowired
    private KRepositoryMapper repRepository;
    @Autowired
    private DiScriptMapper diScriptMapper;
    @Autowired
    private DiCategoryMapper diCategoryMapper;


    public void add(KRepository repository) {
        repRepository.insert(repository);
    }

    public void delete(String id) {
        repRepository.deleteById(id);
    }

    public void deleteBatch(List<String> ids) {
        repRepository.deleteBatchIds(ids);
    }

    public void update(KRepository repository) {
        repRepository.updateById(repository);
    }

    public PageInfo<KRepository> findRepListByPage(KRepository repository, Integer page, Integer rows) {
        QueryWrapper<KRepository> queryWrapper = new QueryWrapper<>();
        if (repository != null) {
            queryWrapper.orderByDesc("edit_time");
            queryWrapper.eq("rep_name", repository.getRepName());
        }
        List<KRepository> kRepositories = repRepository.selectList(queryWrapper);
        PageInfo<KRepository> pageInfo = new PageInfo<>(kRepositories);
        return pageInfo;
        /*Page<KRepository> result = repRepository.selectPage(new Page<KRepository>(page, rows), queryWrapper);
        return result;*/
    }

    public KRepository getRepositoryDetail(String id) {
        return repRepository.selectById(id);
    }

    public List<KRepository> findRepList() {
        return repRepository.selectList(new QueryWrapper<>());
    }

    public List<TreeDTO<String>> findRepTreeById(String id, RepositoryObjectType objectType) throws Exception {
        KRepository kRepository = repRepository.selectById(id);
        if (kRepository != null) {
            // 连接资源库
            AbstractRepository repository = RepositoryUtil.getAbstractRepository(id);
            // 遍历获取资源库信息
            return RepositoryUtil.getRepositoryTreeList(repository, "/", objectType);
        } else {
            return null;
        }
    }


    public List<TreeDTO<String>> findTransRepTreegridById(String id, String dirPath) throws Exception {
        AbstractRepository repository = RepositoryUtil.getAbstractRepository(id);
        // 遍历获取资源库信息
        List<TreeDTO<String>> repositoryTreeList = RepositoryUtil.getRepositoryTreeListTj(repository, dirPath);
        //repository.disconnect();
        return dataFormat(repositoryTreeList);
        //return repositoryTreeList;
    }

    public void testConnection(KRepository repository) {
        // 连接资源库
        AbstractRepository abstractRepository = RepositoryUtil.connection(repository);
        // 判断是否链接成功
        if (abstractRepository == null || !abstractRepository.isConnected()) {
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, "链接资源库失败");
        }
    }

    /*public KRepository getByRepName(String repName) {
        QueryWrapper<KRepository> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("NAME", repName);
        List<KRepository> userList = repRepository.selectList(queryWrapper);
    }*/


    /**
     * 数据格式转化
     *
     * @param repositoryTreeList
     */
    private List<TreeDTO<String>> dataFormat(List<TreeDTO<String>> repositoryTreeList) {
        List<TreeDTO<String>> resultList = new ArrayList<>();
        repositoryTreeList.forEach(treeDTO -> {
            String id = treeDTO.getId();
            List<TreeDTO<String>> children = treeDTO.getChildren();
            if (children != null) {
                children.forEach(childrenTree -> {
                    String id1 = childrenTree.getId();
                    if (childrenTree.getChildren() != null) {
                        //末级脚本
                        childrenTree.getChildren().forEach(childrens -> {
                            childrens.setPid(id1);
                            childrens.setChildren(null);
                            resultList.add(childrens);
                        });
                    }
                    childrenTree.setChildren(null);
                    resultList.add(childrenTree);
                    childrenTree.setPid(id);
                });
            }
            treeDTO.setChildren(null);
            resultList.add(treeDTO);
        });
        return resultList;
    }

    public List<TreeDTO<String>> initTransRep(String id, String dirPath) throws Exception {
        KRepository kRepository = repRepository.selectById(id);
        if (kRepository != null) {
            AbstractRepository repository = RepositoryUtil.getAbstractRepository(id);
            if (!repository.isConnected()) {
                try {
                    repository.connect(kRepository.getRepUsername(), kRepository.getRepPassword());
                } catch (KettleException e) {
                    e.printStackTrace();
                }
            }
            // 初始化资源库
            try {
                List<String> collect = Arrays.asList(DiCanstant.REPOSITORY_CONSTANT);
                RepositoryDirectoryInterface rdi = repository.loadRepositoryDirectoryTree().findDirectory(dirPath);
                collect.forEach(s -> {
                    RepositoryDirectoryInterface directory = rdi.findDirectory(s);
                    if (directory == null) {
                        try {
                            repository.createRepositoryDirectory(rdi, s);
                        } catch (KettleException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // 遍历获取资源库信息
                List<TreeDTO<String>> repositoryTreeList = RepositoryUtil.getRepositoryTreeListTj(repository, dirPath);
                repositoryTreeList.forEach(sl -> {
                    dataRepositoryFormat(sl, sl.getId());
                });
                List<DiCategory> categoryList = new ArrayList<>();
                List<DiScript> scripts = new ArrayList<>();
                for (int i = 0; i < repositoryTreeList.size(); i++) {
                    TreeDTO<String> t = repositoryTreeList.get(i);
                    categoryList.addAll(getCategoryChild(t, collect, id, String.format("%03d", i + 1)));
                }
                repositoryTreeList.forEach(t -> {
                    scripts.addAll(getScriptChild(t, collect, id));
                });
                // 先清除对应repId的数据
//                List<DiCategory> byRepId = diCategoryMapper.findByRepIdOrderByCode(String.valueOf(id));
                diCategoryMapper.deleteByRepId(String.valueOf(id));
//                List<DiScript> scriptList = diScriptMapper.findByRepId(String.valueOf(id));
                diScriptMapper.deleteByRepId(String.valueOf(id));
                // 将资源库的脚本导入数据库
                diCategoryMapper.insertAll(categoryList);
                if(scripts.size()>0){
                    diScriptMapper.insertAll(scripts);
                }
                //repository.disconnect();
                List<TreeDTO<String>> treeDTOS1 = dataFormat(repositoryTreeList);
                return dataFormat(repositoryTreeList);
            } catch (Exception e) {
                String msg = "初始化资源库信息失败";
                e.printStackTrace();
                throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, msg);
            }
            //return repositoryTreeList;
        } else {
            return null;
        }
    }

    @Override
    public JSONArray getDatabasesByRepId(String id) throws Exception {
        AbstractRepository repository = RepositoryUtil.getAbstractRepository(id);
        return RepositoryUtil.getDatabases(repository);
    }

    @Override
    public List<String> selectAllRep() {
        return repRepository.selectAllRep();
    }

    @Override
    public String createRepository(JSONObject jsonObject) throws Exception {
        //持久化数据库，将数据库连接信息创建到服务器端
        RepositoryUtil.create(jsonObject);
        JSONObject reposityInfo = new JSONObject();
        reposityInfo.put("name", jsonObject.getString("name"));
        reposityInfo.put("description", "default");
        reposityInfo.put("type", "KettleDatabaseRepository");//固定为创建数据库资源库
        JSONObject extraOptions = new JSONObject();
        //System.out.println(jsonObject.getString("name"));
        extraOptions.put("database", jsonObject.getString("name"));
        reposityInfo.put("extraOptions", extraOptions);
        //初始化建表语句
        String createTableSQL = RepositoryUtil.initSQL(reposityInfo, false);
        //执行建表语句
        String executeResult = RepositoryUtil.execute(reposityInfo, createTableSQL);
        //System.out.println(executeResult);
        return executeResult;
    }

    /**
     * 数据格式转化
     *
     * @param tList
     */
    private static void dataRepositoryFormat(TreeDTO<String> tList, String id) {
        List<TreeDTO<String>> children = tList.getChildren();
        if (children != null) {
            children.forEach(childrenTree -> {
                childrenTree.setPid(id);
                dataRepositoryFormat(childrenTree, childrenTree.getId());
            });

        }

    }

    private static List<DiCategory> getCategoryChild(TreeDTO<String> t, List<String> collect, String id, String s) {
        List<DiCategory> cs = new ArrayList<>();
        if (t.getIcon() == null) {
            DiCategory d = new DiCategory();
            d.setCode(s);
            diFormate(d, t, collect, id);
            cs.add(d);
            if (t.getChildren() != null) {
                List<TreeDTO<String>> children = t.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    TreeDTO<String> c = children.get(i);
                    cs.addAll(getCategoryChild(c, collect, id, s + String.format("%03d", i + 1)));
                }
            }
        }
        return cs;
    }

    private static void diFormate(DiCategory d, TreeDTO t, List<String> s, String id) {
        if (Objects.nonNull(t)) {
            d.setId(StringUtil.uuid());
            d.setCategoryId(t.getId());
            d.setCategoryPid(t.getPid());
            d.setName(t.getText());
            d.setRepId(id + "");
            d.setPath(t.getExtra() + "");
            if (s.contains(t.getText()) && StringUtils.isEmpty(t.getPid())) {
                d.setIsDefault("1");
            } else {
                d.setIsDefault("0");
            }
        }
    }

    private static List<DiScript> getScriptChild(TreeDTO<String> t, List<String> collect, String id) {
        List<DiScript> cs = new ArrayList<>();
        if (t.getIcon() != null) {
            DiScript d = new DiScript();
            scriptFormate(d, t, collect, id);
            cs.add(d);
        }
        if (t.getChildren() != null) {
            t.getChildren().forEach(c -> {
                cs.addAll(getScriptChild(c, collect, id));
            });
        }
        return cs;
    }

    private static void scriptFormate(DiScript d, TreeDTO t, List<String> s, String id) {
        if (Objects.nonNull(t)) {
            d.setCreateDate(new Date());
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replaceAll("-", "");
            d.setId(uuid);
            d.setPath(t.getExtra() + "");
            String scriptType = "0";
            if (t.getObjectType().equals("transformation")) {
                scriptType = "1";
            }
            d.setType(scriptType);
            d.setRepId(id + "");
            d.setName(t.getText());
            String id1 = t.getId();
            String substring = id1.substring(id1.indexOf("@") + 1, id1.lastIndexOf("@"));
            String substring1 = id1.substring(id1.lastIndexOf("@") + 1, id1.length());
            d.setCategoryId(t.getPid());
            d.setScriptId(substring1);
        }
    }

    /**
     * 根据资源库ID，返回资源库连接对象
     *
     * @param id
     * @return
     */
    public AbstractRepository getRepositoryById(String id) {
        KRepository kRepository = repRepository.selectById(id);
        AbstractRepository repository = null;
        if (kRepository != null) {
            // 连接资源库
            repository = RepositoryUtil.connection(kRepository);
            if (!repository.isConnected()) {
                try {
                    repository.connect(kRepository.getRepUsername(), kRepository.getRepPassword());
                } catch (KettleException e) {
                    e.printStackTrace();
                }
            }
        }
        return repository;
    }


}
