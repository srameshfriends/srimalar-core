package dcapture.data.postgres;

import dcapture.data.core.*;
import dcapture.data.htwo.H2SelectBuilder;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sql Reader
 */
public class PostgresReader implements SqlReader {
    private static final Logger logger = Logger.getLogger(PostgresReader.class);
    private final PostgresProcessor processor;

    PostgresReader(PostgresProcessor processor) {
        this.processor = processor;
    }

    private SqlProcessor getProcessor() {
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

    @Override
    public Object objectValue(SqlQuery sql) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        logging(sql);
        try {
            connection = processor.getConnectionPool().getConnection();
            statement = connection.prepareStatement(sql.toString());
            addParameter(statement, sql.getParameterList());
            resultSet = statement.executeQuery();
            Object data = null;
            if (resultSet.next()) {
                data = resultSet.getObject(1);
            }
            close(resultSet, statement, connection);
            return data;
        } catch (SQLException ex) {
            close(resultSet, statement, connection);
            throw ex;
        }
    }

    @Override
    public Object[] objectArray(SqlQuery sql) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        logging(sql);
        try {
            connection = processor.getConnectionPool().getConnection();
            statement = connection.prepareStatement(sql.toString());
            addParameter(statement, sql.getParameterList());
            result = statement.executeQuery();
            Object[] dataArray = objectArray(result);
            close(result, statement, connection);
            return dataArray;
        } catch (SQLException ex) {
            close(result, statement, connection);
            throw ex;
        }
    }

    @Override
    public List<Object> objectList(SqlQuery sql) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        logging(sql);
        try {
            connection = processor.getConnectionPool().getConnection();
            statement = connection.prepareStatement(sql.toString());
            addParameter(statement, sql.getParameterList());
            resultSet = statement.executeQuery();
            List<Object> dataList = objectList(resultSet);
            close(resultSet, statement, connection);
            return dataList;
        } catch (SQLException ex) {
            close(resultSet, statement, connection);
            throw ex;
        }
    }

    @Override
    public List<Object[]> objectArrayList(SqlQuery sql) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        logging(sql);
        try {
            connection = processor.getConnectionPool().getConnection();
            statement = connection.prepareStatement(sql.toString());
            addParameter(statement, sql.getParameterList());
            result = statement.executeQuery();
            List<Object[]> resultList = objectArrayList(result);
            close(result, statement, connection);
            return resultList;
        } catch (SQLException ex) {
            close(result, statement, connection);
            throw ex;
        }
    }

    @Override
    public SqlMetaDataResult sqlMetaDataResult(SqlQuery sql) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        SqlMetaDataResult dataResult = null;
        logging(sql);
        try {
            connection = processor.getConnectionPool().getConnection();
            statement = connection.prepareStatement(sql.toString());
            addParameter(statement, sql.getParameterList());
            result = statement.executeQuery();
            SqlMetaData[] metaData = getMetaData(result);
            List<Object[]> resultList = objectArrayList(result);
            dataResult = new SqlMetaDataResult(metaData, resultList);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close(result, statement, connection);
        }
        return dataResult;
    }

    @Override
    public String textValue(SqlQuery query) throws SQLException {
        return (String) objectValue(query);
    }

    @Override
    public String[] textArray(SqlQuery query) throws SQLException {
        return textArray(objectArray(query));
    }

    @Override
    public List<String> textList(SqlQuery query) throws SQLException {
        List<Object> objList = objectList(query);
        return objList.stream().map(obj -> (String) obj).collect(Collectors.toList());
    }

    @Override
    public List<String[]> textArrayList(SqlQuery query) throws SQLException {
        List<Object[]> objArrList = objectArrayList(query);
        List<String[]> result = new ArrayList<>();
        for (Object[] objArr : objArrList) {
            result.add(textArray(objArr));
        }
        return result;
    }

    @Override
    public int intValue(SqlQuery query) throws SQLException {
        Integer val = (Integer) objectValue(query);
        return val == null ? 0 : val;
    }

    @Override
    public int[] intArray(SqlQuery query) throws SQLException {
        return intArray(objectArray(query));
    }

    @Override
    public List<Integer> integerList(SqlQuery query) throws SQLException {
        List<Object> objList = objectList(query);
        return objList.stream().map(obj -> (Integer) obj).collect(Collectors.toList());
    }

    @Override
    public long longValue(SqlQuery query) throws SQLException {
        Long val = (Long) objectValue(query);
        return val == null ? 0 : val;
    }

    @Override
    public long[] longArray(SqlQuery query) throws SQLException {
        return longArray(objectArray(query));
    }

    @Override
    public List<Long> longList(SqlQuery query) throws SQLException {
        List<Object> objList = objectList(query);
        return objList.stream().map(obj -> (Long) obj).collect(Collectors.toList());
    }

    @Override
    public BigDecimal bigDecimalValue(SqlQuery query) throws SQLException {
        return (BigDecimal) objectValue(query);
    }

    @Override
    public BigDecimal[] bigDecimalArray(SqlQuery query) throws SQLException {
        return bigDecimalArray(objectArray(query));
    }

    @Override
    public List<BigDecimal> bigDecimalList(SqlQuery query) throws SQLException {
        List<Object> objList = objectList(query);
        return objList.stream().map(obj -> (BigDecimal) obj).collect(Collectors.toList());
    }

    @Override
    public List<BigDecimal[]> bigDecimalArrayList(SqlQuery query) throws SQLException {
        List<Object[]> objArrList = objectArrayList(query);
        List<BigDecimal[]> result = new ArrayList<>();
        for (Object[] objArr : objArrList) {
            result.add(bigDecimalArray(objArr));
        }
        return result;
    }

    @Override
    public SqlReference getUsedReference(Class<?> entityClass, long id) throws SQLException {
        List<SqlReference> referenceList = 1 > id ? null : getProcessor().getSqlReference(entityClass);
        if (referenceList == null) {
            return null;
        }
        SqlReference result = null;
        for (SqlReference reference : referenceList) {
            H2SelectBuilder builder = new H2SelectBuilder(getProcessor());
            builder.selectAll(reference.getReferenceTable().getType()).where(
                    reference.getReferenceColumn().getName(), id);
            SqlMetaDataResult metaDataResult = sqlMetaDataResult(builder.getSelectQuery());
            Object value = getProcessor().toEntity(metaDataResult);
            if (value != null) {
                result = SerializationUtils.clone(reference);
                result.setValue(value);
                break;
            }
        }
        return result;
    }

    @Override
    public SqlSelectBuilder selectAll(Class<?> tableClass) {
        SqlTable sqlTable = getProcessor().getSqlTable(tableClass);
        H2SelectBuilder builder = new H2SelectBuilder(getProcessor());
        builder.selectFrom(sqlTable.getName());
        builder.selectColumns(sqlTable.getColumns());
        return builder;
    }

    @Override
    public SqlSelectBuilder selectFrom(Class<?> tableClass) {
        SqlTable sqlTable = getProcessor().getSqlTable(tableClass);
        H2SelectBuilder builder = new H2SelectBuilder(getProcessor());
        builder.selectFrom(sqlTable.getName());
        return builder;
    }

    private List<Object> objectList(ResultSet rs) throws SQLException {
        List<Object> dataList = new ArrayList<>();
        while (rs.next()) {
            Object data = rs.getObject(1);
            dataList.add(data);
        }
        return dataList;
    }

    private List<Object[]> objectArrayList(ResultSet rs) throws SQLException {
        final int columnCount = rs.getMetaData().getColumnCount();
        List<Object[]> dataList = new ArrayList<>();
        while (rs.next()) {
            Object[] data = new Object[columnCount];
            for (int col = 0; col < columnCount; col++) {
                data[col] = rs.getObject(col + 1);
            }
            dataList.add(data);
        }
        return dataList;
    }

    private Object[] objectArray(ResultSet result) throws SQLException {
        int columnCount = result.getMetaData().getColumnCount();
        if (result.next()) {
            Object[] data = new Object[columnCount];
            for (int col = 0; col < columnCount; col++) {
                data[col] = result.getObject(col + 1);
            }
            return data;
        }
        return null;
    }

    private String[] textArray(Object[] objectArray) {
        String[] data = new String[objectArray.length];
        for (int ix = 0; ix < data.length; ix++) {
            data[ix] = (String) objectArray[ix];
        }
        return data;
    }

    private int[] intArray(Object[] objectArray) {
        int[] data = new int[objectArray.length];
        for (int ix = 0; ix < data.length; ix++) {
            data[ix] = (Integer) objectArray[ix];
        }
        return data;
    }

    private long[] longArray(Object[] objectArray) {
        long[] data = new long[objectArray.length];
        for (int ix = 0; ix < data.length; ix++) {
            data[ix] = (Long) objectArray[ix];
        }
        return data;
    }

    private BigDecimal[] bigDecimalArray(Object[] objectArray) {
        BigDecimal[] data = new BigDecimal[objectArray.length];
        for (int ix = 0; ix < data.length; ix++) {
            data[ix] = (BigDecimal) objectArray[ix];
        }
        return data;
    }

    private SqlMetaData[] getMetaData(ResultSet rs) throws SQLException {
        ResultSetMetaData rsMetaData = rs.getMetaData();
        SqlMetaData[] metaDataArray = new SqlMetaData[rsMetaData.getColumnCount()];
        for (int index = 1; index <= metaDataArray.length; index++) {
            SqlMetaData metaData = new SqlMetaData();
            metaData.setColumnIndex(index);
            metaData.setTableName(rsMetaData.getTableName(index).toLowerCase());
            metaData.setColumnName(rsMetaData.getColumnName(index).toLowerCase());
            metaData.setColumnType(rsMetaData.getColumnType(index));
            metaDataArray[index - 1] = metaData;
        }
        return metaDataArray;
    }

    private void close(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception ex) {
                // ignore
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception ex) {
                // ignore
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    private void addParameter(PreparedStatement statement, List<Object> objects) throws SQLException {
        int index = 1;
        for (Object parameter : objects) {
            if (parameter == null) {
                statement.setString(index, null);
            } else if (parameter instanceof String) {
                statement.setString(index, (String) parameter);
            } else if (parameter instanceof Integer) {
                statement.setInt(index, (Integer) parameter);
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
