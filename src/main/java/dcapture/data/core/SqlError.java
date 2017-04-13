package dcapture.data.core;

import java.sql.SQLException;

/**
 * Sql Error
 */
interface SqlError {
    default void onSqlError(SqlQuery query, SQLException ex) {
    }
}
