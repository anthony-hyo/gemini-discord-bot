package dev.anthhyo.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import dev.anthhyo.config.Config;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.sql.DataSource;
import java.lang.management.ManagementFactory;

public class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private static HikariPoolMXBean poolProxy;

    private static volatile DataSource dataSource;

    public static void close() {
        if (Base.hasConnection()) {
            Base.close();
        }
    }

    public static void open() {
        if (!Base.hasConnection()) {
            Base.open(dataSource());
        }
    }

    public static void openTransaction() {
        if (Base.hasConnection()) {
            Base.openTransaction();
        }
    }

    public static void rollback() {
        if (Base.hasConnection()) {
            Base.rollbackTransaction();
        }
    }

    public static void commit() {
        if (Base.hasConnection()) {
            Base.commitTransaction();
        }
    }

    public static HikariPoolMXBean monitor() {
        return poolProxy;
    }

    public static DataSource dataSource() {
        if (dataSource == null) {
            setup();
        }
        return dataSource;
    }

    private static void setup() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(Config.singleton().database().JDBC_URL());
        hikariConfig.setUsername(Config.singleton().database().user());
        hikariConfig.setPassword(Config.singleton().database().password());

        hikariConfig.setMaximumPoolSize(30);
        hikariConfig.setRegisterMbeans(true);
        hikariConfig.setPoolName("Game");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");

        dataSource = new HikariDataSource(hikariConfig);

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        try {
            ObjectName poolName = new ObjectName("com.zaxxer.hikari:type=Pool (Game)");
            poolProxy = JMX.newMXBeanProxy(mBeanServer, poolName, HikariPoolMXBean.class);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
    }

}
