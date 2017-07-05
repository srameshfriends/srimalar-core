package dcapture.data.core;

import jodd.bean.BeanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Transaction
 */
public class AbstractTransaction {

    protected Object getFieldObject(Object obj, String fieldName) {
        return BeanUtil.pojo.getSimpleProperty(obj, fieldName);
    }

    public List<SqlColumn> getColumnListWithoutPK(SqlTable sqlTable) {
        List<SqlColumn> columnList = new ArrayList<>(sqlTable.getSqlColumnList());
        columnList.remove(sqlTable.getPrimaryColumn());
        return columnList;
    }
}
