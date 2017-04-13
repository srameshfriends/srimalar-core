package dcapture.data.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sql Table
 */
public class SqlTable {
    private final Class<?> type;
    private final String name;
    private List<Field> fieldList;
    private SqlColumn primaryColumn;
    private List<SqlColumn> sqlColumnList;
    private Map<String, String> columnFieldMap;
    private Map<String, Class<?>> enumFieldMap;
    private List<SqlReference> referenceList;

    public SqlTable(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setFieldList(List<Field> fieldList) {
        this.fieldList = fieldList;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public List<SqlColumn> getSqlColumnList() {
        return sqlColumnList;
    }

    public void setSqlColumnList(List<SqlColumn> sqlColumnList) {
        this.sqlColumnList = sqlColumnList;
    }

    public List<SqlReference> getReferenceList() {
        return referenceList;
    }

    public void setReferenceList(List<SqlReference> referenceList) {
        this.referenceList = referenceList;
    }

    public SqlColumn getPrimaryColumn() {
        if (primaryColumn == null) {
            for (SqlColumn column : getSqlColumnList()) {
                if (column.isPrimaryKey()) {
                    primaryColumn = column;
                    break;
                }
            }
        }
        return primaryColumn;
    }

    public Map<String, String> getColumnFieldMap() {
        if (columnFieldMap == null) {
            columnFieldMap = new HashMap<>();
            for (SqlColumn column : getSqlColumnList()) {
                columnFieldMap.put(column.getName(), column.getFieldName());
            }
        }
        return columnFieldMap;
    }

    Class<?> getEnumClass(String fieldName) {
        if (enumFieldMap == null) {
            enumFieldMap = new HashMap<>();
            for (SqlColumn column : getSqlColumnList()) {
                if (column.isEnumType()) {
                    enumFieldMap.put(column.getFieldName(), column.getType());
                }
            }
        }
        return enumFieldMap.get(fieldName);
    }

    public List<SqlColumn> getColumnListWithoutPK() {
        List<SqlColumn> columnList = new ArrayList<>();
        columnList.addAll(getSqlColumnList());
        columnList.remove(getPrimaryColumn());
        return columnList;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof SqlTable && ((SqlTable) obj).getName().equals(getName());
    }
}
