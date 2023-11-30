package com.jason.datacollection.core.repository;

import com.jason.datacollection.core.pool.RepositoryObject;
import com.jason.datacollection.core.pool.RepositoryObjectPool;
import com.jason.datacollection.service.impl.KRepositoryServiceImpl;
import com.jason.datacollection.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jason.datacollection.core.database.DatabaseCodec;
import com.jason.datacollection.entity.KRepository;
import com.jason.datacollection.core.enums.GlobalStatusEnum;
import com.jason.datacollection.util.*;
import lombok.extern.slf4j.Slf4j;
import com.jason.datacollection.core.exceptions.MyMessageException;
import com.jason.datacollection.core.povo.TreeDTO;
import com.jason.datacollection.core.enums.RepTypeEnum;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.ProgressNullMonitorListener;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.PartitionDatabaseMeta;
import org.pentaho.di.core.database.SqlScriptStatement;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.*;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.database.dialog.SQLEditor;
import org.pentaho.ui.database.Messages;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * kettle资源库工具类
 *
 * @author lyf
 */
@Slf4j
public class RepositoryUtil {
    /**
     * 资源库缓存
     */
    private static final Map<String, AbstractRepository> DATABASE_REP = new ConcurrentHashMap<>();


    /**
     * 日志对象接口
     */
    public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("DatabaseController", LoggingObjectType.DATABASE, null);


