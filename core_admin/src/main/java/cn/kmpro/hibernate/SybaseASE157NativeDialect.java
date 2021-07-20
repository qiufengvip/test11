package cn.kmpro.hibernate;

import org.hibernate.dialect.SybaseASE157Dialect;

import java.sql.Types;

/**
 * SybaseASE157NativeDialect.
 * <p/>
 * <p><a href="SybaseASE157NativeDialect.java.html"><i>View Source</i></a></p>
 *
 * @author <a href="mailto:zhangpei@kmpro.cn">Spires</a>
 */
public class SybaseASE157NativeDialect extends SybaseASE157Dialect {
    private static Integer MAX_LENGTH = 4000;

    public SybaseASE157NativeDialect() {
        //super();
        registerColumnType(Types.CHAR, "nchar(1)");
        registerColumnType(Types.VARCHAR, MAX_LENGTH , "nvarchar($l)");
        registerColumnType(Types.VARCHAR, "nvarchar(255)");
        registerColumnType(Types.LONGVARCHAR, "nvarchar($l)");
    }
}
