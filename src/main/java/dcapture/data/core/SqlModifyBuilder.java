package dcapture.data.core;

import java.sql.SQLException;

/**
 * Sql Modify Builder
 */
public interface SqlModifyBuilder {
    String getSchema();

    SqlQuery getSqlQuery();

    SqlModifyBuilder update(String tableName);

    SqlModifyBuilder update(Class<?> tableClass);

    SqlModifyBuilder updateColumn(SqlColumn sqlColumn, Object object);

    SqlModifyBuilder updateColumn(String columnName, Object object) throws SQLException;

    SqlModifyBuilder deleteFrom(String tableName);

    SqlModifyBuilder deleteFrom(Class<?> tableClass);

    SqlModifyBuilder insertInto(String tableName);

    SqlModifyBuilder insertColumn(SqlColumn sqlColumn, Object object);

    SqlModifyBuilder join(String joinQuery);

    SqlModifyBuilder where(WhereQuery whereQuery);
}
