package dcapture.data.dev;

import dcapture.data.core.SqlEnhancer;
import dcapture.data.core.SqlProcessor;
import dcapture.data.htwo.H2Processor;
import dcapture.data.postgres.PostgresProcessor;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;
import org.postgresql.ds.PGConnectionPoolDataSource;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Dev Util
 */
class DevUtil {
    private static final Logger logger = Logger.getLogger(DevUtil.class.getName());
    private static Server h2TcpServer, h2WebServer;
    static final String H2_DRIVER = "org.h2.Driver", POSTGRES_DRIVER = "org.postgresql.Driver";

    static String getClassPathLocation(String pathValue) {
        pathValue = pathValue == null ? "" : pathValue.trim();
        if (pathValue.startsWith("/") || pathValue.startsWith("\\")) {
            pathValue = pathValue.substring(1, pathValue.length());
        }
        if (pathValue.endsWith("/") || pathValue.endsWith("\\")) {
            pathValue = pathValue.substring(0, pathValue.length() - 1);
        }
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource(pathValue);
        try {
            return url == null ? null : Paths.get(url.toURI()).toString();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static SqlProcessor create(File persistenceFile, String driverName, String url, String schema,
                               String user, String password) throws SQLException {
        if (H2_DRIVER.equals(driverName)) {
            JdbcConnectionPool connectionPool = JdbcConnectionPool.create(url, user, password);
            H2Processor h2Processor = new H2Processor();
            h2Processor.initialize(persistenceFile, connectionPool, schema);
            return h2Processor;
        }
        PGConnectionPoolDataSource pool = new PGConnectionPoolDataSource();
        pool.setUrl(url);
        pool.setUser(user);
        pool.setPassword(password);
        pool.setDefaultAutoCommit(false);
        PostgresProcessor postgresProcessor = new PostgresProcessor();
        postgresProcessor.initialize(persistenceFile, pool, schema);
        return postgresProcessor;
    }

    static void writeSqlEnhancer(File file, String[] packageArray) {
        logger.info("Persistence File Location : " + file.getAbsolutePath());
        for (String pack : packageArray) {
            logger.info(pack);
        }
        SqlEnhancer sqlEnhancer = new SqlEnhancer();
        try {
            sqlEnhancer.write(file, packageArray);
            logger.info(" *** --- *** ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void startH2Database() {
        if (h2TcpServer == null) {
            try {
                h2TcpServer = Server.createTcpServer("-tcpPort", "9092").start();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (h2WebServer == null) {
            try {
                h2WebServer = Server.createWebServer("-web", "-webAllowOthers").start();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static boolean isH2TcpDatabaseRunning() {
        return h2TcpServer != null && h2TcpServer.isRunning(false);
    }

    static boolean isH2WebDatabaseRunning() {
        return h2WebServer != null && h2WebServer.isRunning(false);
    }
}

/*
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String[] PACKAGE_ARRAY = new String[]{"dcapture.data.dev"};
    private static final String SCHEMA = "india";
    private static final String URL = "jdbc:h2:tcp://localhost/~/trading";
    private static final String USER = "postgres";
    private static final String PASS = "postgres";

    private SqlProcessor sqlProcessor;

    private Main(SqlProcessor sqlProcessor) {
        this.sqlProcessor = sqlProcessor;
    }

    public static void main(String... args) throws Exception {
        String classPath = DevUtil.getClassPathLocation("");
        File file = new File(classPath, "persistence.json");
        DevUtil.writeSqlEnhancer(file, PACKAGE_ARRAY);
        logger.info(" H2 Database are starting... ");
        DevUtil.startH2Database();
        logger.info(" H2 Tcp server is running : " + DevUtil.isH2TcpDatabaseRunning());
        logger.info(" H2 Web server is running : " + DevUtil.isH2WebDatabaseRunning());
        //
        SqlProcessor sqlProcessor = DevUtil.create(file, DevUtil.H2_DRIVER, URL, SCHEMA, USER, PASS);
        Main main = new Main(sqlProcessor);
        main.runForwardTool();
    }

    private void runForwardTool() {
        try {
            sqlProcessor.runForwardTool();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
*/
