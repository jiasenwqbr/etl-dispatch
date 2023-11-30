package com.jason.datacollection.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleSecurityException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.partition.PartitionSchema;
import org.pentaho.di.repository.*;
import org.pentaho.di.shared.SharedObjects;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@TableName("k_repository")
public class KRepository extends Model<KRepository> implements Repository {

    private static final long serialVersionUID = 1L;

    @TableField("ID")
    private String id;

    @TableField("rep_name")
    private String repName;

    @TableField("rep_type")
    private String repType;

    @TableField("rep_username")
    private String repUsername;

    @TableField("rep_password")
    private String repPassword;

    @TableField("rep_base_path")
    private String repBasePath;

    @TableField("db_type")
    private String dbType;

    @TableField("db_access")
    private String dbAccess;

    @TableField("db_host")
    private String dbHost;

    @TableField("db_port")
    private String dbPort;

    @TableField("db_name")
    private String dbName;

    @TableField("db_username")
    private String dbUsername;

    @TableField("db_password")
    private String dbPassword;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("add_time")
    private Date addTime;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("add_user")
    private String addUser;

    @TableField("edit_time")
    private Date editTime;

    @TableField("edit_user")
    private String editUser;

    @TableField("del_flag")
    private String delFlag;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public RepositoryMeta getRepositoryMeta() {
        return null;
    }

    @Override
    public IUser getUserInfo() {
        return null;
    }

    @Override
    public RepositorySecurityProvider getSecurityProvider() {
        return null;
    }

    @Override
    public RepositorySecurityManager getSecurityManager() {
        return null;
    }

    @Override
    public LogChannelInterface getLog() {
        return null;
    }

