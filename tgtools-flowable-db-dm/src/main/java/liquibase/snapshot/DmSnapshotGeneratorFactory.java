package liquibase.snapshot;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.OfflineConnection;
import liquibase.exception.DatabaseException;
import liquibase.structure.DatabaseObject;
import org.flowable.ui.common.security.SecurityUtils;

import java.lang.reflect.Field;

/**
 * SnapshotGeneratorFactory 为单例模式
 * 由于在生成 oracle sql 和 dm7 有些问题
 * 所以通过这种方式进行覆写
 *
 * @author 田径
 * @create 2019-07-29 20:50
 * @desc
 **/
public class DmSnapshotGeneratorFactory extends SnapshotGeneratorFactory {

    public static void init() {
        try {
            Field vField = SnapshotGeneratorFactory.class.getDeclaredField("instance");
            vField.setAccessible(true);
            vField.set(null, new DmSnapshotGeneratorFactory());
            System.out.println("11");
        } catch (Exception e) {
            e.printStackTrace();
        }
        org.flowable.idm.api.User vUser = new UserEntityImpl();
        vUser.setId("1");
        SecurityUtils.assumeUser(vUser);

    }

    @Override
    public DatabaseSnapshot createSnapshot(DatabaseObject[] examples, Database database,
                                           SnapshotControl snapshotControl)
            throws DatabaseException, InvalidExampleException {
        DatabaseConnection conn = database.getConnection();
        if (conn == null) {
            return new EmptyDatabaseSnapshot(database, snapshotControl);
        }
        if (conn instanceof OfflineConnection) {
            DatabaseSnapshot snapshot = ((OfflineConnection) conn).getSnapshot(examples);
            if (snapshot == null) {
                throw new DatabaseException("No snapshotFile parameter specified for offline database");
            }
            return snapshot;
        }
        return new DmJdbcDatabaseSnapshot(examples, database, snapshotControl);
    }

    public static class UserEntityImpl implements org.flowable.idm.api.User {

        private String id;
        private String firstName;
        private String lastName;
        private String displayName;
        private String email;

        private String password;
        private String tenantId;
        private Boolean pictureSet = false;

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String getFirstName() {
            return firstName;
        }

        @Override
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @Override
        public String getLastName() {
            return lastName;
        }

        @Override
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String getTenantId() {
            return tenantId;
        }

        @Override
        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        @Override
        public boolean isPictureSet() {
            return pictureSet;
        }

    }
}