    /**
     * 连接到数据库资源库
     *
     * @param dbRep 连接参数
     */
    private static AbstractRepository databaseRepository(KRepository dbRep) {
        // 检查资源库是否存在
        if (dbRep.getId() != null && DATABASE_REP.containsKey(dbRep.getId())) {
            if (DATABASE_REP.get(dbRep.getId()).test()) {
                return DATABASE_REP.get(dbRep.getId());
            }
        }
        // 获取数据连接元
        DatabaseMeta dataMeta = new DatabaseMeta(dbRep.getDbName(), dbRep.getDbType(), dbRep.getDbAccess(), dbRep.getDbHost(), dbRep.getDbName(), dbRep.getDbPort(), dbRep.getDbUsername(), dbRep.getDbPassword());
        // 设置连接池
        dataMeta.setUsingConnectionPool(true);
        // 数据库资源库元
        KettleDatabaseRepositoryMeta drm = new KettleDatabaseRepositoryMeta();
        drm.setConnection(dataMeta);
        drm.setName(dbRep.getRepName());
        // 初始化并连接到数据库资源库
        KettleDatabaseRepository rep = new KettleDatabaseRepository();
        rep.init(drm);
        // 开始连接资源库
        try {
            rep.connect(dbRep.getRepUsername(), dbRep.getRepPassword());
        } catch (KettleException e) {
            String msg = "连接数据库资源库失败";
            log.error(msg, e);
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, msg);
        }
        // 缓存资源库信息
        if (dbRep.getId() != null) {
            DATABASE_REP.put(dbRep.getId(), rep);
        }
        return rep;
    }

    /**
     * 连接到文件资源库
     *
     * @param fileRep 连接参数
     */
    private static AbstractRepository fileRepository(KRepository fileRep) {
        // 检查资源库是否存在
        if (fileRep.getId() != null && DATABASE_REP.containsKey(fileRep.getId())) {
            return DATABASE_REP.get(fileRep.getId());
        }
        // 判断文件是否存在
        String baseDir = FileUtil.replaceSeparator(fileRep.getRepBasePath());
        if (StringUtil.isEmpty(baseDir) || !new File(baseDir).exists()) {
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, "文件资源库不存在");
        }
        // 文件资源库元数据
        KettleFileRepositoryMeta frm = new KettleFileRepositoryMeta();
        frm.setBaseDirectory(baseDir);
        frm.setName(fileRep.getRepName());
        // 初始化资源库
        KettleFileRepository rep = new KettleFileRepository();
        rep.init(frm);
        // 开始连接资源库
        try {
            rep.connect(fileRep.getRepUsername(), fileRep.getRepPassword());
        } catch (KettleException e) {
            String msg = "连接文件资源库失败";
            log.error(msg, e);
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, msg);
        }
        // 缓存资源库信息
        if (fileRep.getId() != null) {
            DATABASE_REP.put(fileRep.getId(), rep);
        }
        return rep;
    }

    /**
     * 连接资源库
     *
     * @param kRepository 连接参数
     */
    public static AbstractRepository connection(KRepository kRepository) {
        AbstractRepository repository = null;
        // 判断资源库是否在缓存中
        if (isExist(kRepository.getId())) {
            repository = getRepository(kRepository.getId());
            //判断资源库连接状态
            if (repository.isConnected()) {
                return repository;
            } else {
                log.warn("资源库链接已断开，即将进行重连！！！");
            }
        }
        repository = createConnection(kRepository);
        // 返回资源库
        return repository;
    }

    /**
     * 创建资源库链接
     *
     * @param kRepository
     * @return
     */
    public static AbstractRepository createConnection(KRepository kRepository) {
        // 不存在就创建资源库
        AbstractRepository repository = null;
        RepTypeEnum repTypeEnum = RepTypeEnum.getEnum(kRepository.getRepType());
        if (repTypeEnum != null) {
            switch (repTypeEnum) {
                case FILE:
                    repository = fileRepository(kRepository);
                    break;
                case DB:
                    repository = databaseRepository(kRepository);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + kRepository.getRepType());
            }
        }
        return repository;
    }

    /**
     * 批量连接资源库
     *
     * @param list 连接参数列表
     */
    public static void connectionBatch(List<KRepository> list) {
        list.forEach(RepositoryUtil::connection);
    }

    /**
     * 断开指定资源库
     *
     * @param repId 资源库ID
     */
    public static void disconnection(String repId) {
        // 检查资源库是否存在
        if (DATABASE_REP.containsKey(repId)) {
            AbstractRepository repository = DATABASE_REP.get(repId);
            // 断开连接
            repository.disconnect();
            repository.clearSharedObjectCache();
            // 清除缓存
            DATABASE_REP.remove(repId);
        }
    }

    /**
     * 断开所有资源库
     */
    public static void disconnectionAll() {
        if (CollectionUtil.isNotEmpty(DATABASE_REP)) {
            // 断开连接
            DATABASE_REP.values().forEach(repository -> {
                repository.disconnect();
                repository.clearSharedObjectCache();
            });
            // 清除缓存
            DATABASE_REP.clear();
        }
    }

    /**
     * 判断资源库是否连接成功
     *
     * @param repId 资源库ID
     * @return {@link Boolean}
     */
    public static boolean isExist(String repId) {
        return repId != null && DATABASE_REP.containsKey(repId);
    }

    /**
     * 通过资源库ID获取资源库
     *
     * @param repId 资源库ID
     * @return {@link AbstractRepository}
     */
    public static AbstractRepository getRepository(String repId) {
        return DATABASE_REP.getOrDefault(repId, null);
    }

    /**
     * 通过repId从连接池中获取资源库
     *
     * @param repId 资源库id
     * @return {@link AbstractRepository}
     */
    public static AbstractRepository getAbstractRepository(String repId) throws Exception {
        ThreadLocal<RepositoryObject> objectThreadLocal = new ThreadLocal<>();
        RepositoryObjectPool repositoryObjectPool = (RepositoryObjectPool) SpringContextUtil.getBean("RepositoryPool");
        RepositoryObject repositoryObject = repositoryObjectPool.borrowObject(repId);
        //链接资源库
        KettleDatabaseRepository repository = (KettleDatabaseRepository) repositoryObject.getRepository();
        //判断数据库连接是否断开
        if (repository.getDatabase().getConnection().isClosed()) {
            log.info("资源库已断开，正在重连。");
            repository.init(repository.getRepositoryMeta());
            KRepositoryServiceImpl kRepositoryServiceImpl = (KRepositoryServiceImpl) SpringContextUtil.getBean("KRepositoryServiceImpl");
            KRepository kRepository = kRepositoryServiceImpl.getRepositoryDetail(repId);
            repository.disconnect();
            repository.connect(kRepository.getRepUsername(), kRepository.getRepPassword());
            repositoryObject.setRepository(repository);
        }
        //将资源库对象放进本地线程变量
        objectThreadLocal.set(repositoryObject);
        //释放资源库对象
        repositoryObjectPool.returnObject(repId, objectThreadLocal.get());
        return repository;
    }


    /**
     * 通过资源库名称获取文件资源库
     *
     * @param repId 资源库ID
     * @return {@link KettleFileRepository}
     */
    public static KettleFileRepository getFileRepository(Integer repId) {
        if (!DATABASE_REP.containsKey(repId)) {
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, "资源库不存在");
        }

        if (DATABASE_REP.get(repId) instanceof KettleFileRepository) {
            return (KettleFileRepository) DATABASE_REP.get(repId);
        } else {
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, "资源库类型不匹配, 不是有效文件资源库");
        }
    }

    /**
     * 通过资源库名称获取数据库资源库
     *
     * @param repId 资源库ID
     * @return {@link KettleDatabaseRepository}
     */
    public static KettleDatabaseRepository getDatabaseRepository(Integer repId) {
        if (!DATABASE_REP.containsKey(repId)) {
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, "资源库不存在");
        }

        if (DATABASE_REP.get(repId) instanceof KettleDatabaseRepository) {
            return (KettleDatabaseRepository) DATABASE_REP.get(repId);
        } else {
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, "资源库类型不匹配, 不是有效数据库资源库");
        }
    }

    /**
     * 遍历获取资源库信息
     *
     * @param repository 资源库
     * @param dirPath    当前目录路径
     * @param objectType 查询指定脚本类型
     * @return {@link List}
     */
    public static List<TreeDTO<String>> getRepositoryTreeList(Repository repository, String dirPath, RepositoryObjectType objectType) {
        List<TreeDTO<String>> treeList = new ArrayList<>();
        try {
            if (!repository.isConnected()) {
                log.info("连接已断开");
            }
            // 获取当前的路径信息
            RepositoryDirectoryInterface rdi = repository.loadRepositoryDirectoryTree().findDirectory(dirPath);
            // 获取子目录信息
            List<TreeDTO<String>> dirTree = getSubdirectories(rdi);
            // 获取Job和Transformation的信息
            List<TreeDTO<String>> elementTree = getElementTree(repository, rdi, objectType);
            // 设置子级数据
            dirTree.forEach(dt -> {
                dt.setChildren(getRepositoryTreeList(repository, dt.getExtra(), objectType));
            });
            // 合并数据
            treeList.addAll(dirTree);
            treeList.addAll(elementTree);
        } catch (KettleException e) {
            String msg = "遍历资源库信息失败";
            log.error(msg, e);
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, msg);
        }
        return treeList;
    }

    /**
     * 遍历获取资源库信息(trans和job)
     *
     * @param repository 资源库
     * @param dirPath    当前目录路径
     * @return {@link List}
     */
    public static List<TreeDTO<String>> getRepositoryTreeListTj(Repository repository, String dirPath) {
        List<TreeDTO<String>> treeList = new ArrayList<>();
        try {
            // 获取当前的路径信息
            RepositoryDirectoryInterface rdi = repository.loadRepositoryDirectoryTree().findDirectory(dirPath);
            // 获取子目录信息
            List<TreeDTO<String>> dirTree = getSubdirectories(rdi);
            // 获取Job和Transformation的信息
            List<TreeDTO<String>> elementTree = getElementTreeAll(repository, rdi);
            // 设置子级数据
            dirTree.forEach(dt -> {
                dt.setChildren(getRepositoryTreeListTj(repository, dt.getExtra()));
            });
            // 合并数据
            treeList.addAll(dirTree);
            treeList.addAll(elementTree);
        } catch (KettleException e) {
            String msg = "遍历资源库信息失败";
            log.error(msg, e);
            throw new MyMessageException(GlobalStatusEnum.KETTLE_ERROR, msg);
        }
        return treeList;
    }

    /**
     * 获取当前目录下的子目录信息
     *
     * @param rdi 当前目录
     * @return {@link List}
     */
    public static List<TreeDTO<String>> getSubdirectories(RepositoryDirectoryInterface rdi) {
        List<TreeDTO<String>> treeList = new ArrayList<>();
        // 获取子目录的数量
        int subDirSize = rdi.getNrSubdirectories();
        if (subDirSize > 0) {
            for (int i = 0; i < subDirSize; i++) {
                RepositoryDirectory subDir = rdi.getSubdirectory(i);
                TreeDTO<String> tree = new TreeDTO<>();
                tree.setId(subDir.getObjectId().getId());
                tree.setText(subDir.getName());
                tree.setLeaf(false);
                tree.setExpand(true);
                tree.setExtra(subDir.getPath());
                String pid = subDir.getParent().getObjectId().toString();
                tree.setPid("0".equals(pid) ? null : pid);
                treeList.add(tree);
            }
        }
        return treeList;
    }

    /**
     * 获取资源库中的脚本详细信息
     *
     * @param rep          资源库对象
     * @param scriptPath   ktr所在路径
     * @param scriptName   ktr名称
     * @param versionLabel 版本号，传入null表示执行最新的ktr
     * @param type         脚本类型，trans：job
     */
    public static Object getScriptByRepository(AbstractRepository rep, String scriptPath, String scriptName, String versionLabel, String type) throws KettleException {
        Map<String, Object> map = new HashMap<>();
        JSONObject jsonObject = null;
        if (type.equals("trans")) {
            // 根据相对目录地址获取ktr所在目录信息
            RepositoryDirectoryInterface rdi = rep.loadRepositoryDirectoryTree().findDirectory(FileUtil.getParentPath(scriptPath));
            // 在指定资源库的目录下找到要执行的转换
            TransMeta tm = rep.loadTransformation(scriptName, rdi, new ProgressNullMonitorListener(), true, versionLabel);
            /*tm.getSteps().forEach(stepMeta -> {
                List<TransHopMeta> transHops = stepMeta.getParentTransMeta().getTransHops();
                List<StepMeta> steps = stepMeta.getParentTransMeta().getSteps();

            });*/
            jsonObject = TransFormUtil.dataTransform("trans", tm);
        } else if (type == "job") {

        }
        return jsonObject;
    }

    /**
     * 获取当前目录下的转换和作业
     *
     * @param repository 资源库
     * @param rdi        当前目录
     * @param objectType 查询指定脚本类型
     * @return {@link List}
     * @throws KettleException 异常
     */
    public static List<TreeDTO<String>> getElementTree(Repository repository, RepositoryDirectoryInterface rdi, RepositoryObjectType objectType) throws KettleException {
        List<TreeDTO<String>> treeList = new ArrayList<>();
        List<RepositoryElementMetaInterface> list = repository.getJobAndTransformationObjects(rdi.getObjectId(), false);
        if (null != list) {
            String rdiPath = rdi.getPath();
            list.forEach(element -> {
                if (objectType == null || objectType.equals(element.getObjectType())) {
                    TreeDTO<String> tree = new TreeDTO<>();
                    tree.setId(element.getObjectType().getTypeDescription() + "@" + rdi.getObjectId().getId() + "@" + element.getObjectId().getId());
                    //tree.setId(element.getObjectId().getId());
                    tree.setText(element.getName());
                    tree.setIcon("jstree-file");
                    tree.setLeaf(true);
                    tree.setExpand(false);
                    tree.setObjectType(element.getObjectType().getTypeDescription());
                    if (rdiPath.endsWith(FileUtil.separator)) {
                        tree.setExtra(rdiPath.concat(element.getName()));
                    } else {
                        tree.setExtra(rdiPath.concat(FileUtil.separator).concat(element.getName()));
                    }
                    treeList.add(tree);
                }
            });
        }
        return treeList;
    }

    /**
     * 获取当前目录下的转换和作业
     *
     * @param repository 资源库
     * @param rdi        当前目录
     * @return {@link List}
     * @throws KettleException 异常
     */
    private static List<TreeDTO<String>> getElementTreeAll(Repository repository, RepositoryDirectoryInterface rdi) throws KettleException {
        List<TreeDTO<String>> treeList = new ArrayList<>();
        List<RepositoryElementMetaInterface> list = repository.getJobAndTransformationObjects(rdi.getObjectId(), false);
        if (null != list) {
            String rdiPath = rdi.getPath();
            list.forEach(element -> {
                TreeDTO<String> tree = new TreeDTO<>();
                tree.setId(element.getObjectType().getTypeDescription() + "@" + rdi.getObjectId().getId() + "@" + element.getObjectId().getId());
                //tree.setId(element.getObjectId().getId());
                tree.setText(element.getName());
                tree.setIcon("jstree-file");
                tree.setLeaf(true);
                tree.setExpand(false);
                tree.setObjectType(element.getObjectType().getTypeDescription());
                if (rdiPath.endsWith(FileUtil.separator)) {
                    tree.setExtra(rdiPath.concat(element.getName()));
                } else {
                    tree.setExtra(rdiPath.concat(FileUtil.separator).concat(element.getName()));
                }
                treeList.add(tree);
            });
        }
        return treeList;
    }

    /***
     * 创建资源库流程：
     * 1、持久化数据库信息（承载资源库的数据库） function create（）
     * 2、根据数据库，初始化创库脚本          function initSQL（）
     * 3、执行数据库初始化SQL语句            function execute（）
     */

    /**
     * 持久化数据库信息
     * 新增资源库之前，需先持久化数据库
     *
     * @param databaseInfo //{"name":"test","type":"MYSQL","access":0,"hostname":"120.79.236.37","databaseName":"kettle-test","port":"3306","username":"kettle-test","password":"YwMTH7ZBKxsB6ePf","supportBooleanDataType":true,"supportTimestampDataType":true,"preserveReservedCaseCheck":true,"extraOptions":[],"usingConnectionPool":"N","initialPoolSize":"5","maximumPoolSize":"10","partitioned":"N","partitionInfo":[]}
     * @throws IOException
     * @throws KettleException
     */
    public static void create(JSONObject databaseInfo) throws IOException, KettleException {
        JSONObject result = new JSONObject();
        DatabaseMeta database = checkDatabase(databaseInfo, result);
        if (result.size() > 0) {
            //返回错误信息
            //JsonUtils.fail(result.toString());
            return;
        }

        RepositoriesMeta repositories = new RepositoriesMeta();
        if (repositories.readData()) {
            DatabaseMeta previousMeta = repositories.searchDatabase(database.getName());
            if (previousMeta != null) {
                repositories.removeDatabase(repositories.indexOfDatabase(previousMeta));
            }
            repositories.addDatabase(database);
            repositories.writeData();
            //返回正确信息
            //JsonUtils.success(database.getName());
        }
    }

    /**
     * 生成数据库初始化数据库脚本
     *
     * @param reposityInfo 资源库信息
     * @param upgrade      是否重建（true:已存在则重新创建，false:不重新创建）
     * @throws IOException
     * @throws KettleException
     */
    public static String initSQL(JSONObject reposityInfo, Boolean upgrade) throws IOException, KettleException {
        //url参数：reposityInfo={"name":"2","description":"3","type":"KettleDatabaseRepository","extraOptions":{"database":"kettle-test\t"}}&upgrade=false
        //JSONObject jsonObject = JSONObject.parseObject(reposityInfo);
        StringBuffer sql = new StringBuffer();
        KettleDatabaseRepositoryMeta repositoryMeta = (KettleDatabaseRepositoryMeta) RepositoryUtil.decode(reposityInfo);
        if (repositoryMeta.getConnection() != null) {
            KettleDatabaseRepository rep = (KettleDatabaseRepository) PluginRegistry.getInstance().loadClass(RepositoryPluginType.class, repositoryMeta, Repository.class);
            rep.init(repositoryMeta);

            ArrayList<String> statements = new ArrayList<String>();
            rep.connectionDelegate.connect(true, true);
            rep.createRepositorySchema(null, upgrade, statements, true);


            sql.append("-- Repository creation/upgrade DDL: ").append(Const.CR);
            sql.append("--").append(Const.CR);
            sql.append("-- Nothing was created nor modified in the target repository database.").append(Const.CR);
            sql.append("-- Hit the OK button to execute the generated SQL or Close to reject the changes.").append(Const.CR);
            sql.append("-- Please note that it is possible to change/edit the generated SQL before execution.").append(Const.CR);
            sql.append("--").append(Const.CR);
            for (String statement : statements) {
                statement = statement.replace("'N'", "'0'");
                statement = statement.replace("'Y'", "'1'");
                if (statement.endsWith(";")) {
                    //mysql没有BOOLEAN,会自动转换成tinyint类型，所以需要在插入的时候，将N,Y转换
                    sql.append(statement).append(Const.CR);
                } else {
                    sql.append(statement).append(";").append(Const.CR).append(Const.CR);
                }
            }
        }
        //log.info(sql.toString());
        //返回SQL
        return sql.toString();
        //JsonUtils.success(StringEscapeHelper.encode());
    }

    /***
     * 创建资源库，执行创表语句
     * @param reposityInfo  initSQL方法中的同名参数
     * @param script        initSQL方法中返回值，SQL语句
     * @throws Exception
     */
    public static String execute(JSONObject reposityInfo, String script) throws Exception {
        //JSONObject jsonObject = JSONObject.parseObject(reposityInfo);
        String result = "";
        KettleDatabaseRepositoryMeta repositoryMeta = (KettleDatabaseRepositoryMeta) RepositoryUtil.decode(reposityInfo);
        if (repositoryMeta.getConnection() != null) {
            KettleDatabaseRepository rep = (KettleDatabaseRepository) PluginRegistry.getInstance().loadClass(RepositoryPluginType.class, repositoryMeta, Repository.class);
            rep.init(repositoryMeta);

            DatabaseMeta connection = repositoryMeta.getConnection();
            StringBuffer message = new StringBuffer();

            Database db = new Database(loggingObject, connection);
            boolean first = true;
            PartitionDatabaseMeta[] partitioningInformation = connection.getPartitioningInformation();

            for (int partitionNr = 0; first
                    || (partitioningInformation != null && partitionNr < partitioningInformation.length); partitionNr++) {
                first = false;
                String partitionId = null;
                if (partitioningInformation != null && partitioningInformation.length > 0) {
                    partitionId = partitioningInformation[partitionNr].getPartitionId();
                }
                try {
                    db.connect(partitionId);
                    List<SqlScriptStatement> statements = connection.getDatabaseInterface().getSqlScriptStatements(StringEscapeHelper.decode(script));

                    int nrstats = 0;
                    for (SqlScriptStatement sql : statements) {
                        if (sql.isQuery()) {
                            // A Query

                            nrstats++;
                            List<Object[]> rows = db.getRows(sql.getStatement(), 1000);
                            RowMetaInterface rowMeta = db.getReturnRowMeta();
                            if (rows.size() > 0) {
//	                      PreviewRowsDialog prd =
//	                        new PreviewRowsDialog( shell, ci, SWT.NONE, BaseMessages.getString(
//	                          PKG, "SQLEditor.ResultRows.Title", Integer.toString( nrstats ) ), rowMeta, rows );
//	                      prd.open();
                            } else {
//	                      MessageBox mb = new MessageBox( shell, SWT.ICON_INFORMATION | SWT.OK );
//	                      mb.setMessage( BaseMessages.getString( PKG, "SQLEditor.NoRows.Message", sql ) );
//	                      mb.setText( BaseMessages.getString( PKG, "SQLEditor.NoRows.Title" ) );
//	                      mb.open();
                            }
                        } else {

                            // A DDL statement
                            nrstats++;
                            int startLogLine = KettleLogStore.getLastBufferLineNr();
                            try {

                                db.execStatement(sql.getStatement());

                                message.append(BaseMessages.getString(SQLEditor.class, "SQLEditor.Log.SQLExecuted", sql));
                                message.append(Const.CR);

                                // Clear the database cache, in case we're using one...
                                if (DBCache.getInstance() != null) {
                                    DBCache.getInstance().clear(connection.getName());
                                }

                                // mark the statement in green in the dialog...
                                //
                                sql.setOk(true);
                            } catch (Exception dbe) {
                                sql.setOk(false);
                                String error = BaseMessages.getString(SQLEditor.class, "SQLEditor.Log.SQLExecError", sql, dbe.toString());
                                message.append(error).append(Const.CR);
                            } finally {
                                int endLogLine = KettleLogStore.getLastBufferLineNr();
                                sql.setLoggingText(KettleLogStore.getAppender().getLogBufferFromTo(
                                        db.getLogChannelId(), true, startLogLine, endLogLine).toString());
                                sql.setComplete(true);
                            }
                        }
                    }

                    message.append(BaseMessages.getString(SQLEditor.class, "SQLEditor.Log.StatsExecuted", Integer.toString(nrstats)));
                    if (partitionId != null) {
                        message.append(BaseMessages.getString(SQLEditor.class, "SQLEditor.Log.OnPartition", partitionId));
                    }
                    message.append(Const.CR);
                } finally {
                    db.disconnect();
                }
            }
            result = message.toString();
        }
        return result;

    }

    public static JSONObject encode(RepositoryMeta repositoryMeta) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", repositoryMeta.getName());
        jsonObject.put("description", repositoryMeta.getDescription());
        jsonObject.put("type", repositoryMeta.getId());

        if (repositoryMeta instanceof KettleDatabaseRepositoryMeta) {
            KettleDatabaseRepositoryMeta input = (KettleDatabaseRepositoryMeta) repositoryMeta;
            JSONObject extraOptions = new JSONObject();
            extraOptions.put("database", input.getConnection().getName());
            jsonObject.put("extraOptions", extraOptions);
        } else if (repositoryMeta instanceof KettleFileRepositoryMeta) {
            KettleFileRepositoryMeta input = (KettleFileRepositoryMeta) repositoryMeta;

            JSONObject extraOptions = new JSONObject();
            extraOptions.put("basedir", input.getBaseDirectory());
            extraOptions.put("hidingHidden", input.isHidingHiddenFiles() ? "Y" : "N");
            extraOptions.put("readOnly", input.isReadOnly() ? "Y" : "N");
            jsonObject.put("extraOptions", extraOptions);
        }

        return jsonObject;
    }

    public static RepositoryMeta decode(JSONObject jsonObject) throws KettleException {
        String id = jsonObject.containsKey("type") ? jsonObject.getString("type") : "";
        RepositoryMeta repositoryMeta = PluginRegistry.getInstance().loadClass(RepositoryPluginType.class, id, RepositoryMeta.class);
        repositoryMeta.setName(jsonObject.containsKey("name") ? jsonObject.getString("name") : "");
        repositoryMeta.setDescription(jsonObject.containsKey("description") ? jsonObject.getString("description") : "");

        if (repositoryMeta instanceof KettleDatabaseRepositoryMeta) {
            KettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta = (KettleDatabaseRepositoryMeta) repositoryMeta;

            RepositoriesMeta input = new RepositoriesMeta();
            if (input.readData()) {
                JSONObject extraOptions = jsonObject.containsKey("extraOptions") ? jsonObject.getJSONObject("extraOptions") : null;
                DatabaseMeta connection = input.searchDatabase(extraOptions.containsKey("database") ? extraOptions.getString("database") : null);
                kettleDatabaseRepositoryMeta.setConnection(connection);
            }
        } else if (repositoryMeta instanceof KettleFileRepositoryMeta) {
            KettleFileRepositoryMeta input = (KettleFileRepositoryMeta) repositoryMeta;

            JSONObject extraOptions = jsonObject.containsKey("extraOptions") ? jsonObject.getJSONObject("extraOptions") : null;
            input.setBaseDirectory(extraOptions.containsKey("hidingHidden") ? extraOptions.getString("basedir") : "");
            input.setReadOnly("Y".equalsIgnoreCase(extraOptions.containsKey("readOnly") ? extraOptions.getString("readOnly") : null));
            input.setHidingHiddenFiles("Y".equalsIgnoreCase(extraOptions.containsKey("hidingHidden") ? extraOptions.getString("hidingHidden") : null));
        }

        return repositoryMeta;
    }

    public static DatabaseMeta checkDatabase(JSONObject databaseInfo, JSONObject result) throws KettleDatabaseException, IOException {
        DatabaseMeta database = DatabaseCodec.decode(databaseInfo);

        if (database.isUsingConnectionPool()) {
            String parameters = "";
            JSONArray pool_params = databaseInfo.getJSONArray("pool_params");
            if (pool_params != null) {
                for (int i = 0; i < pool_params.size(); i++) {
                    JSONObject jsonObject2 = pool_params.getJSONObject(i);
                    Boolean enabled = jsonObject2.getBoolean("enabled");
                    String parameter = jsonObject2.getString("name");
                    String value = jsonObject2.getString("defValue");

                    if (!enabled) {
                        continue;
                    }

                    if (!StringUtils.hasText(value)) {
                        parameters = parameters.concat(parameter).concat(System.getProperty("line.separator"));
                    }
                }

            }

            if (parameters.length() > 0) {
                String message = Messages.getString("DataHandler.USER_INVALID_PARAMETERS").concat(parameters);
                result.put("message", message);
                return database;
            }
        }

        String[] remarks = database.checkParameters();
        String message = "";

        if (remarks.length != 0) {
            for (int i = 0; i < remarks.length; i++) {
                message = message.concat("* ").concat(remarks[i]).concat(System.getProperty("line.separator"));
            }
            result.put("message", message);

            return database;
        }

        return database;
    }

    public static JSONArray getDatabases(Repository repository) throws KettleException {
        List<DatabaseMeta> databaseMetaList = null;
        JSONArray jsonArray = new JSONArray();
        if (repository instanceof KettleDatabaseRepository) {
            KettleDatabaseRepository kettleDatabaseRepository = (KettleDatabaseRepository) repository;
            databaseMetaList = kettleDatabaseRepository.getDatabases();
            databaseMetaList.forEach(databaseMeta -> {
                jsonArray.add(DatabaseCodec.encode(databaseMeta));
            });
        }
        return jsonArray;
    }
}
