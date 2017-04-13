package dcapture.data.core.htwo;

import dcapture.data.core.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * H2 Select Builder
 */
public class H2SelectBuilder implements SqlSelectBuilder {
    private SqlProcessor sqlProcessor;
    private String selectTable;
    private List<String> selectColumns, orderByColumns, groupByColumns;
    private WhereQuery whereQuery;
    private StringBuilder joinBuilder;
    private int limit = -1;
    private long offset = -1;

    public H2SelectBuilder(SqlProcessor sqlProcessor) {
        this.sqlProcessor = sqlProcessor;
    }

    @Override
    public void setSqlProcessor(SqlProcessor processor) {
        this.sqlProcessor = processor;
    }

    @Override
    public SqlSelectBuilder selectAll(Class<?> tableClass) {
        SqlTable sqlTable = sqlProcessor.getSqlTable(tableClass);
        selectFrom(sqlTable.getName());
        Set<String> columnSet = sqlTable.getColumnFieldMap().keySet();
        selectColumns(columnSet.toArray(new String[]{}));
        return H2SelectBuilder.this;
    }

    @Override
    public SqlSelectBuilder selectFrom(Class<?> tableClass) {
        SqlTable sqlTable = sqlProcessor.getSqlTable(tableClass);
        selectFrom(sqlTable.getName());
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder selectColumns(String... columns) {
        for (String col : columns) {
            getSelectColumns().add(col);
        }
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder selectFrom(String table) {
        selectTable = table;
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder join(String joinQuery) {
        getJoinBuilder().append(joinQuery);
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder where(String column, Object parameter) {
        getWhereQuery().where(column, parameter);
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder where(SearchTextQuery searchTextQuery) {
        getWhereQuery().where(searchTextQuery);
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder whereOrIn(String query, List<Object> parameters) {
        getWhereQuery().whereOrIn(query, parameters);
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder whereOrIn(String query, Object[] parameters) {
        getWhereQuery().whereOrIn(query, parameters);
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder whereAndIn(String query, List<Object> parameters) {
        getWhereQuery().whereOrIn(query, parameters);
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder whereAndIn(String query, Object[] parameters) {
        getWhereQuery().whereOrIn(query, parameters);
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder groupBy(String... columns) {
        for (String col : columns) {
            getOrderByColumns().add(col);
        }
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder orderBy(String... columns) {
        for (String col : columns) {
            getOrderByColumns().add(col);
        }
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder orderByDESC(String... columns) {
        for (String col : columns) {
            getOrderByColumns().add(col + " desc");
        }
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder limit(int limit) {
        this.limit = limit;
        return H2SelectBuilder.this;
    }

    @Override
    public H2SelectBuilder offset(long offset) {
        this.offset = offset;
        return H2SelectBuilder.this;
    }

    @Override
    public SqlQuery getCountQuery() {
        LinkedList<Object> parameters = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("select count(").append(selectColumns.get(0)).append(") ");
        sb.append(buildFromSQ()).append(buildWhereSQ(parameters)).append(";");
        SqlQuery sqlQuery = new SqlQuery(sb.toString());
        sqlQuery.setParameterList(parameters);
        return sqlQuery;
    }

    @Override
    public SqlQuery getSelectQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (String sel : selectColumns) {
            sb.append(sel).append(",");
        }
        LinkedList<Object> parameters = new LinkedList<>();
        sb.replace(sb.toString().length() - 1, sb.toString().length(), " ");
        sb.append(buildFromSQ()).append(buildWhereSQ(parameters)).append(buildGroupBySQ())
                .append(buildOrderBySQ()).append(buildLimitSQ());
        sb.append(";");
        //
        SqlQuery sqlQuery = new SqlQuery(sb.toString());
        sqlQuery.setParameterList(parameters);
        return sqlQuery;
    }

    private String getSchema() {
        return sqlProcessor.getSchema();
    }

    private String buildFromSQ() {
        StringBuilder sb = new StringBuilder(" ");
        sb.append(" from ").append(getSchema()).append(".").append(selectTable);
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

    private String buildOrderBySQ() {
        if (orderByColumns != null) {
            StringBuilder orderByBuild = new StringBuilder(" order by ");
            for (String orderBy : orderByColumns) {
                orderByBuild.append(orderBy).append(",");
            }
            orderByBuild.replace(orderByBuild.length() - 1, orderByBuild.length(), " ");
            return orderByBuild.toString();
        }
        return "";
    }

    private String buildGroupBySQ() {
        if (groupByColumns != null) {
            StringBuilder builder = new StringBuilder();
            for (String groupBy : groupByColumns) {
                builder.append(" group by ").append(groupBy).append(",");
            }
            builder.replace(builder.length() - 1, builder.length(), " ");
            return builder.toString();
        }
        return "";
    }

    private String buildLimitSQ() {
        String sb = " ";
        if (0 < limit) {
            sb = sb.concat(" limit ").concat(limit + "");
        }
        if (0 < offset) {
            sb = sb.concat(" offset ").concat(offset + "");
        }
        return sb;
    }

    private List<String> getSelectColumns() {
        if (selectColumns == null) {
            selectColumns = new ArrayList<>();
        }
        return selectColumns;
    }

    private StringBuilder getJoinBuilder() {
        if (joinBuilder == null) {
            joinBuilder = new StringBuilder();
        }
        return joinBuilder;
    }

    private List<String> getOrderByColumns() {
        if (orderByColumns == null) {
            orderByColumns = new ArrayList<>();
        }
        return orderByColumns;
    }

    private WhereQuery getWhereQuery() {
        if (whereQuery == null) {
            whereQuery = new WhereQuery();
        }
        return whereQuery;
    }
}
