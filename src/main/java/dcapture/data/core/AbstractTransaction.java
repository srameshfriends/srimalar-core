package dcapture.data.core;

import jodd.bean.BeanUtil;

/**
 * Abstract Transaction
 */
public class AbstractTransaction {

    protected Object getFieldObject(Object obj, String fieldName) {
        return BeanUtil.pojo.getSimpleProperty(obj, fieldName);
    }
}
