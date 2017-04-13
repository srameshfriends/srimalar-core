package dcapture.data.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Where Query
 *
 * @author Ramesh
 * @since Oct, 2016
 */
public class WhereQuery {
    private List<String> queryList;
    private List<Object> parameterList;

    public WhereQuery() {
        parameterList = new ArrayList<>();
        queryList = new ArrayList<>();
    }

    public WhereQuery whereAndEqual(String query, Object parameter) {
        queryList.add(" and " + query + " = ?");
        parameterList.add(parameter);
        return WhereQuery.this;
    }

    public WhereQuery whereOrEqual(String query, Object parameter) {
        queryList.add(" or " + query + " = ?");
        parameterList.add(parameter);
        return WhereQuery.this;
    }

    public WhereQuery whereOrIn(String query, Object[] parameters) {
        queryList.add(" or " + query + " in " + buildInArray(parameters.length));
        Collections.addAll(parameterList, parameters);
        return WhereQuery.this;
    }

    public WhereQuery whereOrIn(String query, List<Object> parameters) {
        queryList.add(" or " + query + " in " + buildInArray(parameters.size()));
        parameterList.addAll(parameters);
        return WhereQuery.this;
    }

    public WhereQuery whereAndIn(String query, Object[] parameters) {
        queryList.add(" and " + query + " in " + buildInArray(parameters.length));
        Collections.addAll(parameterList, parameters);
        return WhereQuery.this;
    }

    public WhereQuery whereAndIn(String query, List<Object> parameters) {
        queryList.add(" and " + query + " in " + buildInArray(parameters.size()));
        parameterList.addAll(parameters);
        return WhereQuery.this;
    }

    public WhereQuery where(String query, Object parameter) {
        queryList.add(" and " + query + " = ? ");
        parameterList.add(parameter);
        return WhereQuery.this;
    }

    public WhereQuery where(SearchTextQuery searchTextQuery) {
        queryList.add(" and " + searchTextQuery.toString());
        for (int index = 0; index < searchTextQuery.getColumnSize(); index++) {
            parameterList.add(searchTextQuery.getSearchText());
        }
        return WhereQuery.this;
    }

    private String buildInArray(int length) {
        StringBuilder sb = new StringBuilder("(");
        while (0 < length) {
            sb.append(",?");
            length = length - 1;
        }
        return sb.toString().replaceFirst(",", ")");
    }

    public List<Object> getParameterList() {
        return parameterList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String query : queryList) {
            sb.append(query);
        }
        String query = sb.toString();
        if (query.startsWith(" and ")) {
            query = query.replaceFirst(" and ", " ");
        }
        if (query.startsWith(" or ")) {
            query = query.replaceFirst(" or ", " ");
        }
        return query;
    }
}
