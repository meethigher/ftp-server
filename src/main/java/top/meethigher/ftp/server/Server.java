package top.meethigher.ftp.server;


import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import top.meethigher.ftp.server.config.FTPServerProperties;
import top.meethigher.simple.startup.log.SimpleApplication;

import java.util.List;

import static top.meethigher.ftp.server.utils.FTPServerUtils.*;


public class Server extends SimpleApplication {

    @Override
    public void run() throws Exception {
        FTPServerProperties ftpServerProperties = ftpServerProperties();
        List<BaseUser> baseUserList = baseUserList();
        UserManager um = userManager(baseUserList);
        Listener listener = listener(ftpServerProperties);
        FtpServer ftpServer = ftpServer(listener, um);
        ftpServer.start();
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("PID", "111");
        runApp(Server.class, args);
    }
}