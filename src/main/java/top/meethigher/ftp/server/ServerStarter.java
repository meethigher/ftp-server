package top.meethigher.ftp.server;


import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import top.meethigher.ftp.server.bugfix.MemoryPropertiesUserManagerFactory;
import top.meethigher.ftp.server.bugfix.MemoryWritePermission;
import top.meethigher.ftp.server.config.FTPServerProperties;
import top.meethigher.ftp.server.utils.BaseProperties;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import static top.meethigher.ftp.server.utils.BasePropertiesUtils.load;

/**
 * 服务启动器
 * 
 * @author chenchuancheng
 * @since 2023/11/04 23:40
 */
public class ServerStarter {


    private final String serverFile = "server.properties";

    private final String users = "users";

    private final String userFile = ".properties";


    public FTPServerProperties ftpServerProperties() {
        FTPServerProperties p = new FTPServerProperties();
        try {
            BaseProperties properties = load(serverFile);
            p.setPort(properties.getInteger("port", p.getPort()));
            p.setIdleSeconds(properties.getInteger("idleSeconds", p.getIdleSeconds()));
            p.setActiveLocalPort(properties.getInteger("activeLocalPort", p.getActiveLocalPort()));
            p.setPassivePorts(properties.getString("passivePorts", p.getPassivePorts()));
        } catch (Exception ignore) {
        }
        return p;
    }


    public List<BaseUser> baseUserList() {
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


    public Listener listener(FTPServerProperties p) {
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(p.getPort());
        DataConnectionConfigurationFactory configurationFactory = new DataConnectionConfigurationFactory();
        configurationFactory.setIdleTime(p.getIdleSeconds());
        configurationFactory.setActiveLocalPort(p.getActiveLocalPort());
        configurationFactory.setPassivePorts(p.getPassivePorts());
        listenerFactory.setDataConnectionConfiguration(configurationFactory.createDataConnectionConfiguration());
        return listenerFactory.createListener();
    }

    public UserManager userManager(List<BaseUser> list) {
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

    public FtpServer ftpServer(Listener listener,
                               UserManager userManager) {
        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.addListener("default", listener);
        serverFactory.setUserManager(userManager);
        return serverFactory.createServer();
    }


    public void start() {
        FTPServerProperties ftpServerProperties = ftpServerProperties();
        List<BaseUser> baseUserList = baseUserList();
        UserManager um = userManager(baseUserList);
        Listener listener = listener(ftpServerProperties);
        FtpServer ftpServer = ftpServer(listener, um);
        try {
            ftpServer.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }
}