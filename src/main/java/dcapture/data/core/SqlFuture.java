package dcapture.data.core;

/**
 * Sql Feature
 */
public interface SqlFuture extends SqlError {
    default void onSqlFuture(SqlMetaDataResult dataResult) {
    }
}
