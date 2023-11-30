package com.jason.datacollection.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jason.datacollection.core.povo.Result;
import com.jason.datacollection.core.povo.TreeDTO;
import com.jason.datacollection.entity.KRepository;
import com.jason.datacollection.entity.QueryHelper;
import com.jason.datacollection.service.KRepositoryService;
import com.jason.datacollection.util.FileUtil;
import com.jason.datacollection.util.StringUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.RepositoryObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 资源库管理
 *
 * @author 陈钊
 */
@RestController
@RequestMapping("dataCollection/repository")
@Api(tags = "资源库管理")
public class RepositoryController {

    @Autowired
    private KRepositoryService repositoryService;

    /**
     * 添加资源库
     *
     * @param repository {@link KRepository}
     * @return {@link Result}
     */
    @PostMapping("add")
    @ApiOperation("添加资源库")
    public Result add(@RequestBody KRepository repository) {
        repository.setId(StringUtil.uuid());
        repository.setAddTime(new Date());
        // 添加资源库
        repository.setRepBasePath(FileUtil.replaceSeparator(repository.getRepBasePath()));
        repositoryService.add(repository);
        return Result.ok();
    }

    /**
     * 通过id删除资源库
     *
     * @param id 要删除的数据的id
     * @return {@link Result}
     */
    @PostMapping("delete")
    @ApiOperation("通过id删除资源库")
    public Result delete(@RequestParam String id) {
        repositoryService.delete(id);
        return Result.ok();
    }

    /**
     * 批量删除资源库
     *
     * @param ids 要删除数据的{@link List}集
     * @return {@link Result}
     */
    @PostMapping("deleteBatch")
    @ApiOperation("批量删除资源库")
    public Result deleteBatch(List<String> ids) {
        repositoryService.deleteBatch(ids);
        return Result.ok();
    }

    /**
     * 更新资源库
     *
     * @param repository {@link KRepository}
     * @return {@link Result}
     */
    @PostMapping("update")
    @ApiOperation("更新资源库")
    public Result update(@RequestBody KRepository repository) {
        // 修改
        repositoryService.update(repository);
        return Result.ok();
    }

    /**
     * 根据条件查询资源库列表
     *
     * @param kRepository
     * @return
     */
    @PostMapping("findRepListByPage")
    @ApiOperation("根据条件查询资源库列表")
    public Result<PageInfo<KRepository>> findRepListByPage(@RequestBody QueryHelper<KRepository> kRepository) {
        return Result.ok(repositoryService.findRepListByPage(kRepository.getQuery(), kRepository.getPage(), kRepository.getRows()));
    }

    /**
     * 查询资源库明细
     *
     * @param id 根据id查询
     * @return {@link Result}
     */
    @GetMapping("getRepositoryDetail")
    @ApiOperation("查询资源库明细")
    public Result<KRepository> getRepositoryDetail(String id) {
        return Result.ok(repositoryService.getRepositoryDetail(id));
    }

    /**
     * 资源库列表
     *
     * @return {@link Result}
     */
    @GetMapping("findRepList")
    @ApiOperation("资源库列表")
    public Result<List<KRepository>> findRepList() {
        return Result.ok(repositoryService.findRepList());
    }

    /**
     * 根据资源库id查询资源库中job作业内容树
     *

     */

    /**
     * @param id   根据id查询
     * @param type 类型：1 job；2trans
     * @return {@link Result}
     */
    @GetMapping("findJobRepTreeById")
    @ApiOperation("根据资源库id查询资源库中job作业内容树")
    public Result<List<TreeDTO<String>>> findJobRepTreeById(@RequestParam("id") String id, @RequestParam(value = "type", required = false) String type) throws Exception {
        List<TreeDTO<String>> repTree = null;
        if ("0".equals(type)) {
            repTree = repositoryService.findRepTreeById(id, RepositoryObjectType.JOB);
        } else if ("1".equals(type)) {
            repTree = repositoryService.findRepTreeById(id, RepositoryObjectType.TRANSFORMATION);
        } else {
            repTree = repositoryService.findRepTreeById(id, null);
        }
        return Result.ok(repTree);
    }


