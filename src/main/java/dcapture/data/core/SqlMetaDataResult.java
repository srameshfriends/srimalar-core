package dcapture.data.core;

import java.util.List;

/**
 * Sql Meta Data Result
 */
public class SqlMetaDataResult {
    private SqlMetaData[] metaData;
    private List<Object[]> objectsList;

    public SqlMetaDataResult(SqlMetaData[] metaData, List<Object[]> objectsList) {
        this.metaData = metaData;
        this.objectsList = objectsList;
    }

    SqlMetaData[] getMetaData() {
        return metaData;
    }

    List<Object[]> getObjectsList() {
        return objectsList;
    }
}
