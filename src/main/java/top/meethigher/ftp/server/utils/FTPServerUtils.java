package top.meethigher.ftp.server.utils;

import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.DefaultConnectionConfig;
import org.apache.ftpserver.impl.DefaultDataConnectionConfiguration;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import top.meethigher.ftp.server.bugfix.MemoryPropertiesUserManagerFactory;
import top.meethigher.ftp.server.bugfix.MemoryWritePermission;
import top.meethigher.ftp.server.config.FTPServerProperties;
import top.meethigher.ftp.server.listener.AuditFtpServer;
import top.meethigher.ftp.server.listener.AuditFtpServerFactory;
import top.meethigher.ftp.server.listener.AuditListenerFactory;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static top.meethigher.ftp.server.utils.BasePropertiesUtils.load;

/**
 * 将配置apache ftpserver的操作放到工具类
 *
 * @author chenchuancheng
 * @since 2023/11/04 23:44
 */
public class FTPServerUtils {

    private static final String serverFile = "server.properties";

    private static final String users = "users";

    private static final String userFile = ".properties";

    public static boolean deleteByUsername(String username) {
        boolean result = true;
        try {
            File dir = getDir();
            if (dir.isDirectory()) {
                File[] files = dir.listFiles(pathname -> (username + userFile).equals(pathname.getName()));
                if (files != null) {
                    for (File file : files) {
                        if (!file.delete()) {
                            result = false;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    private static File getDir() {
        return new File(System.getProperty("user.dir").replace("\\", "/") + "/" + users);
    }

    public static boolean updateUserFile(UserModel userModel) {
        boolean result = true;
        try {
            File dir = getDir();
            File file = new File(dir, userModel.getName() + userFile);
            if (!file.exists() && !file.createNewFile()) {
                result = false;
            } else {
                Properties p = new Properties();
                p.setProperty("enabled", String.valueOf(userModel.isEnabled()));
                p.setProperty("name", String.valueOf(userModel.getName()));
                p.setProperty("password", String.valueOf(userModel.getPassword()));
                p.setProperty("homeDir", String.valueOf(userModel.getHomeDir()));
                p.setProperty("write", String.valueOf(userModel.getWrite()));
                p.setProperty("maxConcurrentLogins", String.valueOf(userModel.getMaxConcurrentLogins()));
                p.setProperty("maxConcurrentLoginsPerIP", String.valueOf(userModel.getMaxConcurrentLoginsPerIP()));
                p.setProperty("maxDownloadRate", String.valueOf(userModel.getMaxDownloadRate()));
                p.setProperty("maxUploadRate", String.valueOf(userModel.getMaxUploadRate()));
                FileOutputStream fos = new FileOutputStream(file);
                p.store(fos, userModel.getName() + userFile);
                fos.close();
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }


    public static FTPServerProperties ftpServerProperties() {
        FTPServerProperties p = new FTPServerProperties();
        try {
            BaseProperties properties = load(serverFile);
            p.setPort(properties.getInteger("port", p.getPort()));
            p.setIdleSeconds(properties.getInteger("idleSeconds", p.getIdleSeconds()));
            p.setActiveLocalPort(properties.getInteger("activeLocalPort", p.getActiveLocalPort()));
            p.setPassivePorts(properties.getString("passivePorts", p.getPassivePorts()));
            p.setAnonymousLoginEnabled(properties.getBoolean("anonymousLoginEnabled", p.isAnonymousLoginEnabled()));
            p.setLoginFailureDelay(properties.getInteger("loginFailureDelay", p.getLoginFailureDelay()));
            p.setMaxLogins(properties.getInteger("maxLogins", p.getMaxLogins()));
            p.setMaxAnonymousLogins(properties.getInteger("maxAnonymousLogins", p.getMaxAnonymousLogins()));
            p.setMaxLoginFailures(properties.getInteger("maxLoginFailures", p.getMaxLoginFailures()));
            p.setMaxThreads(properties.getInteger("maxThreads", p.getMaxThreads()));
            p.setWebPort(properties.getInteger("web.port", p.getWebPort()));
            p.setWebUsername(properties.getString("web.username", p.getWebUsername()));
            p.setWebPassword(properties.getString("web.password", p.getWebPassword()));
            p.setWebEnable(properties.getBoolean("web.enable", p.isWebEnable()));
        } catch (Exception ignore) {
        }
        return p;
    }

    public static List<UserModel> userModels() {
        return load(users, userFile, UserModel.class);
    }

    public static Optional<UserModel> findByName(String name) {
        Optional<UserModel> optional = Optional.empty();
        try {
            List<UserModel> list = load(users, userFile, UserModel.class).stream().filter(userModel -> userModel.getName().equals(name)).collect(Collectors.toList());
            if (!list.isEmpty()) {
                optional = Optional.of(list.get(0));
            }
        } catch (Exception e) {

        }
        return optional;
    }


    public static List<BaseUser> baseUserList() {
        List<BaseUser> list = new ArrayList<>();
        try {
            List<BaseProperties> pList = load(users, userFile);
            for (BaseProperties p : pList) {
                BaseUser user = new BaseUser();
                user.setName(p.getString("name"));
                user.setPassword(p.getString("password"));
                user.setEnabled(p.getBoolean("enabled", true));
                user.setHomeDirectory(p.getString("homeDir", "/data/ftp"));
                ArrayList<Authority> authorities = new ArrayList<>();
                authorities.add(new MemoryWritePermission(p.getString("write", "no")));
                authorities.add(new ConcurrentLoginPermission(p.getInteger("maxConcurrentLogins", 5), p.getInteger("maxConcurrentLoginsPerIP", 1)));
                authorities.add(new TransferRatePermission(p.getInteger("maxDownloadRate", 1048576), p.getInteger("maxUploadRate", 1048576)));
                user.setAuthorities(authorities);
                list.add(user);
            }
        } catch (Exception ignored) {
        }
        return list;
    }

    public static BaseUser createBaseUser(UserModel userModel) {
        BaseUser user = new BaseUser();
        user.setName(userModel.getName());
        user.setPassword(userModel.getPassword());
        user.setEnabled(userModel.isEnabled());
        user.setHomeDirectory(userModel.getHomeDir());
        ArrayList<Authority> authorities = new ArrayList<>();
        authorities.add(new MemoryWritePermission(userModel.getWrite()));
        authorities.add(new ConcurrentLoginPermission(userModel.getMaxConcurrentLogins(), userModel.getMaxConcurrentLoginsPerIP()));
        authorities.add(new TransferRatePermission(userModel.getMaxDownloadRate(), userModel.getMaxUploadRate()));
        user.setAuthorities(authorities);
        return user;
    }


    public static Listener listener(FTPServerProperties p) {
        AuditListenerFactory listenerFactory = new AuditListenerFactory();
        listenerFactory.setPort(p.getPort());
        DataConnectionConfigurationFactory configurationFactory = new DataConnectionConfigurationFactory();
        configurationFactory.setIdleTime(p.getIdleSeconds());
        configurationFactory.setActiveLocalPort(p.getActiveLocalPort());
        configurationFactory.setPassivePorts(p.getPassivePorts());
        listenerFactory.setDataConnectionConfiguration(configurationFactory.createDataConnectionConfiguration());
        return listenerFactory.createListener();
    }

    public static ConnectionConfig connectionConfig(FTPServerProperties p) {
        return new DefaultConnectionConfig(p.isAnonymousLoginEnabled(), p.getLoginFailureDelay(),
                p.getMaxLogins(), p.getMaxAnonymousLogins(), p.getMaxLoginFailures(), p.getMaxThreads());
    }

    public static UserManager userManager(List<BaseUser> list) {
        MemoryPropertiesUserManagerFactory propertiesUserManagerFactory = new MemoryPropertiesUserManagerFactory();
        UserManager um = propertiesUserManagerFactory.createUserManager();
        for (BaseUser baseUser : list) {
            try {
                um.save(baseUser);
            } catch (Exception ignore) {
            }
        }
        return um;
    }

    public static AuditFtpServer ftpServer(Listener listener,
                                           UserManager userManager, ConnectionConfig connectionConfig) {
        AuditFtpServerFactory serverFactory = new AuditFtpServerFactory();
        // 配置连接限制
        serverFactory.setConnectionConfig(connectionConfig);
        //请不要移除default
        serverFactory.addListener("default", listener);
        serverFactory.setUserManager(userManager);
        return (AuditFtpServer) serverFactory.createServer();
    }
}