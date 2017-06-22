package dcapture.data.postgres;

import dcapture.data.core.*;

import java.sql.SQLException;
import java.sql.SQLType;
import java.util.*;

/**
 * H2 Modify Builder
 */
public class PostgresModifyBuilder implements SqlModifyBuilder {
    private final SqlProcessor sqlProcessor;
    private String updateTable, deleteTable, insertTable;
    private List<String> updateColumns, insertColumns;
    private WhereQuery whereQuery;
    private List<Object> updateParameters, insertParameters;
    private StringBuilder joinBuilder;
    private Map<Integer, SQLType> typeIndexMap;

    PostgresModifyBuilder(SqlProcessor sqlProcessor) {
        this.sqlProcessor = sqlProcessor;
    }

    @Override
    public String getSchema() {
        return sqlProcessor.getSchema();
    }

    @Override
    public PostgresModifyBuilder updateColumn(SqlColumn sqlColumn, Object object) {
        getUpdateColumns().add(sqlColumn.getName());
        if (sqlColumn.getJoinTable() != null) {
            if (1 > ((Integer) object)) {
                object = null;
            }
        }
        getUpdateParameters().add(object);
        getTypeIndexMap().put(getUpdateParameters().size(), sqlColumn.getSqlType());
        return PostgresModifyBuilder.this;
    }

    @Override
    public SqlModifyBuilder updateColumn(String columnName, Object object) throws SQLException {
        if (columnName.contains(".")) {
            columnName = columnName.substring(columnName.lastIndexOf("."), columnName.length());
        }
        SqlColumn sqlColumn = sqlProcessor.getSqlTableMap().getSqlColumn(updateTable, columnName);
        if (sqlColumn == null) {
            throw new SQLException("Update Table : " + updateTable + " > Column not found : " + columnName);
        }
        return updateColumn(sqlColumn, object);
    }

    @Override
    public PostgresModifyBuilder update(String tableName) {
        this.updateTable = tableName;
        return PostgresModifyBuilder.this;
    }

    @Override
    public SqlModifyBuilder update(Class<?> tableClass) {
        SqlTable sqlTable = sqlProcessor.getSqlTable(tableClass);
        return deleteFrom(sqlTable.getName());
    }

    @Override
    public PostgresModifyBuilder deleteFrom(String table) {
        deleteTable = table;
        return PostgresModifyBuilder.this;
    }

    @Override
    public SqlModifyBuilder deleteFrom(Class<?> tableClass) {
        SqlTable sqlTable = sqlProcessor.getSqlTable(tableClass);
        return deleteFrom(sqlTable.getName());
    }

    @Override
    public PostgresModifyBuilder insertInto(String tableName) {
        this.insertTable = tableName;
        return PostgresModifyBuilder.this;
    }

    @Override
    public PostgresModifyBuilder insertColumn(SqlColumn sqlColumn, Object object) {
        getInsertColumns().add(sqlColumn.getName());
        if (sqlColumn.getJoinTable() != null) {
            if (1 > ((Integer) object)) {
                object = null;
            }
        }
        getInsertParameters().add(object);
        getTypeIndexMap().put(getInsertParameters().size(), sqlColumn.getSqlType());
        return PostgresModifyBuilder.this;
    }

    @Override
    public PostgresModifyBuilder join(String joinQuery) {
        getJoinBuilder().append(joinQuery);
        return PostgresModifyBuilder.this;
    }

    @Override
    public PostgresModifyBuilder where(WhereQuery whereQuery) {
        this.whereQuery = whereQuery;
        return PostgresModifyBuilder.this;
    }

    @Override
    public SqlQuery getSqlQuery() {
        SqlQuery sqlQuery = new SqlQuery();
        if (updateTable != null) {
            sqlQuery = buildUpdateQuery();
        } else if (insertTable != null) {
            sqlQuery = buildInsertQuery();
        } else if (deleteTable != null) {
            sqlQuery = buildDeleteQuery();
        }
        return sqlQuery;
    }

