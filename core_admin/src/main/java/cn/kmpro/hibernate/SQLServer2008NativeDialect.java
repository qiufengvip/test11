package cn.kmpro.hibernate;

import org.hibernate.dialect.SQLServer2008Dialect;

import java.sql.Types;

/**
 * Created by yuxi on 2014/8/29.
 */
public class SQLServer2008NativeDialect extends SQLServer2008Dialect {

    private static Integer MAX_LENGTH = 4000;

    public SQLServer2008NativeDialect() {
        //super();
        registerColumnType(Types.CHAR, "nchar(1)");
        registerColumnType(Types.VARCHAR, MAX_LENGTH , "nvarchar($l)");
        registerColumnType(Types.VARCHAR, "nvarchar(MAX)");
        registerColumnType(Types.LONGVARCHAR, "nvarchar($l)");
        registerColumnType(Types.CLOB, "ntext");
    }
}
