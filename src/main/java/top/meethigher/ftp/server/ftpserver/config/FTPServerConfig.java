package top.meethigher.ftp.server.ftpserver.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import top.meethigher.ftp.server.ftpserver.utils.PropertiesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * FTPServer配置
 *
 * @author chenchuancheng
 * @since 2023/10/29 19:36
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(FTPServerProperties.class)
public class FTPServerConfig {

    @Resource
    private FTPServerProperties ftpServerProperties;


    @Bean
    public Listener listener() {
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(ftpServerProperties.getPort());
        DataConnectionConfigurationFactory configurationFactory = new DataConnectionConfigurationFactory();
        configurationFactory.setIdleTime(ftpServerProperties.getIdleSeconds());
        configurationFactory.setActiveLocalPort(ftpServerProperties.getActiveLocalPort());
        configurationFactory.setPassivePorts(ftpServerProperties.getPassivePorts());
        listenerFactory.setDataConnectionConfiguration(configurationFactory.createDataConnectionConfiguration());
        return listenerFactory.createListener();
    }

    @Bean
    public UserManager userManager() {
        PropertiesUserManagerFactory propertiesUserManagerFactory = new PropertiesUserManagerFactory();
        UserManager um = propertiesUserManagerFactory.createUserManager();
        List<Properties> list = PropertiesUtils.load("users", ".properties");
        if (ObjectUtils.isEmpty(list)) {
            log.error("请在 users 文件夹下创建 *.properties 文件");
            System.exit(0);
            return um;
        }
        try {
            for (Properties p : list) {
                BaseUser user = new BaseUser();
                user.setName(p.getProperty("name"));
                user.setPassword(p.getProperty("password"));
                user.setEnabled(Boolean.parseBoolean(p.getProperty("enabled", "true")));
                user.setHomeDirectory(p.getProperty("homeDir", "/data/ftp"));
                ArrayList<Authority> authorities = new ArrayList<>();
                authorities.add(new WritePermission());
                authorities.add(new ConcurrentLoginPermission(5, 2));
                user.setAuthorities(authorities);
                um.save(user);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return um;
    }

    @Bean
    public FtpServer ftpServer(@Qualifier("listener") Listener listener,
                               @Qualifier("userManager") UserManager userManager) {
        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.addListener("default", listener);
        serverFactory.setUserManager(userManager);
        return serverFactory.createServer();
    }
}
