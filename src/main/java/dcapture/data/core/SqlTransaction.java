package dcapture.data.core;

import java.sql.SQLException;
import java.util.List;

/**
 * Sql Transaction
 */
public interface SqlTransaction {

    void executeBatch(SqlQuery query) throws SQLException;

    void executeBatch(List<SqlQuery> queries) throws SQLException;

    void executeCommit(SqlQuery query) throws SQLException;

    void executeCommit(List<SqlQuery> queries) throws SQLException;

    SqlQuery insertQuery(Object object) throws SQLException;

    SqlQuery updateQuery(Object object) throws SQLException;

    SqlQuery deleteQuery(Object object) throws SQLException;
}