    /*******
     *  根据资源库id查询资源库中job作业内容树,根据树形表格来封装数据
     * @param id
     * @return
     */
    @GetMapping("findScriptRepTreegridById")
    @ApiOperation("根据资源库id查询资源库中job作业内容树,根据树形表格来封装数据")
    public Result<List<TreeDTO<String>>> findTransRepTreegridById(String id, @RequestParam(required = false, defaultValue = "/") String dirPath) throws Exception {
        return Result.ok(repositoryService.findTransRepTreegridById(id, dirPath));
    }

    /**
     * 测试资源库链接
     *
     * @param repository {@link KRepository}
     * @return {@link Result}
     */
    @PostMapping("testConnection")
    @ApiOperation("测试资源库链接")
    public Result testConnection(@RequestBody KRepository repository) {
        // 测试链接
        repositoryService.testConnection(repository);
        return Result.ok();
    }

    /**
     * 验证资源库名是否存在
     *
     * @param repId   资源库ID
     * @param repName 资源库名
     * @return 只能返回true或false
     */
    @PostMapping("repNameExist")
    public String repNameExist(String repId, String repName) {
        if (StringUtil.isEmpty(repName)) {
            return "true";
        } else {
            QueryWrapper<KRepository> repWrapper = new QueryWrapper<>();
            repWrapper.eq("REP_NAME", repName);
            KRepository rep = repositoryService.getOne(repWrapper);
            if (rep != null && !rep.getId().equals(repId)) {
                return "false";
            } else {
                return "true";
            }
        }
    }

    /**
     * 初始化资源库
     *
     * @param id 资源库ID
     */
    @GetMapping("/initRepository.do")
    String initRepository(@RequestParam("id") Integer id) {
        return null;
    }

    /****
     * 根据资源库id将资源库的信息初始化到数据库
     * @param id
     * @return
     */
    @ApiOperation(value = "根据资源库id将资源库的信息初始化到数据库")
    @GetMapping("/initTransRep")
    Result<List<TreeDTO<String>>> initTransRep(@RequestParam("id") String id, @RequestParam(name = "dirPath", defaultValue = "/") String dirPath) throws Exception {
        return Result.ok(repositoryService.initTransRep(id, dirPath));
    }

    /**
     * 根据资源库id获取资源库下数据库连接
     *
     * @param repId 资源库ID
     * @return
     * @throws KettleException
     */
    @ApiOperation(value = "根据资源库id获取资源库下数据库连接")
    @GetMapping("/getDatabasesByRepId")
    public Result<JSONArray> getDatabasesByRepId(@RequestParam("repId") String repId) throws Exception {
        return Result.ok(repositoryService.getDatabasesByRepId(repId));
    }

    /**
     * 在指定数据库创建资源库
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation(value = "创建一个新的资源库")
    @PostMapping("createRepository")
    public Result<Object> createRepository(@RequestBody JSONObject jsonObject) throws Exception {
        String result = repositoryService.createRepository(jsonObject);
        if (result != null) {
            KRepository kRepository = new KRepository();
            kRepository.setId(StringUtil.uuid());
            kRepository.setAddTime(new Date());
            kRepository.setDbHost(jsonObject.getString("hostname"));
            kRepository.setDbPort(jsonObject.getString("port"));
            kRepository.setDbType(jsonObject.getString("type"));
            kRepository.setDbName(jsonObject.getString("databaseName"));
            kRepository.setDbUsername(jsonObject.getString("username"));
            kRepository.setDbPassword(jsonObject.getString("password"));
            kRepository.setRepName(jsonObject.getString("name"));
            kRepository.setRepUsername("admin");
            kRepository.setRepPassword("admin");
            kRepository.setRepType("dbRep");
            kRepository.setDbAccess("Native");
            repositoryService.add(kRepository);
        }
        return Result.ok();
    }
}
