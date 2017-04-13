package dcapture.data.core;

import java.util.List;

/**
 * Sql Select Builder
 */
public interface SqlSelectBuilder {
    void setSqlProcessor(SqlProcessor processor);

    SqlQuery getSelectQuery();

    SqlQuery getCountQuery();

    SqlSelectBuilder selectAll(Class<?> tableClass);

    SqlSelectBuilder selectFrom(Class<?> tableClass);

    SqlSelectBuilder selectColumns(String... columns);

    SqlSelectBuilder selectFrom(String table);

    SqlSelectBuilder join(String joinQuery);

    SqlSelectBuilder where(String column, Object parameter);

    SqlSelectBuilder where(SearchTextQuery searchTextQuery);

    SqlSelectBuilder whereOrIn(String query, List<Object> parameters);

    SqlSelectBuilder whereOrIn(String query, Object[] parameters);

    SqlSelectBuilder whereAndIn(String query, List<Object> parameters);

    SqlSelectBuilder whereAndIn(String query, Object[] parameters);

    SqlSelectBuilder groupBy(String... columns);

    SqlSelectBuilder orderBy(String... columns);

    SqlSelectBuilder orderByDESC(String... columns);

    SqlSelectBuilder limit(int limit);

    SqlSelectBuilder offset(long offset);
}
