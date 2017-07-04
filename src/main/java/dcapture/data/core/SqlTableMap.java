package dcapture.data.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Sql Table Map
 */
public class SqlTableMap {
    private final String schema;
    private Map<String, Class<?>> nameClassMap;
    private Map<Class<?>, SqlTable> classMap;

    SqlTableMap(String schema) {
        this.schema = schema;
    }

    void setClassMap(Map<Class<?>, SqlTable> map) {
        this.classMap = map;
    }

    public Collection<SqlTable> getSqlTableList() {
        return classMap.values();
    }

    String getSchema() {
        return schema;
    }

    public SqlTable getSqlTable(Class<?> tableClass) {
        return classMap.get(tableClass);
    }

    public SqlTable getSqlTable(String tableName) {
        Class<?> clsName = nameClassMap.get(tableName);
        return clsName == null ? null : classMap.get(clsName);
    }

    public SqlTable getSqlTable(String tableName, boolean ignoreCase) {
        if (nameClassMap == null) {
            updateNameClassMap();
        }
        if (ignoreCase) {
            for (Map.Entry<String, Class<?>> entry : nameClassMap.entrySet()) {
                if (tableName.equalsIgnoreCase(entry.getKey())) {
                    return classMap.get(entry.getValue());
                }
            }
            return null;
        }
        return getSqlTable(tableName);
    }

    private void updateNameClassMap() {
        nameClassMap = new HashMap<>();
        for (Class<?> tableClass : classMap.keySet()) {
            nameClassMap.put(tableClass.getSimpleName(), tableClass);
        }
    }

    public SqlColumn getSqlColumn(String tableName, String columnName) {
        SqlTable sqlTable = getSqlTable(tableName);
        return sqlTable == null ? null : getSqlColumn(sqlTable, columnName);
    }

    public SqlColumn getSqlColumn(SqlTable sqlTable, String columnName) {
        for (SqlColumn column : sqlTable.getSqlColumnList()) {
            if (columnName.equals(column.getName())) {
                return column;
            }
        }
        return null;
    }
}
