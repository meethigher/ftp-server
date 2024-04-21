package top.meethigher.ftp.server.web;

import org.apache.ftpserver.ftplet.UserManager;
import top.meethigher.ftp.server.listener.AuditFtpServer;
import top.meethigher.ftp.server.utils.UserModel;

import static top.meethigher.ftp.server.utils.FTPServerUtils.*;

public class UserService {


    private final AuditFtpServer auditFtpServer;

    private final UserManager userManager;

    public UserService(AuditFtpServer auditFtpServer) {
        this.auditFtpServer = auditFtpServer;
        this.userManager = auditFtpServer.getUserManager();
    }

    public boolean deleteByName(String username) {
        boolean result = true;
        try {
            userManager.delete(username);
            deleteByUsername(username);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public boolean updateUser(UserModel userModel) {
        boolean result = true;
        try {
            userManager.save(createBaseUser(userModel));
            updateUserFile(userModel);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
