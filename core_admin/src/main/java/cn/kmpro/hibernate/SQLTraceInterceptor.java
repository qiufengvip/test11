package cn.kmpro.hibernate;

import org.hibernate.EmptyInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * hibernate配置DB2时，为了防止高事务安全级别对查询造成影响，因此查询需要单独制定with ur。
 * 此类是hibernate拦截器，用于给select的查询末尾增加with ur选项，以防止查询时锁住数据库库。
 * @author superxb
 *
 */
public class SQLTraceInterceptor extends EmptyInterceptor {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 1L;

    @Override
    public String onPrepareStatement(String str) {
        logger.trace("sql:{}",str);
        return str;
    }
}