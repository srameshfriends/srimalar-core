package dcapture.data.core;

import java.util.LinkedList;
import java.util.List;

/**
 * SqlQuery
 */
public class SqlQuery {
    private String query;
    private LinkedList<Object> parameterList;

    public SqlQuery() {
        this("Database query has not found");
    }

    public SqlQuery(String query) {
        parameterList = new LinkedList<>();
        setQuery(query);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setParameterList(LinkedList<Object> parameterList) {
        this.parameterList = parameterList;
    }

    public LinkedList<Object> getParameterList() {
        return parameterList;
    }

    @Override
    public String toString() {
        return query;
    }
}
