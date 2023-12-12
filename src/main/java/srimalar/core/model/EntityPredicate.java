package srimalar.core.model;

@FunctionalInterface
public interface EntityPredicate<T> {
    boolean test(T t);

    default T beforeInsert(T data) {
        if(data == null) {
            throw new NullPointerException("INSERT: Not found.");
        }
        return data;
    }

    default T beforeUpdate(T old, T update) {
        if(old == null || update == null) {
            throw new NullPointerException("UPDATE: Not found.");
        }
        return update;
    }

    default T beforeDelete(T old) {
        if(old == null) {
            throw new NullPointerException("DELETE: Not found.");
        }
        return old;
    }
}
