package top.meethigher.ftp.server;


import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import top.meethigher.ftp.server.config.FTPServerProperties;
import top.meethigher.ftp.server.listener.AuditFtpServer;
import top.meethigher.ftp.server.web.WebServer;
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
        ConnectionConfig connectionConfig = connectionConfig(ftpServerProperties);
        AuditFtpServer ftpServer = ftpServer(listener, um, connectionConfig);
        ftpServer.start();
        if (ftpServerProperties.isWebEnable()) {
            WebServer webServer = new WebServer(ftpServerProperties, ftpServer);
            webServer.start();
        }
    }

    @Override
    public String banner() throws Exception {
        return "" +
                "\n" +
                "\n" +
                "     //    / /                             //   / / /__  ___/ //   ) )\n" +
                "    //___ / /  ___     // //  ___         //___       / /    //___/ /\n" +
                "   / ___   / //___) ) // // //   ) )     / ___       / /    / ____ /\n" +
                "  //    / / //       // // //   / /     //          / /    //\n" +
                " //    / / ((____   // // ((___/ /     //          / /    //\n" +
                "   Source code address is https://github.com/meethigher/ftp-server\n" +
                "       Have fun! Author's website is https://meethigher.top \n" +
                "\n" +
                "\n";
    }

    public static void main(String[] args) throws Exception {
        runApp(Server.class, args);
    }


}