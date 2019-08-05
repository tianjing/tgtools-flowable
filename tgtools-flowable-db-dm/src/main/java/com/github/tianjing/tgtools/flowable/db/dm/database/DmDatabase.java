package com.github.tianjing.tgtools.flowable.db.dm.database;

import liquibase.CatalogAndSchema;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.OracleDatabase;
import liquibase.exception.DatabaseException;
import liquibase.structure.core.Schema;

/**
 * dm7数据源对象
 * @author 田径
 * @create 2019-07-28 15:53
 * @desc
 **/
public class DmDatabase extends OracleDatabase {

    @Override
    public String getShortName() {
        return "dm";
    }

    @Override
    protected String getDefaultDatabaseProductName() {
        return "DM DBMS";
    }

    @Override
    public Integer getDefaultPort() {
        return Integer.valueOf(5236);
    }

    @Override
    public String getJdbcCatalogName(CatalogAndSchema schema) {
        return null;
    }

    @Override
    public String getJdbcSchemaName(CatalogAndSchema schema) {
        return correctObjectName(schema.getCatalogName() == null ? schema.getSchemaName() : schema.getCatalogName(), Schema.class);
    }


    @Override
    protected String getConnectionCatalogName()
            throws DatabaseException {
        return super.getConnectionCatalogName();
    }

    @Override
    public boolean isCorrectDatabaseImplementation(DatabaseConnection conn)
            throws DatabaseException {
        return  "DM DBMS".equalsIgnoreCase(conn.getDatabaseProductName());
    }

    @Override
    public String getDefaultDriver(String url) {
        if (url.startsWith("jdbc:dm")) {
            return "dm.jdbc.driver.DmDriver";
        }
        return null;
    }

}
