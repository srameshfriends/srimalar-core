package dcapture.data.core;

import java.io.Serializable;

/**
 * Sql Reference
 */
public class SqlReference implements Serializable {
    private static final long serialVersionUID = 75264727L;
    private SqlTable sqlTable, referenceTable;
    private SqlColumn sqlColumn, referenceColumn;
    private Object value;

    public SqlTable getSqlTable() {
        return sqlTable;
    }

    public void setSqlTable(SqlTable sqlTable) {
        this.sqlTable = sqlTable;
    }

    public SqlTable getReferenceTable() {
        return referenceTable;
    }

    public void setReferenceTable(SqlTable referenceTable) {
        this.referenceTable = referenceTable;
    }

    public SqlColumn getSqlColumn() {
        return sqlColumn;
    }

    public void setSqlColumn(SqlColumn sqlColumn) {
        this.sqlColumn = sqlColumn;
    }

    public SqlColumn getReferenceColumn() {
        return referenceColumn;
    }

    public void setReferenceColumn(SqlColumn referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Reference Table : ").append(referenceTable.getName());
        builder.append(" Column : ").append(referenceColumn.getName());
        if(value != null) {
            builder.append(" Record : ").append(value.toString());
        }
        return builder.toString();
    }
}
