package dcapture.data.core;

import java.sql.SQLType;
import java.util.LinkedList;
import java.util.Map;

/**
 * SqlQuery
 */
public class SqlQuery {
    private String query;
    private LinkedList<Object> parameterList;
    private Map<Integer, SQLType> typeIndexMap;

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

    public LinkedList<Object> getParameterList() {
        return parameterList;
    }

    public void setParameterList(LinkedList<Object> parameterList) {
        this.parameterList = parameterList;
    }

    public Map<Integer, SQLType> getTypeIndexMap() {
        return typeIndexMap;
    }

    public void setTypeIndexMap(Map<Integer, SQLType> typeIndexMap) {
        this.typeIndexMap = typeIndexMap;
    }

    @Override
    public String toString() {
        return query;
    }
}