    @Override
    public void connect(String s, String s1) throws KettleException, KettleSecurityException {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void init(RepositoryMeta repositoryMeta) {

    }

    @Override
    public boolean exists(String s, RepositoryDirectoryInterface repositoryDirectoryInterface, RepositoryObjectType repositoryObjectType) throws KettleException {
        return false;
    }

    @Override
    public ObjectId getTransformationID(String s, RepositoryDirectoryInterface repositoryDirectoryInterface) throws KettleException {
        return null;
    }

    @Override
    public ObjectId getJobId(String s, RepositoryDirectoryInterface repositoryDirectoryInterface) throws KettleException {
        return null;
    }

    @Override
    public void save(RepositoryElementInterface repositoryElementInterface, String s, ProgressMonitorListener progressMonitorListener) throws KettleException {

    }

    @Override
    public void save(RepositoryElementInterface repositoryElementInterface, String s, ProgressMonitorListener progressMonitorListener, boolean b) throws KettleException {

    }

    @Override
    public void save(RepositoryElementInterface repositoryElementInterface, String s, Calendar calendar, ProgressMonitorListener progressMonitorListener, boolean b) throws KettleException {

    }

    @Override
    public RepositoryDirectoryInterface getDefaultSaveDirectory(RepositoryElementInterface repositoryElementInterface) throws KettleException {
        return null;
    }

    @Override
    public RepositoryDirectoryInterface getUserHomeDirectory() throws KettleException {
        return null;
    }

    @Override
    public void clearSharedObjectCache() {

    }

    @Override
    public TransMeta loadTransformation(String s, RepositoryDirectoryInterface repositoryDirectoryInterface, ProgressMonitorListener progressMonitorListener, boolean b, String s1) throws KettleException {
        return null;
    }

    @Override
    public TransMeta loadTransformation(ObjectId objectId, String s) throws KettleException {
        return null;
    }

    @Override
    public SharedObjects readTransSharedObjects(TransMeta transMeta) throws KettleException {
        return null;
    }

    @Override
    public ObjectId renameTransformation(ObjectId objectId, RepositoryDirectoryInterface repositoryDirectoryInterface, String s) throws KettleException {
        return null;
    }

    @Override
    public ObjectId renameTransformation(ObjectId objectId, String s, RepositoryDirectoryInterface repositoryDirectoryInterface, String s1) throws KettleException {
        return null;
    }

    @Override
    public void deleteTransformation(ObjectId objectId) throws KettleException {

    }

    @Override
    public JobMeta loadJob(String s, RepositoryDirectoryInterface repositoryDirectoryInterface, ProgressMonitorListener progressMonitorListener, String s1) throws KettleException {
        return null;
    }

    @Override
    public JobMeta loadJob(ObjectId objectId, String s) throws KettleException {
        return null;
    }

    @Override
    public SharedObjects readJobMetaSharedObjects(JobMeta jobMeta) throws KettleException {
        return null;
    }

    @Override
    public ObjectId renameJob(ObjectId objectId, String s, RepositoryDirectoryInterface repositoryDirectoryInterface, String s1) throws KettleException {
        return null;
    }

    @Override
    public ObjectId renameJob(ObjectId objectId, RepositoryDirectoryInterface repositoryDirectoryInterface, String s) throws KettleException {
        return null;
    }

    @Override
    public void deleteJob(ObjectId objectId) throws KettleException {

    }

    @Override
    public DatabaseMeta loadDatabaseMeta(ObjectId objectId, String s) throws KettleException {
        return null;
    }

    @Override
    public void deleteDatabaseMeta(String s) throws KettleException {

    }

    @Override
    public ObjectId[] getDatabaseIDs(boolean b) throws KettleException {
        return new ObjectId[0];
    }

    @Override
    public String[] getDatabaseNames(boolean b) throws KettleException {
        return new String[0];
    }

    @Override
    public List<DatabaseMeta> readDatabases() throws KettleException {
        return null;
    }

    @Override
    public ObjectId getDatabaseID(String s) throws KettleException {
        return null;
    }

    @Override
    public ClusterSchema loadClusterSchema(ObjectId objectId, List<SlaveServer> list, String s) throws KettleException {
        return null;
    }

    @Override
    public ObjectId[] getClusterIDs(boolean b) throws KettleException {
        return new ObjectId[0];
    }

    @Override
    public String[] getClusterNames(boolean b) throws KettleException {
        return new String[0];
    }

    @Override
    public ObjectId getClusterID(String s) throws KettleException {
        return null;
    }

    @Override
    public void deleteClusterSchema(ObjectId objectId) throws KettleException {

    }

    @Override
    public SlaveServer loadSlaveServer(ObjectId objectId, String s) throws KettleException {
        return null;
    }

    @Override
    public ObjectId[] getSlaveIDs(boolean b) throws KettleException {
        return new ObjectId[0];
    }

    @Override
    public String[] getSlaveNames(boolean b) throws KettleException {
        return new String[0];
    }

    @Override
    public List<SlaveServer> getSlaveServers() throws KettleException {
        return null;
    }

    @Override
    public ObjectId getSlaveID(String s) throws KettleException {
        return null;
    }

    @Override
    public void deleteSlave(ObjectId objectId) throws KettleException {

    }

    @Override
    public PartitionSchema loadPartitionSchema(ObjectId objectId, String s) throws KettleException {
        return null;
    }

    @Override
    public ObjectId[] getPartitionSchemaIDs(boolean b) throws KettleException {
        return new ObjectId[0];
    }

    @Override
    public String[] getPartitionSchemaNames(boolean b) throws KettleException {
        return new String[0];
    }

    @Override
    public ObjectId getPartitionSchemaID(String s) throws KettleException {
        return null;
    }

    @Override
    public void deletePartitionSchema(ObjectId objectId) throws KettleException {

    }

    @Override
    public RepositoryDirectoryInterface loadRepositoryDirectoryTree() throws KettleException {
        return null;
    }

    @Override
    public RepositoryDirectoryInterface findDirectory(String s) throws KettleException {
        return null;
    }

    @Override
    public RepositoryDirectoryInterface findDirectory(ObjectId objectId) throws KettleException {
        return null;
    }

    @Override
    public void saveRepositoryDirectory(RepositoryDirectoryInterface repositoryDirectoryInterface) throws KettleException {

    }

    @Override
    public void deleteRepositoryDirectory(RepositoryDirectoryInterface repositoryDirectoryInterface) throws KettleException {

    }

    @Override
    public ObjectId renameRepositoryDirectory(ObjectId objectId, RepositoryDirectoryInterface repositoryDirectoryInterface, String s) throws KettleException {
        return null;
    }

    @Override
    public RepositoryDirectoryInterface createRepositoryDirectory(RepositoryDirectoryInterface repositoryDirectoryInterface, String s) throws KettleException {
        return null;
    }

    @Override
    public String[] getTransformationNames(ObjectId objectId, boolean b) throws KettleException {
        return new String[0];
    }

    @Override
    public List<RepositoryElementMetaInterface> getJobObjects(ObjectId objectId, boolean b) throws KettleException {
        return null;
    }

    @Override
    public List<RepositoryElementMetaInterface> getTransformationObjects(ObjectId objectId, boolean b) throws KettleException {
        return null;
    }

    @Override
    public List<RepositoryElementMetaInterface> getJobAndTransformationObjects(ObjectId objectId, boolean b) throws KettleException {
        return null;
    }

    @Override
    public String[] getJobNames(ObjectId objectId, boolean b) throws KettleException {
        return new String[0];
    }

    @Override
    public String[] getDirectoryNames(ObjectId objectId) throws KettleException {
        return new String[0];
    }

    @Override
    public ObjectId insertLogEntry(String s) throws KettleException {
        return null;
    }

    @Override
    public void insertStepDatabase(ObjectId objectId, ObjectId objectId1, ObjectId objectId2) throws KettleException {

    }

    @Override
    public void insertJobEntryDatabase(ObjectId objectId, ObjectId objectId1, ObjectId objectId2) throws KettleException {

    }

    @Override
    public void saveConditionStepAttribute(ObjectId objectId, ObjectId objectId1, String s, Condition condition) throws KettleException {

    }

    @Override
    public Condition loadConditionFromStepAttribute(ObjectId objectId, String s) throws KettleException {
        return null;
    }

    @Override
    public boolean getStepAttributeBoolean(ObjectId objectId, int i, String s, boolean b) throws KettleException {
        return false;
    }

    @Override
    public boolean getStepAttributeBoolean(ObjectId objectId, int i, String s) throws KettleException {
        return false;
    }

    @Override
    public boolean getStepAttributeBoolean(ObjectId objectId, String s) throws KettleException {
        return false;
    }

    @Override
    public long getStepAttributeInteger(ObjectId objectId, int i, String s) throws KettleException {
        return 0;
    }

    @Override
    public long getStepAttributeInteger(ObjectId objectId, String s) throws KettleException {
        return 0;
    }

    @Override
    public String getStepAttributeString(ObjectId objectId, int i, String s) throws KettleException {
        return null;
    }

    @Override
    public String getStepAttributeString(ObjectId objectId, String s) throws KettleException {
        return null;
    }

    @Override
    public void saveStepAttribute(ObjectId objectId, ObjectId objectId1, int i, String s, String s1) throws KettleException {

    }

    @Override
    public void saveStepAttribute(ObjectId objectId, ObjectId objectId1, String s, String s1) throws KettleException {

    }

    @Override
    public void saveStepAttribute(ObjectId objectId, ObjectId objectId1, int i, String s, boolean b) throws KettleException {

    }

    @Override
    public void saveStepAttribute(ObjectId objectId, ObjectId objectId1, String s, boolean b) throws KettleException {

    }

    @Override
    public void saveStepAttribute(ObjectId objectId, ObjectId objectId1, int i, String s, long l) throws KettleException {

    }

    @Override
    public void saveStepAttribute(ObjectId objectId, ObjectId objectId1, String s, long l) throws KettleException {

    }

    @Override
    public void saveStepAttribute(ObjectId objectId, ObjectId objectId1, int i, String s, double v) throws KettleException {

    }

    @Override
    public void saveStepAttribute(ObjectId objectId, ObjectId objectId1, String s, double v) throws KettleException {

    }

    @Override
    public int countNrStepAttributes(ObjectId objectId, String s) throws KettleException {
        return 0;
    }

    @Override
    public int countNrJobEntryAttributes(ObjectId objectId, String s) throws KettleException {
        return 0;
    }

    @Override
    public boolean getJobEntryAttributeBoolean(ObjectId objectId, String s) throws KettleException {
        return false;
    }

    @Override
    public boolean getJobEntryAttributeBoolean(ObjectId objectId, int i, String s) throws KettleException {
        return false;
    }

    @Override
    public boolean getJobEntryAttributeBoolean(ObjectId objectId, String s, boolean b) throws KettleException {
        return false;
    }

    @Override
    public long getJobEntryAttributeInteger(ObjectId objectId, String s) throws KettleException {
        return 0;
    }

    @Override
    public long getJobEntryAttributeInteger(ObjectId objectId, int i, String s) throws KettleException {
        return 0;
    }

    @Override
    public String getJobEntryAttributeString(ObjectId objectId, String s) throws KettleException {
        return null;
    }

    @Override
    public String getJobEntryAttributeString(ObjectId objectId, int i, String s) throws KettleException {
        return null;
    }

    @Override
    public void saveJobEntryAttribute(ObjectId objectId, ObjectId objectId1, int i, String s, String s1) throws KettleException {

    }

    @Override
    public void saveJobEntryAttribute(ObjectId objectId, ObjectId objectId1, String s, String s1) throws KettleException {

    }

    @Override
    public void saveJobEntryAttribute(ObjectId objectId, ObjectId objectId1, int i, String s, boolean b) throws KettleException {

    }

    @Override
    public void saveJobEntryAttribute(ObjectId objectId, ObjectId objectId1, String s, boolean b) throws KettleException {

    }

    @Override
    public void saveJobEntryAttribute(ObjectId objectId, ObjectId objectId1, int i, String s, long l) throws KettleException {

    }

    @Override
    public void saveJobEntryAttribute(ObjectId objectId, ObjectId objectId1, String s, long l) throws KettleException {

    }

    @Override
    public DatabaseMeta loadDatabaseMetaFromStepAttribute(ObjectId objectId, String s, List<DatabaseMeta> list) throws KettleException {
        return null;
    }

    @Override
    public void saveDatabaseMetaStepAttribute(ObjectId objectId, ObjectId objectId1, String s, DatabaseMeta databaseMeta) throws KettleException {

    }

    @Override
    public DatabaseMeta loadDatabaseMetaFromJobEntryAttribute(ObjectId objectId, String s, String s1, List<DatabaseMeta> list) throws KettleException {
        return null;
    }

    @Override
    public DatabaseMeta loadDatabaseMetaFromJobEntryAttribute(ObjectId objectId, String s, int i, String s1, List<DatabaseMeta> list) throws KettleException {
        return null;
    }

    @Override
    public void saveDatabaseMetaJobEntryAttribute(ObjectId objectId, ObjectId objectId1, String s, String s1, DatabaseMeta databaseMeta) throws KettleException {

    }

    @Override
    public void saveDatabaseMetaJobEntryAttribute(ObjectId objectId, ObjectId objectId1, int i, String s, String s1, DatabaseMeta databaseMeta) throws KettleException {

    }

    @Override
    public void undeleteObject(RepositoryElementMetaInterface repositoryElementMetaInterface) throws KettleException {

    }

    @Override
    public List<Class<? extends IRepositoryService>> getServiceInterfaces() throws KettleException {
        return null;
    }

    @Override
    public IRepositoryService getService(Class<? extends IRepositoryService> aClass) throws KettleException {
        return null;
    }

    @Override
    public boolean hasService(Class<? extends IRepositoryService> aClass) throws KettleException {
        return false;
    }

    @Override
    public RepositoryObject getObjectInformation(ObjectId objectId, RepositoryObjectType repositoryObjectType) throws KettleException {
        return null;
    }

    @Override
    public String getConnectMessage() {
        return null;
    }

    @Override
    public String[] getJobsUsingDatabase(ObjectId objectId) throws KettleException {
        return new String[0];
    }

    @Override
    public String[] getTransformationsUsingDatabase(ObjectId objectId) throws KettleException {
        return new String[0];
    }

    @Override
    public IRepositoryImporter getImporter() {
        return null;
    }

    @Override
    public IRepositoryExporter getExporter() throws KettleException {
        return null;
    }

    @Override
    public IMetaStore getMetaStore() {
        return null;
    }

    @Override
    public IUnifiedRepository getUnderlyingRepository() {
        return null;
    }
}
