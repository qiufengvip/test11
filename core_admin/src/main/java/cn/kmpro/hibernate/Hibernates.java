
package cn.kmpro.hibernate;

import cn.kmpro.util.PackageFile;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.SybaseASE15Dialect;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class Hibernates {
    private static Log logger = LogFactory.getLog(Hibernates.class);
    public static final String DB_TYPE_MYSQL = "mysql";
    public static final String DB_TYPE_ORACLE = "oracle";

    private static String DB_TYPE = DB_TYPE_MYSQL;

    public static String getDbType(){
        return DB_TYPE;
    }
    /**
     * Initialize the lazy property value.
     * <p/>
     * eg.
     * Hibernates.initLazyProperty(user.getGroups());
     */
    public static void initLazyProperty(Object proxyedPropertyValue) {
        Hibernate.initialize(proxyedPropertyValue);
    }

    /**
     * 从DataSoure中取出connection, 根据connection的metadata中的jdbcUrl判断Dialect类型.
     * 仅支持Oracle, H2, MySql，如需更多数据库类型，请仿照此类自行编写。
     * 2015-02-10 杨川修改
     * 新增对配置文件的支持,默认首先从配置文件中获取数据库方言对象.
     * 配置项名称为:hibernate.dialect.
     * 配置项的值为:org.hibernate.dialect.Dialect的子类,应填写完整类名例如cn.kmpro.hibernate.SQLServer2008NativeDialect.
     * 配置文件条件为classpath下的kmproconfig文件夹内的后缀为properties的文件.
     */
    public static String getDialect(DataSource dataSource) {

        String dialect = null;
        Set<URI> files = PackageFile.getFiles("kmproconfig", "properties");
        for (URI u : files) {
            try {
                PropertiesConfiguration pc = new PropertiesConfiguration(u.toURL());
                Object dialectObj = pc.getProperty("hibernate.dialect");
                if (dialectObj != null) {
                    dialect = dialectObj.toString();
                    if(dialect.equals("org.hibernate.dialect.SybaseASE157Dialect")){
                        dialect="cn.kmpro.hibernate.SybaseASE157NativeDialect";
                    }
                    break;
                }
            } catch (ConfigurationException e) {
                logger.warn("加载配置文件失败:" + u.toString());
            } catch (MalformedURLException e) {
                logger.error("uri error",e);
            }
        }
        if (dialect != null && !"".equals(dialect)) {
            try {
                ClassLoader classLoader = Thread.currentThread()
                        .getContextClassLoader();
                Class clz = classLoader.loadClass(dialect);
                logger.info("找到hibernate方言,采用配置名称:" + clz.getName());

                return clz.getName();
            } catch (ClassNotFoundException e) {
                logger.warn("找不到hibernate方言,配置的名称: " + dialect);
                logger.info("hibernate方言可选值包括:\n" +
                        "org.hibernate.dialect.DB2Dialect\n" +
                        "org.hibernate.dialect.H2Dialect\n" +
                        "org.hibernate.dialect.HSQLDialect\n" +
                        "org.hibernate.dialect.MySQL5Dialect\n" +
                        "org.hibernate.dialect.MySQL5InnoDBDialect\n" +
                        "org.hibernate.dialect.MySQLMyISAMDialect\n" +
                        "org.hibernate.dialect.Oracle8iDialect\n" +
                        "org.hibernate.dialect.Oracle9iDialect\n" +
                        "org.hibernate.dialect.Oracle10gDialect\n" +
                        "org.hibernate.dialect.PostgreSQLDialect\n" +
                        "org.hibernate.dialect.SQLServer2005Dialect\n" +
                        "org.hibernate.dialect.SQLServer2008Dialect\n" +
                        "cn.kmpro.hibernate.SQLServer2008NativeDialect\n" +
                        "org.hibernate.dialect.SQLServerDialect\n" +
                        "org.hibernate.dialect.Sybase11Dialect\n" +
                        "org.hibernate.dialect.SybaseAnywhereDialect\n" +
                        "org.hibernate.dialect.SybaseASE15Dialect\n" +
                        "org.hibernate.dialect.SybaseASE157Dialect\n"+
                        "cn.kmpro.hibernate.SybaseASE157NativeDialect");
            }
        } else {
            logger.info("未定义hibernate方言配置:hibernate.dialect,尝试从数据源获取!");
        }

        String jdbcUrl = getJdbcUrlFromDataSource(dataSource);

        // 根据jdbc url判断dialect
        if (StringUtils.contains(jdbcUrl, ":h2:")) {
            logger.info("找到H2连接,返回org.hibernate.dialect.H2Dialect!");
            return H2Dialect.class.getName();
        } else if (StringUtils.contains(jdbcUrl, ":mysql:")) {
            logger.info("找到mysql连接,返回org.hibernate.dialect.MySQL5InnoDBDialect!");
            DB_TYPE = DB_TYPE_MYSQL;
            return MySQL5InnoDBDialect.class.getName();
        } else if (StringUtils.contains(jdbcUrl, ":sqlserver:")) {
            logger.info("找到sqlserver连接,返回cn.kmpro.hibernate.SQLServer2008NativeDialect!");
            return SQLServer2008NativeDialect.class.getName();
        } else if (StringUtils.contains(jdbcUrl, ":oracle:")) {
            logger.info("找到oracle连接,返回org.hibernate.dialect.Oracle10gDialect!");
            DB_TYPE = DB_TYPE_ORACLE;
            return Oracle10gDialect.class.getName();
        } else if (StringUtils.contains(jdbcUrl, ":sybase:")) {//支持15及以上版本
            logger.info("找到sybase连接,返回org.hibernate.dialect.SybaseASE15Dialect!");
            return SybaseASE15Dialect.class.getName();
        }else if (StringUtils.contains(jdbcUrl, ":sybase:")) {//支持15及以上版本
            logger.info("找到sybase连接,返回cn.kmpro.hibernate.SybaseASE157NativeDialect!");
            return SybaseASE157NativeDialect.class.getName();
        }else if (StringUtils.contains(         jdbcUrl, ":db2:")) {//支持15及以上版本
            logger.info("找到db2连接,返回org.hibernate.dialect.DB2Dialect!");
            return cn.kmpro.hibernate.DB2NativeDialect.class.getName();
        }
        else {
            throw new IllegalArgumentException("数据库方言找不到 " + jdbcUrl);
        }
    }

    private static String getJdbcUrlFromDataSource(DataSource dataSource) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            if (connection == null) {
                throw new IllegalStateException("Connection returned by DataSource [" + dataSource + "] was null");
            }
            return connection.getMetaData().getURL();
        } catch (SQLException e) {
            throw new RuntimeException("无法取得数据库连接串", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    //do nothing
                }
            }
        }
    }
}
