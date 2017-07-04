package dcapture.data.core;

import javax.persistence.TemporalType;
import java.sql.SQLType;

/**
 * Sql Column
 */
public class SqlColumn {
    private final String name;
    private final Class<?> type;
    private boolean primaryKey, autoIncrement, nullable, enumType;
    private int length;
    private SqlTable joinTable;
    private TemporalType temporalType;
    private int colIndex;
    private SQLType sqlType;

    public SqlColumn(String fieldName, Class<?> type) {
        this.name = fieldName;
        this.type = type;
        nullable = true;
    }

    public boolean isEnumType() {
        return enumType;
    }

    public void setEnumType(boolean enumType) {
        this.enumType = enumType;
    }

    public SqlTable getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(SqlTable joinTable) {
        this.joinTable = joinTable;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public TemporalType getTemporalType() {
        return temporalType;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    public int getColIndex() {
        return colIndex;
    }

    void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    public SQLType getSqlType() {
        return sqlType;
    }

    void setSqlType(SQLType sqlType) {
        this.sqlType = sqlType;
    }

    @Override
    public String toString() {
        return name;
    }
}
