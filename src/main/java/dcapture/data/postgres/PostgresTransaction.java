package dcapture.data.postgres;

import dcapture.data.core.*;
import jodd.bean.BeanUtilBean;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Postgres Transaction
 */
public class PostgresTransaction extends AbstractTransaction implements SqlTransaction {
    private static final Logger logger = Logger.getLogger(PostgresTransaction.class);
    private final PostgresProcessor processor;

    PostgresTransaction(PostgresProcessor processor) {
        this.processor = processor;
    }

    private PostgresProcessor getProcessor() {
        return processor;
    }

    private void logging(SqlQuery sql) {
        if (logger.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder(sql.toString()).append(" \t ");
            if (!sql.getParameterList().isEmpty()) {
                for (Object parameter : sql.getParameterList()) {
                    builder.append(parameter).append("\t");
                }
            }
            logger.debug(builder.toString());
        }
    }

    private void logging(String log) {
        if (logger.isDebugEnabled()) {
            logger.debug("" + log);
        }
    }

    private void logging(LinkedList<Object> parameterList) {
        if (logger.isDebugEnabled() && !parameterList.isEmpty()) {
            StringBuilder builder = new StringBuilder("\n");
            for (Object parameter : parameterList) {
                builder.append(parameter).append("\t");
            }
            logger.debug(builder.toString());
        }
    }

    @Override
    public void executeBatch(SqlQuery sqlQuery) throws SQLException {
        Connection connection = getProcessor().getConnectionPool().getConnection();
        connection.setAutoCommit(false);
        logging(sqlQuery);
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery.toString());
            addParameter(statement, sqlQuery);
            statement.addBatch();
            statement.executeBatch();
            connection.commit();
            close(statement);
            close(connection);
        } catch (SQLException ex) {
            rollback(connection);
            throw ex;
        }
    }

    @Override
    public void executeCommit(SqlQuery sqlQuery) throws SQLException {
        Connection connection = getProcessor().getConnectionPool().getConnection();
        logging(sqlQuery);
        try {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(sqlQuery.toString());
            addParameter(statement, sqlQuery);
            statement.execute();
            connection.commit();
            close(connection);
        } catch (SQLException ex) {
            rollback(connection);
            throw ex;
        }
    }

    @Override
    public SqlQuery insertQuery(Object object) {
        SqlTable table = getProcessor().getSqlTable(object.getClass());
        PostgresModifyBuilder builder = new PostgresModifyBuilder(getProcessor());
        builder.insertInto(table.getName());
        for (SqlColumn sqlColumn : table.getSqlColumnList()) {
            Object fieldValue = getFieldObject(object, sqlColumn.getName());
            builder.insertColumn(sqlColumn, fieldValue);
        }
        return builder.getSqlQuery();
    }

    @Override
    public SqlQuery updateQuery(Object object) {
        SqlTable table = getProcessor().getSqlTable(object.getClass());
        PostgresModifyBuilder builder = new PostgresModifyBuilder(getProcessor());
        builder.update(table.getName());
        for (SqlColumn sqlColumn : getColumnListWithoutPK(table)) {
            Object fieldValue = getFieldObject(object, sqlColumn.getName());
            builder.updateColumn(sqlColumn, fieldValue);
        }
        Object value = BeanUtilBean.pojo.getProperty(object, table.getPrimaryColumn().getName());
        WhereQuery whereQuery = new WhereQuery();
        whereQuery.where(table.getPrimaryColumn().getName(), value);
        builder.where(whereQuery);
        return builder.getSqlQuery();
    }

    @Override
    public SqlQuery deleteQuery(Object object) {
        SqlTable table = getProcessor().getSqlTable(object.getClass());
        PostgresModifyBuilder builder = new PostgresModifyBuilder(getProcessor());
        builder.deleteFrom(table.getName());
        SqlColumn sqlColumn = table.getPrimaryColumn();
        Object fieldValue = getFieldObject(object, sqlColumn.getName());
        WhereQuery whereQuery = new WhereQuery();
        whereQuery.where(sqlColumn.getName(), fieldValue);
        builder.where(whereQuery);
        return builder.getSqlQuery();
    }

    @Override
    public void executeBatch(List<SqlQuery> queries) throws SQLException {
        Connection connection = getProcessor().getConnectionPool().getConnection();
        connection.setAutoCommit(false);
        logging(" *** Batch Execute *** ");
        try {
            SqlQuery sqlQuery = queries.get(0);
            logging(sqlQuery);
            PreparedStatement statement = connection.prepareStatement(sqlQuery.toString());
            for (SqlQuery sql : queries) {
                logging(sql.getParameterList());
                addParameter(statement, sql);
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
            close(statement);
            close(connection);
        } catch (SQLException ex) {
            rollback(connection);
            throw ex;
        }
    }

    @Override
    public void executeCommit(List<SqlQuery> queries) throws SQLException {
        Connection connection = getProcessor().getConnectionPool().getConnection();
        connection.setAutoCommit(false);
        try {
            for (SqlQuery sql : queries) {
                logging(sql);
                PreparedStatement statement = connection.prepareStatement(sql.toString());
                addParameter(statement, sql);
                statement.execute();
            }
            connection.commit();
            close(connection);
        } catch (SQLException ex) {
            rollback(connection);
            throw ex;
        }
    }

    private void rollback(Connection connection) {
        try {
            if (connection == null) {
                logger.info("Connection should not be null");
            } else {
                logger.info("Connection is not a problem");
                connection.rollback();
            }

        } catch (SQLException ex) {
            // ignore
        }
    }

    private void close(Statement statement) {
        try {
            statement.close();
        } catch (Exception ex) {
            // ignore
        }
    }

    private void close(Connection connection) {
        try {
            connection.close();
        } catch (Exception ex) {
            // ignore
        }
    }

    private void addParameter(PreparedStatement statement, SqlQuery sqlQuery) throws SQLException {
        Map<Integer, SQLType> sqlTypeMap = sqlQuery.getTypeIndexMap();
        if (sqlTypeMap == null) {
            sqlTypeMap = new HashMap<>();
        }
        int index = 1;
        for (Object parameter : sqlQuery.getParameterList()) {
            SQLType sqlType = sqlTypeMap.get(index);
            if (sqlType != null) {
                if (parameter == null) {
                    statement.setNull(index, sqlType.getVendorTypeNumber());
                } else if (parameter instanceof java.util.Date) {
                    statement.setDate(index, toSqlDate((java.util.Date) parameter));
                } else {
                    statement.setObject(index, parameter, sqlType.getVendorTypeNumber());
                }
            } else if (parameter instanceof String) {
                statement.setString(index, (String) parameter);
            } else if (parameter instanceof Long) {
                statement.setLong(index, (Long) parameter);
            } else if (parameter instanceof Integer) {
                statement.setInt(index, (Integer) parameter);
            } else if (parameter instanceof Double) {
                statement.setDouble(index, (Double) parameter);
            } else if (parameter instanceof BigDecimal) {
                statement.setBigDecimal(index, (BigDecimal) parameter);
            } else if (parameter instanceof Boolean) {
                statement.setBoolean(index, (Boolean) parameter);
            } else if (parameter instanceof java.util.Date) {
                statement.setDate(index, toSqlDate((java.util.Date) parameter));
            } else {
                statement.setString(index, parameter.toString());
            }
            index += 1;
        }
    }

    private Date toSqlDate(java.util.Date sqlDate) {
        return new Date(sqlDate.getTime());
    }
}
