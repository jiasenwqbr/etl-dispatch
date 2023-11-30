package com.jason.datacollection.core.repository;


import com.alibaba.fastjson.JSONObject;
import com.jason.datacollection.core.dto.HopsDTO;
import com.jason.datacollection.core.dto.StepInterface;
import com.jason.datacollection.core.dto.StepMetaDTO;
import com.jason.datacollection.core.dto.TransStepDTO;
import lombok.extern.log4j.Log4j2;
import com.jason.datacollection.core.dto.common.DatabaseMetaDTO;
import com.jason.datacollection.core.dto.output.TableOutputDTO;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Trans工具类，主要用于数据格式转换等
 *
 * @author chenzhao
 */
@Log4j2
public class TransFormUtil {

    /**
     * 数据转换入口类
     *
     * @param type      类型，Job:Trans
     * @param transMeta 转换属性
     * @return
     */
    public static JSONObject dataTransform(String type, TransMeta transMeta) {
        JSONObject jsonObject = new JSONObject();
        if (type.equals("job")) {

        } else if (type.equals("trans")) {
            List<TransStepDTO> transStepDTOS = transStepsTransform(transMeta.getSteps());
            List<HopsDTO> hopsList = transHopsTransform(transMeta.getTransHops());
            //hops
            jsonObject.put("transName", transMeta.getName());
            jsonObject.put("stepList", transStepDTOS);
            jsonObject.put("hopsList", hopsList);
        }
        return jsonObject;
    }

    /**
     * 连接线对象处理
     *
     * @param transHops
     * @return
     */
    public static List<HopsDTO> transHopsTransform(List<TransHopMeta> transHops) {
        List<HopsDTO> hopsList = new ArrayList<>();
        transHops.stream().forEach((hops) -> {
            HopsDTO hopsDTO = new HopsDTO();
            hopsDTO.setFromId(hops.getFromStep().getObjectId().getId());
            hopsDTO.setEnabled(hops.isEnabled());
            hopsDTO.setToId(hops.getToStep().getObjectId().getId());
            hopsDTO.setHopsId(hops.getObjectId().getId());
            hopsList.add(hopsDTO);
        });
        return hopsList;
    }

    /**
     * 转换步骤
     *
     * @param stepMetaList
     * @return
     */
    public static List<TransStepDTO> transStepsTransform(List<StepMeta> stepMetaList) {
        List<TransStepDTO> stepsList = new ArrayList();
        if (stepMetaList.size() > 0) {
            stepMetaList.forEach((stepMeta) -> {
                TransStepDTO transStepDTO = new TransStepDTO();
                transStepDTO.setStepId(stepMeta.getObjectId().getId());
                transStepDTO.setStepChangedDate(stepMeta.getChangedDate());
                transStepDTO.setStepName(stepMeta.getName());
                //步骤属性替换
                StepInterface stepInterface = stepTransform(stepMeta.getStepMetaInterface());
                transStepDTO.setStepInterface(stepInterface);
                transStepDTO.setStepType(stepMeta.getStepID());
                transStepDTO.setLocationX(stepMeta.getLocation().x);
                transStepDTO.setLocationY(stepMeta.getLocation().y);
                stepsList.add(transStepDTO);
            });
        }
        return stepsList;
    }

    /**
     * 步骤属性转换
     *
     * @param stepMetaInterface
     * @return
     */
    public static StepInterface stepTransform(StepMetaInterface stepMetaInterface) {
        StepInterface stepInterface = null;
        if (stepMetaInterface instanceof TableInputMeta) {
            StepMetaDTO stepMetaDTO = new StepMetaDTO();
            TableInputMeta tableInputMeta = (TableInputMeta) stepMetaInterface;
            stepMetaDTO.setSql(tableInputMeta.getSQL());
            stepMetaDTO.setDatabaseMeta(databaseTransform(tableInputMeta.getDatabaseMeta()));
            //setpMetaDTO.setDatabaseMetaList(tableInputMeta.getUsedDatabaseConnections());
            stepInterface = stepMetaDTO;
        } else if (stepMetaInterface instanceof TableOutputMeta) {
            TableOutputMeta tableOutputMeta = (TableOutputMeta) stepMetaInterface;
            TableOutputDTO tableOutputDTO = new TableOutputDTO();
            tableOutputDTO.setTableName(tableOutputMeta.getTableName());
            //tableOutputDTO.setId(tableOutputMeta.getObjectId().getId());
            tableOutputDTO.setName(tableOutputMeta.getParentStepMeta().getName());
            tableOutputDTO.setDatabaseMeta(databaseTransform(tableOutputMeta.getDatabaseMeta()));
            tableOutputDTO.setTruncateTable(tableOutputMeta.truncateTable());
            tableOutputDTO.setCommitSize(tableOutputMeta.getCommitSize());
            stepInterface = tableOutputDTO;
            log.info("表输出");
        }
        return stepInterface;
    }

    /**
     * 数据源连接转换
     *
     * @param databaseMeta
     * @return
     */
    public static DatabaseMetaDTO databaseTransform(DatabaseMeta databaseMeta) {
        DatabaseMetaDTO databaseMetaDTO = new DatabaseMetaDTO();
        databaseMetaDTO.setDatabaseName(databaseMeta.getDatabaseName());
        databaseMetaDTO.setDatabasePortNumberString(databaseMeta.getDatabasePortNumberString());
        databaseMetaDTO.setPluginId(databaseMeta.getPluginId());
        databaseMetaDTO.setDisplayName(databaseMeta.getDisplayName());
        databaseMetaDTO.setDriverClass(databaseMeta.getDriverClass());
        databaseMetaDTO.setHostname(databaseMeta.getHostname());
        databaseMetaDTO.setName(databaseMeta.getName());
        databaseMetaDTO.setObjectId(databaseMeta.getObjectId().getId());
        databaseMetaDTO.setPassword(databaseMeta.getPassword());
        databaseMetaDTO.setUsername(databaseMeta.getUsername());
        return databaseMetaDTO;
    }
}
