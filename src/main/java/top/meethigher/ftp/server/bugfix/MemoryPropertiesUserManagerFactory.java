package top.meethigher.ftp.server.bugfix;

import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

/**
 * 官方提供的PropertiesUserManager中的getUserByName方法，对于写权限的实现有问题
 * 为了解决该问题，自行实现UserManagerFactory
 *
 * @author chenchuancheng
 * @since 2023/11/04 23:39
 */
public class MemoryPropertiesUserManagerFactory extends PropertiesUserManagerFactory {

    @Override
    public UserManager createUserManager() {
        if (getUrl() != null) {
            return new MemoryPropertiesUserManager(getPasswordEncryptor(), getUrl(),
                    getAdminName());
        } else {

            return new MemoryPropertiesUserManager(getPasswordEncryptor(), getUrl(),
                    getAdminName());
        }
    }
}
