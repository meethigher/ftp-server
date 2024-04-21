package top.meethigher.ftp.server.web;

import io.jooby.*;
import io.jooby.thymeleaf.ThymeleafModule;
import top.meethigher.ftp.server.config.FTPServerProperties;
import top.meethigher.ftp.server.listener.AuditFtpServer;
import top.meethigher.ftp.server.utils.JWTUtils;
import top.meethigher.ftp.server.utils.UserModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static top.meethigher.ftp.server.utils.FTPServerUtils.findByName;
import static top.meethigher.ftp.server.utils.FTPServerUtils.userModels;

public class WebServer extends Jooby {

    private final String sessionName = "session";

    private final FTPServerProperties ftpServerProperties;

    private final AuditFtpServer auditFtpServer;

    private final UserService userService;

    {
        install(new ThymeleafModule());

        before((context) -> {
            if (!isLoggedIn(context)) {
                if ("/login.html".equals(context.getRequestPath())) {
                    return;
                }
                context.sendRedirect("/login.html");
            }
        });
        get("/", ctx -> ctx.sendRedirect("/login"));

        /*页面*/
        get("/login.html", (context) -> {
            Map<String, Object> map = new HashMap<>();
            Value msg = context.query("msg");
            if (msg.isPresent()) {
                map.put("msg", msg.value());
            }
            ValueNode node = context.query("username");
            if (node.isPresent()) {
                map.put("username", node.value());
            }
            return new ModelAndView("login.html", map);
        });


        /**
         * 展示用户列表
         */
        get("/index.html", (context) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("port", getFtpServerProperties().getPort());
            map.put("idleSeconds", getFtpServerProperties().getIdleSeconds());
            map.put("activeLocalPort", getFtpServerProperties().getActiveLocalPort());
            map.put("passivePorts", getFtpServerProperties().getPassivePorts());
            map.put("webPort", getFtpServerProperties().getWebPort());
            List<UserModel> userModels = userModels();
            map.put("users", userModels);
            return new ModelAndView("index.html", map);
        });
        get("/editUser.html", (context) -> {
            Optional<UserModel> optional = findByName(context.query("name").value());
            if (optional.isPresent()) {
                return new ModelAndView("editUser.html", optional.get().toMap());
            } else {
                context.sendRedirect("/index.html?msg=User does not exist");
                return null;
            }
        });

        /*接口*/
        post("/login.html", (context) -> {
            String username = context.form("username").value();
            String password = context.form("password").value();
            Map<String, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("password", password);
            // 简单的登录逻辑，这里假设用户名和密码都是 admin
            if (getFtpServerProperties().getWebUsername().equals(username) && getFtpServerProperties().getWebPassword().equals(password)) {
                // 登录成功，设置用户登录状态为 true，并重定向到首页
                Session session = context.session();
                session.put(sessionName, createJWT(username));
                context.sendRedirect("/index.html");
                return null;
            } else {
                // 登录失败，返回登录页面并显示错误消息
                model.put("msg", "User and password do not match");
                return new ModelAndView("login.html", model);
            }
        });
        get("/deleteUser.html", (context) -> {
            String name = context.query("name").value();
            boolean b = getUserService().deleteByName(name);
            context.sendRedirect("/index.html?msg=" + (b ? "Delete Successful" : "Delete Failed"));
            return null;
        });
        post("/updateUser.html", (context) -> {
            String enabled = context.form("enabled").value();
            String name = context.form("name").value();
            String password = context.form("password").value();
            String homeDir = context.form("homeDir").value();
            String write = context.form("write").value();
            String maxConcurrentLogins = context.form("maxConcurrentLogins").value();
            String maxConcurrentLoginsPerIP = context.form("maxConcurrentLoginsPerIP").value();
            String maxDownloadRate = context.form("maxDownloadRate").value();
            String maxUploadRate = context.form("maxUploadRate").value();
            UserModel userModel = new UserModel(enabled, name, password, homeDir, write, maxConcurrentLogins, maxConcurrentLoginsPerIP, maxDownloadRate, maxUploadRate);
            boolean b = getUserService().updateUser(userModel);
            context.sendRedirect("/index.html?msg=" + (b ? "Edit Successful" : "Edit Failed"));
            return null;
        });
    }


    public WebServer(FTPServerProperties ftpServerProperties, AuditFtpServer auditFtpServer) {
        this.ftpServerProperties = ftpServerProperties;
        this.auditFtpServer = auditFtpServer;
        this.userService = new UserService(auditFtpServer);
        ServerOptions serverOptions = new ServerOptions();
        serverOptions.setPort(ftpServerProperties.getWebPort());
        this.setServerOptions(serverOptions);
    }

    private boolean isLoggedIn(Context req) {
        Session session = req.session();
        Value value = session.get(sessionName);

        boolean loggedIn = true;
        try {
            if (value.isPresent()) {
                JWTUtils.getTokenInfo(value.value());
            } else {
                loggedIn = false;
            }
        } catch (Exception e) {
            loggedIn = false;
        }
        return loggedIn;
    }

    private String createJWT(String username) {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        return JWTUtils.getToken(map);
    }


    public FTPServerProperties getFtpServerProperties() {
        return ftpServerProperties;
    }

    public AuditFtpServer getAuditFtpServer() {
        return auditFtpServer;
    }

    public UserService getUserService() {
        return userService;
    }
}
