package dcapture.data.htwo;

import dcapture.data.core.*;
import org.h2.jdbcx.JdbcConnectionPool;

import javax.persistence.TemporalType;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * H2 Processor
 */
public final class H2Processor extends SqlFactory {
    private JdbcConnectionPool connectionPool;
    private H2Reader reader;
    private H2Transaction transaction;

    public void initialize(File persistenceFile, JdbcConnectionPool pool, String schema) {
        initialize(schema, persistenceFile);
        this.connectionPool = pool;
        reader = new H2Reader(this);
        transaction = new H2Transaction(this);
    }

    JdbcConnectionPool getConnectionPool() throws SQLException {
        return connectionPool;
    }

    public SqlModifyBuilder createQueryBuilder() {
        return new H2ModifyBuilder(this);
    }

    @Override
    public SqlTable getSqlTable(Class<?> tableClass) {
        return getSqlTableMap().getSqlTable(tableClass);
    }

    @Override
    public SqlReader getSqlReader() {
        return reader;
    }

    @Override
    public SqlSelectBuilder createSqlSelectBuilder() {
        return new H2SelectBuilder(this);
    }

    @Override
    public SqlColumn getReferenceSqlColumn(Class<?> tableClass, String column) {
        SqlTable sqlTable = getSqlTable(tableClass);
        column = column.toLowerCase();
        for (SqlColumn sqlColumn : sqlTable.getSqlColumnList()) {
            if (column.equals(sqlColumn.getName().toLowerCase())) {
                return sqlColumn;
            }
        }
        return null;
    }

    @Override
    public List<SqlReference> getSqlReference(Class<?> entityClass) {
        return null;
    }

    @Override
    public SqlQuery createSchemaQuery() {
        SqlQuery query = new SqlQuery();
        query.setQuery("create schema if not exists " + getSchema() + ";");
        return query;
    }

    @Override
    public List<SqlQuery> createTableQueries() {
        List<SqlQuery> queryList = new ArrayList<>();
        for (SqlTable table : getSqlTableMap().getSqlTableList()) {
            SqlQuery sqlQuery = new SqlQuery();
            sqlQuery.setQuery(createTableQuery(table));
            queryList.add(sqlQuery);
        }
        return queryList;
    }

    @Override
    public List<SqlQuery> alterTableQueries() {
        List<SqlQuery> queryList = new ArrayList<>();
        for (SqlTable table : getSqlTableMap().getSqlTableList()) {
            List<String> alterList = alterTableQuery(table);
            for (String alter : alterList) {
                SqlQuery query = new SqlQuery();
                query.setQuery(alter);
                queryList.add(query);
            }
        }
        return queryList;
    }

    @Override
    public SqlTransaction getSqlTransaction() {
        return transaction;
    }

    private String createTableQuery(SqlTable sqlTable) {
        String table = sqlTable.getName();
        SqlColumn primaryColumn = sqlTable.getPrimaryColumn();
        List<SqlColumn> columnList = transaction.getColumnListWithoutPK(sqlTable);
        StringBuilder builder = new StringBuilder("create table if not exists ");
        builder.append(getSchema()).append('.').append(table).append("(");
        builder.append(primaryColumn.getName()).append(" ").append(getDataType(primaryColumn))
                .append(" primary key auto_increment, ");
        for (SqlColumn column : columnList) {
            builder.append(column.getName()).append(" ").append(getDataType(column)).append(", ");
        }
        builder.replace(builder.length() - 2, builder.length(), " ");
        builder.append(");");
        return builder.toString();
    }

    private List<String> alterTableQuery(SqlTable sqlTable) {
        List<String> referenceList = new ArrayList<>();
        for (SqlColumn column : sqlTable.getSqlColumnList()) {
            if (column.getJoinTable() != null) {
                StringBuilder builder = new StringBuilder("alter table ");
                builder.append(getSchema()).append('.').append(sqlTable.getName()).append(" add foreign key ");
                builder.append("(").append(column.getName()).append(") ");
                builder.append(" references ");
                SqlTable joinTable = column.getJoinTable();
                builder.append(getSchema()).append(".").append(joinTable.getName()).append("(")
                        .append(joinTable.getPrimaryColumn().getName()).append(");");
                referenceList.add(builder.toString());
            }
        }
        return referenceList;
    }

    private int getMaxTextLength() {
        return 516;
    }

    private int getEnumLength() {
        return 16;
    }

    private String getDataType(final SqlColumn column) {
        final Class<?> type = column.getType();
        if (String.class.equals(type)) {
            String suffix = column.isNullable() ? "" : " not null";
            if (getMaxTextLength() < column.getLength()) {
                return "text".concat(suffix);
            }
            return "varchar(" + column.getLength() + ")" + suffix;
        } else if (Date.class.equals(type)) {
            if (column.getTemporalType() != null && TemporalType.TIMESTAMP.equals(column.getTemporalType())) {
                return "timestamp";
            }
            return "date";
        } else if (BigDecimal.class.equals(type)) {
            return "decimal";
        } else if (int.class.equals(type)) {
            return "integer";
        } else if (boolean.class.equals(type)) {
            return "boolean";
        } else if (double.class.equals(type)) {
            return "double";
        } else if (Enum.class.isAssignableFrom(type)) {
            return "varchar(" + getEnumLength() + ")";
        } else if (long.class.equals(type)) {
            return "bigint";
        } else if (Short.class.equals(type)) {
            return "smallint";
        } else if (Byte.class.equals(type)) {
            return "binary";
        } else if (Integer.class.equals(type)) {
            return "integer";
        } else if (Boolean.class.equals(type)) {
            return "boolean";
        } else if (Double.class.equals(type)) {
            return "double";
        } else if (Long.class.equals(type)) {
            return "bigint";
        }
        throw new IllegalArgumentException("Unknown data type " + column.getName());
    }
}
