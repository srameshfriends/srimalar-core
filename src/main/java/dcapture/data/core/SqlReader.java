package dcapture.data.core;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Sql Reader
 */
public interface SqlReader {

    Object objectValue(SqlQuery query) throws SQLException;

    Object[] objectArray(SqlQuery query) throws SQLException;

    List<Object> objectList(SqlQuery query) throws SQLException;

    List<Object[]> objectArrayList(SqlQuery query) throws SQLException;

    SqlMetaDataResult sqlMetaDataResult(SqlQuery query) throws SQLException;

    String textValue(SqlQuery query) throws SQLException;

    String[] textArray(SqlQuery query) throws SQLException;

    List<String> textList(SqlQuery query) throws SQLException;

    List<String[]> textArrayList(SqlQuery query) throws SQLException;

    int intValue(SqlQuery query) throws SQLException;

    int[] intArray(SqlQuery query) throws SQLException;

    List<Integer> integerList(SqlQuery query) throws SQLException;

    long longValue(SqlQuery query) throws SQLException;

    long[] longArray(SqlQuery query) throws SQLException;

    List<Long> longList(SqlQuery query) throws SQLException;

    BigDecimal bigDecimalValue(SqlQuery query) throws SQLException;

    BigDecimal[] bigDecimalArray(SqlQuery query) throws SQLException;

    List<BigDecimal> bigDecimalList(SqlQuery query) throws SQLException;

    List<BigDecimal[]> bigDecimalArrayList(SqlQuery query) throws SQLException;

    SqlReference getUsedReference(Class<?> entityClass, long id) throws SQLException;

    SqlSelectBuilder selectAll(Class<?> tableClass);

    SqlSelectBuilder selectFrom(Class<?> tableClass);
}
