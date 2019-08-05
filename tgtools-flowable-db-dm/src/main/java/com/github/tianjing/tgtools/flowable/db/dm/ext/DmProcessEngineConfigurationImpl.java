package com.github.tianjing.tgtools.flowable.db.dm.ext;

/**
 * @author 田径
 * @Title
 * @Description
 * @date 19:37
 */

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.spring.SpringProcessEngineConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


public class DmProcessEngineConfigurationImpl extends SpringProcessEngineConfiguration {
    public DmProcessEngineConfigurationImpl() {
        addDatabaseTypeMappings();
    }

    protected void addDatabaseTypeMappings() {
        databaseTypeMappings.setProperty("DM DBMS", "dm");
    }

    @Override
    public void initSqlSessionFactory() {
        if (this.sqlSessionFactory == null) {
            InputStream inputStream = null;
            try {
                inputStream = this.getMyBatisXmlConfigurationStream();

                Environment e = new Environment("default", this.transactionFactory, this.dataSource);
                InputStreamReader reader = new InputStreamReader(inputStream);
                Properties properties = new Properties();
                properties.put("prefix", this.databaseTablePrefix);
                if (this.databaseType != null) {

                    properties.put("limitBefore", "select * from ( select a.*, ROWNUM rnum from (");
                    properties.put("limitAfter", " ) a where ROWNUM < #{lastRow}) where rnum  >= #{firstRow}");
                    properties.put("limitBetween", "");
                    properties.put("limitOuterJoinBetween", "");
                    properties.put("limitBeforeNativeQuery", "");


                    properties.put("blobType", "BLOB");
                    properties.put("boolValue", "1");
                }
                if (databaseType != null) {
                    properties.load(getResourceAsStream(pathToEngineDbProperties()));
                }

                Configuration configuration = this.initMybatisConfiguration(e, reader, properties);
                this.sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
            } catch (Exception var9) {
                throw new FlowableException("Error while building ibatis SqlSessionFactory: " + var9.getMessage(), var9);
            } finally {
                IoUtil.closeSilently(inputStream);
            }
        }
    }

    @Override
    public String pathToEngineDbProperties() {
        return "org/flowable/common/db/properties/" + ("dm".equals(this.databaseType) ? "oracle" : this.databaseType) + ".properties";
    }
}