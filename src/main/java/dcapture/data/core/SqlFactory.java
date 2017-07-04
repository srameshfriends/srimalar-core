package dcapture.data.core;

import com.google.gson.*;
import jodd.bean.BeanUtil;

import javax.persistence.TemporalType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.*;

/**
 * Sql Factory
 */
public abstract class SqlFactory implements SqlProcessor {
    private SqlTableMap sqlTableMap;

    @Override
    public String getSchema() {
        return getSqlTableMap().getSchema();
    }

    @Override
    public SqlTableMap getSqlTableMap() {
        return sqlTableMap;
    }

    @Override
    public void runForwardTool(boolean addForeignKey) throws SQLException {
        List<SqlQuery> queryList = new ArrayList<>();
        queryList.add(createSchemaQuery());
        queryList.addAll(createTableQueries());
        if (addForeignKey) {
            queryList.addAll(alterTableQueries());
        }
        SqlTransaction transaction = getSqlTransaction();
        transaction.executeCommit(queryList);
    }

    protected void initialize(String schema, File persistenceFile) {
        try {
            JsonArray jsonArray = readJsonArray(persistenceFile);
            if (jsonArray == null) {
                throw new NullPointerException("Persistence file parsing error : " + persistenceFile);
            }
            Map<Class<?>, SqlTable> classMap = new HashMap<>();
            for (JsonElement ele : jsonArray) {
                SqlTable sqlTable = toSqlTable(ele);
                classMap.put(sqlTable.getType(), sqlTable);
            }
            for (SqlTable sqlTable : classMap.values()) {
                updateSqlJoinTable(sqlTable, classMap);
            }
            for (JsonElement ele : jsonArray) {
                updateSqlReference(ele, classMap);
            }
            sqlTableMap = new SqlTableMap(schema);
            sqlTableMap.setClassMap(classMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Class<?> toClass(String text) {
        Class<?> cls = null;
        try {
            cls = getPrimitive(text);
            if (cls == null) {
                return Class.forName(text);
            }
        } catch (Exception ex) {
            // ignore exception
        }
        return cls;
    }

    private Class<?> toClassTable(JsonObject obj) {
        try {
            String clsName = toTextNullSafe(obj, "type");
            return clsName == null ? null : Class.forName(clsName);
        } catch (Exception ex) {
            // ignore exception
        }
        return null;
    }

    private Class<?> getPrimitive(String text) {
        if ("long".equals(text)) {
            return long.class;
        } else if ("boolean".equals(text)) {
            return boolean.class;
        } else if ("int".equals(text)) {
            return int.class;
        } else if ("double".equals(text)) {
            return double.class;
        } else if ("char".equals(text)) {
            return char.class;
        } else if ("byte".equals(text)) {
            return byte.class;
        } else if ("short".equals(text)) {
            return short.class;
        } else if ("float".equals(text)) {
            return float.class;
        }
        return null;
    }

    private SqlTable toSqlTable(JsonElement ele) {
        JsonObject obj = ele.getAsJsonObject();
        Class<?> tblCls = toClassTable(obj);
        if (tblCls == null) {
            throw new NullPointerException("sql table type should not be empty " + ele.toString());
        }
        JsonArray jsonSqlColList = toJsonArray(obj, "sqlColumnList");
        if (jsonSqlColList == null) {
            throw new NullPointerException("sql column should not be null");
        }
        SqlTable result = new SqlTable(tblCls);
        List<SqlColumn> columnList = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        for (JsonElement colEle : jsonSqlColList) {
            SqlColumn sqlColumn = toSqlColumn(colEle);
            columnList.add(sqlColumn);
            columns.add(sqlColumn.getName());
        }
        result.setSqlColumnList(columnList);
        result.setColumns(columns.toArray(new String[columns.size()]));
        result.getPrimaryColumn();
        return result;
    }

    private void updateSqlJoinTable(SqlTable table, Map<Class<?>, SqlTable> nameMap) {
        for (SqlColumn sqlColumn : table.getSqlColumnList()) {
            if (sqlColumn.getJoinTable() != null) {
                SqlTable joinTable = sqlColumn.getJoinTable();
                sqlColumn.setJoinTable(nameMap.get(joinTable.getType()));
            }
        }
    }

    private SqlColumn toSqlColumn(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        String fieldName = toText(obj, "name");
        Class<?> colType = toClass(toText(obj, "type"));
        SqlColumn sqlColumn = new SqlColumn(fieldName, colType);
        sqlColumn.setPrimaryKey(toBoolean(obj, "primaryKey"));
        sqlColumn.setAutoIncrement(toBoolean(obj, "autoIncrement"));
        sqlColumn.setNullable(toBoolean(obj, "nullable"));
        sqlColumn.setLength(toInt(obj, "length"));
        sqlColumn.setColIndex(toInt(obj, "colIndex"));
        sqlColumn.setSqlType(toSQLType(colType));
        sqlColumn.setTemporalType(toTemporalType(toText(obj, "temporalType")));
        sqlColumn.setEnumType(toBoolean(obj, "enumType"));
        //
        JsonObject jsonJoinTbl = toJsonObject(obj, "joinTable");
        if (jsonJoinTbl != null) {
            colType = toClass(toText(jsonJoinTbl, "type"));
            sqlColumn.setJoinTable(new SqlTable(colType));
        }
        return sqlColumn;
    }

    private SQLType toSQLType(Class<?> typeClass) {
        if (String.class.equals(typeClass)) {
            return JDBCType.VARCHAR;
        } else if (int.class.equals(typeClass)) {
            return JDBCType.INTEGER;
        } else if (BigDecimal.class.equals(typeClass)) {
            return JDBCType.BIGINT;
        } else if (boolean.class.equals(typeClass)) {
            return JDBCType.BOOLEAN;
        } else if (Date.class.equals(typeClass)) {
            return JDBCType.DATE;
        } else if (long.class.equals(typeClass)) {
            return JDBCType.BIGINT;
        } else if (double.class.equals(typeClass)) {
            return JDBCType.DECIMAL;
        } else if (double.class.equals(typeClass)) {
            return JDBCType.BIGINT;
        } else if (Enum.class.isAssignableFrom(typeClass)) {
            return JDBCType.VARCHAR;
        } else {
            throw new IllegalArgumentException("Sql factory have not handle SQL type conversion for " + typeClass);
        }
    }

    private TemporalType toTemporalType(String name) {
        if (name != null && name.trim().isEmpty()) {
            try {
                return TemporalType.valueOf(name);
            } catch (Exception ex) {
                // ignore exception
            }
        }
        return null;
    }

    private void updateSqlReference(JsonElement ele, Map<Class<?>, SqlTable> tableMap) {
        JsonObject obj = ele.getAsJsonObject();
        JsonArray jsonRefList = toJsonArray(obj, "referenceList");
        if (jsonRefList == null) {
            return;
        }
        Class<?> tblCls = toClassTable(obj);
        if (tblCls == null) {
            throw new NullPointerException("sql reference table type should not be empty " + ele.toString());
        }
        SqlTable updateSqlTable = tableMap.get(tblCls);
        List<SqlReference> referenceList = new ArrayList<>();
        updateSqlTable.setReferenceList(referenceList);
        for (JsonElement colEle : jsonRefList) {
            JsonObject root = colEle.getAsJsonObject();
            JsonObject jSqlTbl = toJsonObject(root, "sqlTable");
            JsonObject jRefTbl = toJsonObject(root, "referenceTable");
            JsonObject jSqlCol = toJsonObject(root, "sqlColumn");
            JsonObject jRefCol = toJsonObject(root, "referenceColumn");
            //
            Class<?> txtSqlTbl = toClassTable(jSqlTbl);
            Class<?> txtRefTbl = toClassTable(jRefTbl);
            String txtSqlCol = toTextNullSafe(jSqlCol, "name");
            String txtRefCol = toTextNullSafe(jRefCol, "name");
            //
            SqlReference reference = new SqlReference();
            SqlTable sqlTable = tableMap.get(txtSqlTbl);
            reference.setSqlTable(sqlTable);
            for (SqlColumn column : sqlTable.getSqlColumnList()) {
                if (txtSqlCol.equals(column.getName())) {
                    reference.setSqlColumn(column);
                    break;
                }
            }
            SqlTable refTable = tableMap.get(txtRefTbl);
            reference.setReferenceTable(refTable);
            for (SqlColumn column : refTable.getSqlColumnList()) {
                if (txtRefCol.equals(column.getName())) {
                    reference.setReferenceColumn(column);
                    break;
                }
            }
            referenceList.add(reference);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> toEntityList(SqlMetaDataResult dataResult) {
        SqlTable table = getValidSqlTable(getSqlTableMap(), dataResult.getMetaData());
      /*  if (table == null) {
            return new ArrayList<>();
        }*/
        List<T> resultList = new ArrayList<>();
        for (Object[] data : dataResult.getObjectsList()) {
            T result = (T) instance(table.getType());
            int index = 0;
            for (Object value : data) {
                String columnName = dataResult.getMetaData()[index].getColumnName();
                SqlColumn sqlColumn = table.getSqlColumn(columnName);
                Class<?> enumClass = table.getEnumClass(sqlColumn.getName());
                if (enumClass != null) {
                    value = parseEnum(enumClass, (String) value);
                }
                copyProperty(result, sqlColumn, value);
                index += 1;
            }
            resultList.add(result);
        }
        return resultList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T toEntity(SqlMetaDataResult dataResult) {
        SqlTable table = getValidSqlTable(getSqlTableMap(), dataResult.getMetaData());
        if (dataResult.getObjectsList().isEmpty()) {
            return null;
        }
        Object[] data = dataResult.getObjectsList().get(0);
        T result = (T) instance(table.getType());
        int index = 0;
        for (Object value : data) {
            String columnName = dataResult.getMetaData()[index].getColumnName();
            SqlColumn sqlColumn = table.getSqlColumn(columnName);
            Class<?> enumClass = table.getEnumClass(sqlColumn.getName());
            if (enumClass != null) {
                value = parseEnum(enumClass, (String) value);
            }
            copyProperty(result, sqlColumn, value);
            index += 1;
        }
        return result;
    }

    @Override
    public List<SqlReference> getSqlReference(Class<?> entityClass) {
        SqlTable table = getSqlTable(entityClass);
        return table.getReferenceList();
    }

    private void copyProperty(Object bean, SqlColumn sqlColumn, Object value) {
        if (value != null) {
            BeanUtil.pojo.setProperty(bean, sqlColumn.getName(), value);
        }
    }

    private SqlTable getValidSqlTable(SqlTableMap tableMap, SqlMetaData[] metaDataArray) {
        return tableMap.getSqlTable(metaDataArray[0].getTableName(), true);
    }

    private Object instance(Class<?> ins) {
        try {
            return ins.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected Object parseEnum(Class enumClass, String value) {
        if (value != null) {
            try {
                return Enum.valueOf(enumClass, value);
            } catch (Exception ex) {
                // ignore exception
            }
        }
        return null;
    }

    private JsonElement readJsonElement(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Gson gson = new Gson();
            return gson.fromJson(reader, JsonElement.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonArray readJsonArray(File file) {
        JsonElement element = readJsonElement(file);
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : null;
    }

    private JsonPrimitive toJsonPrimitive(JsonObject obj, String name) {
        JsonElement element = obj.get(name);
        if (element instanceof JsonPrimitive) {
            return (JsonPrimitive) element;
        }
        return null;
    }

    private String toText(JsonObject obj, String name) {
        JsonPrimitive primitive = toJsonPrimitive(obj, name);
        return primitive == null ? null : primitive.getAsString().trim();
    }

    private String toTextNullSafe(JsonObject obj, String name) {
        JsonPrimitive primitive = toJsonPrimitive(obj, name);
        return primitive == null ? "" : primitive.getAsString().trim();
    }

    String toText(JsonPrimitive primitive) {
        return primitive == null ? null : primitive.getAsString().trim();
    }

    String toText(JsonObject obj, String name, String deepName) {
        JsonElement element = obj.get(name);
        if (element instanceof JsonObject) {
            JsonPrimitive primitive = ((JsonObject) element).getAsJsonPrimitive(deepName);
            if (primitive != null && primitive.isString()) {
                return primitive.getAsString();
            }
        }
        return null;
    }

    private JsonObject toJsonObject(JsonObject obj, String name) {
        JsonElement element = obj.get(name);
        if (element instanceof JsonObject) {
            return ((JsonObject) element);
        }
        return null;
    }

    private JsonArray toJsonArray(JsonObject obj, String name) {
        JsonElement element = obj.get(name);
        if (element != null && element.isJsonArray()) {
            return element.getAsJsonArray();
        }
        return null;
    }

    private boolean toBoolean(JsonObject obj, String name) {
        JsonPrimitive primitive = toJsonPrimitive(obj, name);
        return primitive != null && primitive.getAsBoolean();
    }

    private int toInt(JsonObject obj, String name) {
        JsonPrimitive primitive = toJsonPrimitive(obj, name);
        return primitive == null ? 0 : primitive.getAsInt();
    }
}
