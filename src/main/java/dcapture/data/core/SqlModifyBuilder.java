package dcapture.data.core;

/**
 * Sql Modify Builder
 */
public interface SqlModifyBuilder {
    String getSchema();

    SqlQuery getSqlQuery();

    SqlModifyBuilder update(String tableName);

    SqlModifyBuilder update(Class<?> tableClass);

    SqlModifyBuilder updateColumn(String column, Object object);

    SqlModifyBuilder deleteFrom(String tableName);

    SqlModifyBuilder deleteFrom(Class<?> tableClass);

    SqlModifyBuilder insertInto(String tableName);

    SqlModifyBuilder insertColumns(String column, Object object);

    SqlModifyBuilder join(String joinQuery);

    SqlModifyBuilder where(WhereQuery whereQuery);
}