    private Map<Integer, SQLType> getTypeIndexMap() {
        if (typeIndexMap == null) {
            typeIndexMap = new HashMap<>();
        }
        return typeIndexMap;
    }

    private String buildJoinSQ() {
        StringBuilder sb = new StringBuilder(" ");
        if (joinBuilder != null) {
            sb.append(" ").append(joinBuilder.toString());
        }
        return sb.toString();
    }

    private String buildWhereSQ(LinkedList<Object> parameters) {
        if (whereQuery != null) {
            parameters.addAll(whereQuery.getParameterList());
            return " where " + whereQuery.toString();
        }
        return "";
    }

    private SqlQuery buildUpdateQuery() {
        LinkedList<Object> parameters = new LinkedList<>();
        parameters.addAll(getUpdateParameters());
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(getSchema()).append(".").append(updateTable).append(" set ");
        for (String upd : updateColumns) {
            sb.append(upd).append(" = ?,");
        }
        sb.replace(sb.toString().length() - 1, sb.toString().length(), " ");
        if (joinBuilder != null) {
            sb.append(" ").append(joinBuilder.toString());
        }
        if (whereQuery != null) {
            sb.append(" where ").append(whereQuery.toString());
            parameters.addAll(whereQuery.getParameterList());
        }
        sb.append(";");
        SqlQuery sqlQuery = new SqlQuery(sb.toString());
        sqlQuery.setParameterList(parameters);
        sqlQuery.setTypeIndexMap(getTypeIndexMap());
        return sqlQuery;
    }

    private SqlQuery buildInsertQuery() {
        LinkedList<Object> parameters = new LinkedList<>();
        parameters.addAll(getInsertParameters());
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(getSchema()).append(".").append(insertTable).append(" (");
        StringBuilder pss = new StringBuilder("(");
        for (String ic : insertColumns) {
            sb.append(ic).append(",");
            pss.append("?,");
        }
        sb.replace(sb.toString().length() - 1, sb.toString().length(), ")");
        pss.replace(pss.toString().length() - 1, pss.toString().length(), ")");
        sb.append(" values ").append(pss.toString()).append(";");
        SqlQuery sqlQuery = new SqlQuery(sb.toString());
        sqlQuery.setParameterList(parameters);
        sqlQuery.setTypeIndexMap(getTypeIndexMap());
        return sqlQuery;
    }

    private SqlQuery buildDeleteQuery() {
        LinkedList<Object> parameters = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ").append(getSchema()).append(".").append(deleteTable);
        if (joinBuilder != null) {
            sb.append(" ").append(joinBuilder.toString());
        }
        if (whereQuery != null) {
            sb.append(" where ").append(whereQuery.toString());
            parameters.addAll(whereQuery.getParameterList());
        }
        sb.append(";");
        SqlQuery sqlQuery = new SqlQuery(sb.toString());
        sqlQuery.setParameterList(parameters);
        return sqlQuery;
    }

    private List<String> getUpdateColumns() {
        if (updateColumns == null) {
            updateColumns = new ArrayList<>();
        }
        return updateColumns;
    }

    private List<Object> getUpdateParameters() {
        if (updateParameters == null) {
            updateParameters = new ArrayList<>();
        }
        return updateParameters;
    }

    private List<String> getInsertColumns() {
        if (insertColumns == null) {
            insertColumns = new ArrayList<>();
        }
        return insertColumns;
    }

    private List<Object> getInsertParameters() {
        if (insertParameters == null) {
            insertParameters = new ArrayList<>();
        }
        return insertParameters;
    }

    private StringBuilder getJoinBuilder() {
        if (joinBuilder == null) {
            joinBuilder = new StringBuilder();
        }
        return joinBuilder;
    }

    private WhereQuery getWhereQuery() {
        if (whereQuery == null) {
            whereQuery = new WhereQuery();
        }
        return whereQuery;
    }
}
