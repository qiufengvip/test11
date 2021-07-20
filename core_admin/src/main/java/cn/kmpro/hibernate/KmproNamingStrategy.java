/**
 * Package:cn.kmpro.hibernate;
 * $Id$
 * Copyright(c) 2001-2005 www.afteryuan.com
 */
package cn.kmpro.hibernate;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * KmproNamingStrategy
 * <p/>
 * <p><a href="KmproNamingStrategy.java.html"><i>View Source</i></a></p>
 *
 * @author <a href="mailto:zhangpei@kmpro.cn">Spires Zhang</a>
 * @version $Revision$
 */
public class KmproNamingStrategy extends ImprovedNamingStrategy {
    @Override
    public String classToTableName(String className) {
        return "km_"+super.classToTableName(className).toLowerCase();
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return "f_"+super.propertyToColumnName(propertyName);
    }

    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return "f_"+super.joinKeyColumnName(joinedColumn, joinedTable);
    }

    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        return "f_"+super.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName, referencedColumnName);
    }
}
