package cn.kmpro.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.dialect.DB2Dialect;

import java.sql.Types;

/**
 * DB2NativeDialect
 * <p/>
 * <p><a href="DB2NativeDialect.java.html"><i>View Source</i></a></p>
 *
 * @author <a href="mailto:afteryuan@gmail.com">Spires</a>
 * @version 1.0
 */
public class DB2NativeDialect extends DB2Dialect {
    //DB2数据库字段长度为字节，程序使用的字段长度为utf8字符，一个字符占3个字节，故varchar类型的需要乘以3
    @Override
    public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
        if(code== Types.VARCHAR)return super.getTypeName(code, length*3, precision, scale);
        return super.getTypeName(code, length, precision, scale);
    }
}
