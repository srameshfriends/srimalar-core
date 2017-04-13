package dcapture.data.core;

import java.sql.SQLException;
import java.util.List;

/**
 * Sql Processor
 */
public interface SqlProcessor {

    String getSchema();

    void runForwardTool() throws SQLException;

    SqlSelectBuilder createSqlSelectBuilder();

    SqlModifyBuilder createQueryBuilder();

    SqlReader getSqlReader();

    SqlTransaction getSqlTransaction();

    SqlTable getSqlTable(Class<?> tableClass);

    SqlTableMap getSqlTableMap();

    SqlColumn getReferenceSqlColumn(Class<?> tableClass, String column);

    List<SqlReference> getSqlReference(Class<?> entityClass);

    SqlQuery createSchemaQuery();

    List<SqlQuery> createTableQueries();

    List<SqlQuery> alterTableQueries();

    <T> T toEntity(SqlMetaDataResult metaDataResult);

    <T> List<T> toEntityList(SqlMetaDataResult dataResult);
}
