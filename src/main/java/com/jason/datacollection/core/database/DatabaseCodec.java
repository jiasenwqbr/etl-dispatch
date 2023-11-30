package com.jason.datacollection.core.database;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jason.datacollection.util.StringEscapeHelper;
import org.pentaho.di.core.database.*;
import org.pentaho.di.core.database.sap.SAPR3DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DatabaseCodec {

    public static JSONObject encode(DatabaseMeta databaseMeta) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("name", databaseMeta.getDisplayName());
        jsonObject.put("type", databaseMeta.getPluginId());
        jsonObject.put("access", databaseMeta.getAccessType());

        jsonObject.put("hostname", databaseMeta.getHostname());
        jsonObject.put("databaseName", databaseMeta.getDatabaseName());
        jsonObject.put("username", databaseMeta.getUsername());
        jsonObject.put("password", Encr.decryptPasswordOptionallyEncrypted(databaseMeta.getPassword()));
        if (databaseMeta.isStreamingResults())
            jsonObject.put("streamingResults", databaseMeta.isStreamingResults());
        jsonObject.put("dataTablespace", databaseMeta.getDataTablespace());
        jsonObject.put("indexTablespace", databaseMeta.getIndexTablespace());
        if (databaseMeta.getSQLServerInstance() != null)
            jsonObject.put("sqlServerInstance", databaseMeta.getSQLServerInstance());
        if (databaseMeta.isUsingDoubleDecimalAsSchemaTableSeparator())
            jsonObject.put("usingDoubleDecimalAsSchemaTableSeparator", databaseMeta.isUsingDoubleDecimalAsSchemaTableSeparator());
        jsonObject.put(SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE, databaseMeta.getAttributes().getProperty(SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE));
        jsonObject.put(SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER, databaseMeta.getAttributes().getProperty(SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER));
        jsonObject.put(SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT, databaseMeta.getAttributes().getProperty(SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT));

        jsonObject.put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, databaseMeta.getAttributes().getProperty(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL));
        jsonObject.put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, databaseMeta.getAttributes().getProperty(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS));
        jsonObject.put("servername", databaseMeta.getServername());

        Object v = databaseMeta.getAttributes().get(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY);
        if (v != null && v instanceof String) {
            String useIntegratedSecurity = (String) v;
            jsonObject.put(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY, Boolean.parseBoolean(useIntegratedSecurity));
        } else {
            jsonObject.put(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY, false);
        }

        jsonObject.put("port", databaseMeta.getDatabasePortNumberString());

        // Option parameters:

        Map<String, String> extraOptions = databaseMeta.getExtraOptions();
        JSONArray options = new JSONArray();
        if (extraOptions != null) {
            Iterator<String> keys = extraOptions.keySet().iterator();
            String currentType = databaseMeta.getPluginId();
            while (keys.hasNext()) {

                String parameter = keys.next();
                String value = extraOptions.get(parameter);
                if (!StringUtils.hasText(value) || (value.equals(DatabaseMeta.EMPTY_OPTIONS_STRING))) {
                    value = "";
                }

                int dotIndex = parameter.indexOf('.');
                if (dotIndex >= 0) {
                    String parameterOption = parameter.substring(dotIndex + 1);
                    String databaseType = parameter.substring(0, dotIndex);
                    if (currentType != null && currentType.equals(databaseType)) {
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("name", parameterOption);
                        jsonObject2.put("value", value);
                        options.add(jsonObject2);
                    }
                }
            }
        }
        jsonObject.put("extraOptions", options);

        // Advanced panel settings:
        jsonObject.put("supportBooleanDataType", databaseMeta.supportsBooleanDataType());
        jsonObject.put("supportTimestampDataType", databaseMeta.supportsTimestampDataType());
        jsonObject.put("quoteIdentifiersCheck", databaseMeta.isQuoteAllFields());
        jsonObject.put("lowerCaseIdentifiersCheck", databaseMeta.isForcingIdentifiersToLowerCase());
        jsonObject.put("upperCaseIdentifiersCheck", databaseMeta.isForcingIdentifiersToUpperCase());
        jsonObject.put("preserveReservedCaseCheck", databaseMeta.preserveReservedCase());
        jsonObject.put("preferredSchemaName", databaseMeta.getPreferredSchemaName());
        jsonObject.put("connectSQL", StringEscapeHelper.encode(databaseMeta.getConnectSQL()));

        // Cluster panel settings
        jsonObject.put("partitioned", databaseMeta.isPartitioned() ? "Y" : "N");

        JSONArray partitionInfo = new JSONArray();
        PartitionDatabaseMeta[] clusterInformation = databaseMeta.getPartitioningInformation();
        if (clusterInformation != null) {
            for (int i = 0; i < clusterInformation.length; i++) {
                PartitionDatabaseMeta meta = clusterInformation[i];
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("partitionId", meta.getPartitionId());
                jsonObject2.put("hostname", meta.getHostname());
                jsonObject2.put("port", meta.getPort());
                jsonObject2.put("databaseName", meta.getDatabaseName());
                jsonObject2.put("username", meta.getUsername());
                jsonObject2.put("password", meta.getPassword());

                partitionInfo.add(jsonObject2);
            }
        }
        jsonObject.put("partitionInfo", partitionInfo);

        // Pooling panel settings
        jsonObject.put("usingConnectionPool", databaseMeta.isUsingConnectionPool() ? "Y" : "N");
        jsonObject.put("initialPoolSize", databaseMeta.getInitialPoolSize());
        jsonObject.put("maximumPoolSize", databaseMeta.getMaximumPoolSize());
        Properties properties = databaseMeta.getConnectionPoolingProperties();
        JSONArray jsonArray2 = new JSONArray();
        for (DatabaseConnectionPoolParameter parameter : BaseDatabaseMeta.poolingParameters) {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("enabled", properties.containsKey(parameter.getParameter()));
            jsonObject2.put("name", parameter.getParameter());
            jsonObject2.put("defValue", parameter.getDefaultValue());
            jsonObject2.put("description", StringEscapeHelper.encode(parameter.getDescription()));
            jsonArray2.add(jsonObject2);
        }

        jsonObject.put("pool_params", jsonArray2);
        jsonObject.put("read_only", databaseMeta.isReadOnly());

        return jsonObject;
    }

    public static DatabaseMeta decode(JSONObject jsonObject) throws KettleDatabaseException {
        DatabaseMeta databaseMeta = new DatabaseMeta();

        databaseMeta.setName(jsonObject.containsKey("name") ? jsonObject.getString("name") : null);
        databaseMeta.setDisplayName(databaseMeta.getName());
        databaseMeta.setDatabaseType(jsonObject.containsKey("type") ? jsonObject.getString("type") : null);
        databaseMeta.setAccessType(jsonObject.containsKey("access") ? jsonObject.getIntValue("access") : null);

        if (jsonObject.containsKey("hostname"))
            databaseMeta.setHostname(jsonObject.getString("hostname"));
        if (jsonObject.containsKey("databaseName"))
            databaseMeta.setDBName(jsonObject.getString("databaseName"));
        if (jsonObject.containsKey("username"))
            databaseMeta.setUsername(jsonObject.getString("username"));
        if (jsonObject.containsKey("password"))
            databaseMeta.setPassword(jsonObject.getString("password"));
        if (jsonObject.containsKey("streamingResults"))    // infobright-jndi
            databaseMeta.setStreamingResults(true);
        if (jsonObject.containsKey("dataTablespace"))    //oracle-jndi
            databaseMeta.setDataTablespace(jsonObject.getString("dataTablespace"));
        if (jsonObject.containsKey("indexTablespace"))    //oracle-jndi
            databaseMeta.setIndexTablespace(jsonObject.getString("indexTablespace"));
        if (jsonObject.containsKey("sqlServerInstance"))        //mssql-native
            databaseMeta.setSQLServerInstance(jsonObject.getString("sqlServerInstance"));
        if (jsonObject.containsKey("usingDoubleDecimalAsSchemaTableSeparator"))    //mssql-jndi
            databaseMeta.setUsingDoubleDecimalAsSchemaTableSeparator(jsonObject.getBoolean("usingDoubleDecimalAsSchemaTableSeparator"));

        // SAP Attributes...
        if (jsonObject.containsKey(SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE)) {
            databaseMeta.getAttributes().put(SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE, jsonObject.getString(SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE));
        }
        if (jsonObject.containsKey(SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER)) {
            databaseMeta.getAttributes().put(SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER, jsonObject.getString(SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER));
        }
        if (jsonObject.containsKey(SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT)) {
            databaseMeta.getAttributes().put(SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT, jsonObject.getString(SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT));
        }

        // Generic settings...
        if (jsonObject.containsKey(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL)) {
            databaseMeta.getAttributes().put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, jsonObject.getString(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL));
        }
        if (jsonObject.containsKey(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS)) {
            databaseMeta.getAttributes().put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, jsonObject.getString(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS));
        }

        // Server Name: (Informix)
        if (jsonObject.containsKey("servername")) {
            databaseMeta.setServername(jsonObject.getString("servername"));
        }

        // Microsoft SQL Server Use Integrated Security
        if (jsonObject.containsKey(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY)) {
            boolean flag = jsonObject.getBoolean(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY);
            if (flag)
                databaseMeta.getAttributes().put(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY, flag);
        }

        if (jsonObject.containsKey("port"))
            databaseMeta.setDBPort(jsonObject.getString("port"));

        // Option parameters:

        JSONArray options = jsonObject.getJSONArray("extraOptions");
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                JSONObject jsonObject2 = options.getJSONObject(i);
                String parameter = jsonObject2.getString("name");
                String value = jsonObject2.getString("value");

                if (value == null) {
                    value = "";
                }

                if ((parameter != null) && (parameter.trim().length() > 0)) {
                    if (value.trim().length() <= 0) {
                        value = DatabaseMeta.EMPTY_OPTIONS_STRING;
                    }

                    databaseMeta.addExtraOption(databaseMeta.getPluginId(), parameter, value);
                }
            }
        }

        // Advanced panel settings:

        if (jsonObject.containsKey("supportBooleanDataType")) {
            databaseMeta.setSupportsBooleanDataType(jsonObject.getBoolean("supportBooleanDataType"));
        }

        if (jsonObject.containsKey("supportTimestampDataType")) {
            databaseMeta.setSupportsTimestampDataType(jsonObject.getBoolean("supportTimestampDataType"));
        }

        if (jsonObject.containsKey("quoteIdentifiersCheck")) {
            databaseMeta.setQuoteAllFields(jsonObject.getBoolean("quoteIdentifiersCheck"));
        }

        if (jsonObject.containsKey("lowerCaseIdentifiersCheck")) {
            databaseMeta.setForcingIdentifiersToLowerCase(jsonObject.getBoolean("lowerCaseIdentifiersCheck"));
        }

        if (jsonObject.containsKey("upperCaseIdentifiersCheck")) {
            databaseMeta.setForcingIdentifiersToUpperCase(jsonObject.getBoolean("upperCaseIdentifiersCheck"));
        }

        if (jsonObject.containsKey("preserveReservedCaseCheck")) {
            databaseMeta.setPreserveReservedCase(jsonObject.getBoolean("preserveReservedCaseCheck"));
        }

        if (jsonObject.containsKey("preferredSchemaName")) {
            databaseMeta.setPreferredSchemaName(jsonObject.getString("preferredSchemaName"));
        }

        if (jsonObject.containsKey("connectSQL")) {
            databaseMeta.setConnectSQL(StringEscapeHelper.decode(jsonObject.getString("connectSQL")));
        }

        // Cluster panel settings
        databaseMeta.setPartitioned("Y".equalsIgnoreCase(jsonObject.getString("partitioned")));
        if ("Y".equalsIgnoreCase(jsonObject.getString("partitioned"))) {
            JSONArray partitionInfo = jsonObject.getJSONArray("partitionInfo");
            if (partitionInfo != null) {
                ArrayList<PartitionDatabaseMeta> list = new ArrayList<PartitionDatabaseMeta>();
                for (int i = 0; i < partitionInfo.size(); i++) {
                    JSONObject jsonObject2 = partitionInfo.getJSONObject(i);
                    PartitionDatabaseMeta meta = new PartitionDatabaseMeta();

                    String partitionId = jsonObject2.getString("partitionId");
                    if ((partitionId == null) || (partitionId.trim().length() <= 0)) {
                        continue;
                    }

                    meta.setPartitionId(jsonObject2.getString("partitionId"));
                    meta.setHostname(jsonObject2.getString("hostname"));
                    meta.setPort(jsonObject2.getString("port"));
                    meta.setDatabaseName(jsonObject2.getString("databaseName"));
                    meta.setUsername(jsonObject2.getString("username"));
                    meta.setPassword(jsonObject2.getString("password"));
                    list.add(meta);
                }
                if (list.size() > 0)
                    databaseMeta.setPartitioningInformation(list.toArray(new PartitionDatabaseMeta[list.size()]));
            }
        }

        if ("Y".equalsIgnoreCase(jsonObject.getString("usingConnectionPool"))) {
            databaseMeta.setUsingConnectionPool(true);

            try {
                databaseMeta.setInitialPoolSize(jsonObject.getIntValue("initialPoolSize"));
            } catch (Exception e) {
            }

            try {
                databaseMeta.setMaximumPoolSize(jsonObject.getIntValue("maximumPoolSize"));
            } catch (Exception e) {
            }

            JSONArray pool_params = jsonObject.getJSONArray("pool_params");
            if (pool_params != null) {
                Properties properties = new Properties();
                for (int i = 0; i < pool_params.size(); i++) {
                    JSONObject jsonObject2 = pool_params.getJSONObject(i);
                    Boolean enabled = jsonObject2.getBoolean("enabled");
                    String parameter = jsonObject2.getString("name");
                    String value = jsonObject2.getString("defValue");

                    if (!enabled) {
                        continue;
                    }

                    if (StringUtils.hasText(parameter) && StringUtils.hasText(value)) {
                        properties.setProperty(parameter, value);
                    }
                }
                databaseMeta.setConnectionPoolingProperties(properties);
            }
        }

        databaseMeta.setReadOnly(jsonObject.containsKey("read_only") ? jsonObject.getBoolean("read_only") : false);
        return databaseMeta;
    }

}
