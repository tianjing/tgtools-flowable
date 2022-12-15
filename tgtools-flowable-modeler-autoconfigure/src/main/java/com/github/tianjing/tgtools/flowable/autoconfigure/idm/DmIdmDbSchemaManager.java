package com.github.tianjing.tgtools.flowable.autoconfigure.idm;

import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.impl.db.IdmDbSchemaManager;
import tgtools.util.StringUtil;

public class DmIdmDbSchemaManager extends IdmDbSchemaManager {
    private static final String IDM_PROPERTY_TABLE = "ACT_ID_PROPERTY";
    private IdmEngineConfiguration idmEngineConfiguration;

    public DmIdmDbSchemaManager(IdmEngineConfiguration pIdmEngineConfiguration) {
        idmEngineConfiguration = pIdmEngineConfiguration;
    }

    @Override
    protected String getPropertyTable() {
        return IDM_PROPERTY_TABLE;//idmEngineConfiguration.getDatabaseTablePrefix() +
    }

    @Override
    public void schemaCheckVersion() {
        if (StringUtil.isEmpty(idmEngineConfiguration.getDatabaseTablePrefix())) {
            super.schemaCheckVersion();
        }
    }
}
