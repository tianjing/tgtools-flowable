package com.github.tianjing.tgtools.flowable.autoconfigure.config;


import com.github.tianjing.tgtools.flowable.db.dm.database.DmDatabase;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.snapshot.DmSnapshotGeneratorFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.flowable.ui.modeler.conf.DatabaseConfiguration;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 *
 * 添加对 dm7 库的支持
 * @author 田径
 * @create 2019-07-28 14:56
 * @desc
 **/
@Configuration
public class DmDatabaseConfiguration  {//extends DatabaseConfiguration {

    protected static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_DE_";
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);
    protected static Properties databaseTypeMappings = getDefaultDatabaseTypeMappings();
    @Autowired
    protected FlowableModelerAppProperties modelerAppProperties;
    @Autowired
    protected ResourceLoader resourceLoader;

    public DmDatabaseConfiguration() {
    }

    public static Properties getDefaultDatabaseTypeMappings() {
        Properties databaseTypeMappings = new Properties();
        databaseTypeMappings.setProperty("H2", "h2");
        databaseTypeMappings.setProperty("HSQL Database Engine", "hsql");
        databaseTypeMappings.setProperty("MySQL", "mysql");
        databaseTypeMappings.setProperty("Oracle", "oracle");
        databaseTypeMappings.setProperty("PostgreSQL", "postgres");
        databaseTypeMappings.setProperty("Microsoft SQL Server", "mssql");
        databaseTypeMappings.setProperty("db2", "db2");
        databaseTypeMappings.setProperty("DB2", "db2");
        databaseTypeMappings.setProperty("DB2/NT", "db2");
        databaseTypeMappings.setProperty("DB2/NT64", "db2");
        databaseTypeMappings.setProperty("DB2 UDP", "db2");
        databaseTypeMappings.setProperty("DB2/LINUX", "db2");
        databaseTypeMappings.setProperty("DB2/LINUX390", "db2");
        databaseTypeMappings.setProperty("DB2/LINUXX8664", "db2");
        databaseTypeMappings.setProperty("DB2/LINUXZ64", "db2");
        databaseTypeMappings.setProperty("DB2/LINUXPPC64", "db2");
        databaseTypeMappings.setProperty("DB2/400 SQL", "db2");
        databaseTypeMappings.setProperty("DB2/6000", "db2");
        databaseTypeMappings.setProperty("DB2 UDB iSeries", "db2");
        databaseTypeMappings.setProperty("DB2/AIX64", "db2");
        databaseTypeMappings.setProperty("DB2/HPUX", "db2");
        databaseTypeMappings.setProperty("DB2/HP64", "db2");
        databaseTypeMappings.setProperty("DB2/SUN", "db2");
        databaseTypeMappings.setProperty("DB2/SUN64", "db2");
        databaseTypeMappings.setProperty("DB2/PTX", "db2");
        databaseTypeMappings.setProperty("DB2/2", "db2");
        databaseTypeMappings.setProperty("DB2 UDB AS400", "db2");
        databaseTypeMappings.setProperty("DM DBMS", "dm");
        return databaseTypeMappings;
    }

    @Bean(destroyMethod = "clearCache")
    public SqlSessionTemplate SqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        String databaseType = this.initDatabaseType(dataSource);
        if (databaseType == null) {
            throw new FlowableException("couldn't deduct database type");
        } else {
            try {
                Properties properties = new Properties();
                properties.put("prefix", this.modelerAppProperties.getDataSourcePrefix());
                properties.put("blobType", "BLOB");
                properties.put("boolValue", "1");
                properties.load(this.getClass().getClassLoader().getResourceAsStream("org/flowable/db/properties/" + databaseType + ".properties"));
                sqlSessionFactoryBean.setConfigurationProperties(properties);
                sqlSessionFactoryBean.setMapperLocations(ResourcePatternUtils.getResourcePatternResolver(this.resourceLoader).getResources("classpath:/META-INF/modeler-mybatis-mappings/*.xml"));
                sqlSessionFactoryBean.afterPropertiesSet();
                return sqlSessionFactoryBean.getObject();
            } catch (Exception var5) {
                throw new FlowableException("Could not create sqlSessionFactory", var5);
            }
        }
    }


    @Bean
    public Liquibase liquibase(DataSource dataSource) {
        DmSnapshotGeneratorFactory.init();

        LOGGER.info("Configuring Liquibase");
        DatabaseFactory.getInstance().register(new DmDatabase());


        Liquibase liquibase = null;
        try {
            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            database.setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
            database.setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());

            liquibase = new Liquibase("META-INF/liquibase/flowable-modeler-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);

            return liquibase;
        } catch (Exception var9) {
            throw new InternalServerErrorException("Error creating liquibase database", var9);
        } finally {
            this.closeDatabase(liquibase);
        }

    }

    /**
     * 初始化
     * @param dataSource
     * @return
     */
    protected String initDatabaseType(DataSource dataSource) {
        String databaseType = null;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String databaseProductName = databaseMetaData.getDatabaseProductName();
            LOGGER.info("database product name: '{}'", databaseProductName);
            databaseType = databaseTypeMappings.getProperty(databaseProductName);
            if (databaseType == null) {
                throw new FlowableException("couldn't deduct database type from database product name '" + databaseProductName + "'");
            }

            LOGGER.info("using database type: {}", databaseType);
        } catch (SQLException var14) {
            LOGGER.error("Exception while initializing Database connection", var14);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException var13) {
                LOGGER.error("Exception while closing the Database connection", var13);
            }

        }
        databaseType = ("dm".equals(databaseType)) ? "oracle" : databaseType;
        return databaseType;
    }

    private void closeDatabase(Liquibase liquibase) {
        if (liquibase != null) {
            Database database = liquibase.getDatabase();
            if (database != null) {
                try {
                    database.close();
                } catch (DatabaseException var4) {
                    LOGGER.warn("Error closing database", var4);
                }
            }
        }

    }

}
