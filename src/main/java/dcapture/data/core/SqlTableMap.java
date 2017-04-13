package dcapture.data.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Sql Table Map
 */
public class SqlTableMap {
    private final String schema;
    private Map<String, SqlTable> nameMap;
    private Map<Class<?>, SqlTable> classMap;

    SqlTableMap(String schema) {
        this.schema = schema;
        nameMap = new HashMap<>();
    }

    void setClassMap(Map<Class<?>, SqlTable> map) {
        this.classMap = map;
    }

    void setNameMap(Map<String, SqlTable> map) {
        this.nameMap = map;
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

    SqlTable getSqlTable(String tableName) {
        return nameMap.get(tableName.toLowerCase());
    }
}
