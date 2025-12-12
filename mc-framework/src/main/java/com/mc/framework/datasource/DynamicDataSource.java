package com.mc.framework.datasource;

import com.mc.common.enums.DataSourceType;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源
 *
 * @author caidu
 */
public class DynamicDataSource extends AbstractRoutingDataSource
{
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources)
    {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey()
    {
        // 当数据源类型为null时，默认使用master数据源
        String dataSourceType = DynamicDataSourceContextHolder.getDataSourceType();
        return dataSourceType != null ? dataSourceType : DataSourceType.MASTER.name();
    }
}