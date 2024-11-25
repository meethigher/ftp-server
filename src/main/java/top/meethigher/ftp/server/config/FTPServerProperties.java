package top.meethigher.ftp.server.config;


/**
 * FTP服务配置
 *
 * @author chenchuancheng
 * @since 2023/10/29 19:14
 */

public class FTPServerProperties {


    /**
     * 监听端口
     */
    private int port = 21;


    /**
     * 最大空闲时间，毫秒
     */
    private int idleSeconds = 60;


    /**
     * 主动模式端口
     */
    private int activeLocalPort = 22;


    /**
     * 被动模式端口
     * 获取用于数据连接的被动端口。端口可定义为单个端口、封闭或开放范围。多个定义可以用逗号分隔，例如：
     * 2300 ：仅使用端口 2300 作为被动端口
     * 2300-2399：使用该范围内的所有端口
     * 2300- ：使用所有大于 2300 的端口
     * 2300、2305、2400-：使用 2300 或 2305 或任何大于 2400 的端口
     */
    private String passivePorts = "30000-";

    /**
     * web账户
     */
    private String webUsername = "admin";

    /**
     * web账户对应密码
     */
    private String webPassword = "admin";

    /**
     * web服务端口
     */
    private int webPort = 8080;

    /**
     * 启动web服务
     */
    private boolean webEnable = true;

    /**
     * 最大登录数
     */
    private int maxLogins = 10;

    /**
     * 启用匿名登录
     */
    private boolean anonymousLoginEnabled = true;

    /**
     * 最大匿名登录数
     */
    private int maxAnonymousLogins = 10;

    /**
     * 最大登录失败数
     */
    private int maxLoginFailures = 3;

    /**
     * 登录失败延迟
     */
    private int loginFailureDelay = 500;

    /**
     * 最大线程
     */
    private int maxThreads = 0;


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getIdleSeconds() {
        return idleSeconds;
    }

    public void setIdleSeconds(int idleSeconds) {
        this.idleSeconds = idleSeconds;
    }

    public int getActiveLocalPort() {
        return activeLocalPort;
    }

    public void setActiveLocalPort(int activeLocalPort) {
        this.activeLocalPort = activeLocalPort;
    }

    public String getPassivePorts() {
        return passivePorts;
    }

    public void setPassivePorts(String passivePorts) {
        this.passivePorts = passivePorts;
    }

    public String getWebUsername() {
        return webUsername;
    }

    public void setWebUsername(String webUsername) {
        this.webUsername = webUsername;
    }

    public String getWebPassword() {
        return webPassword;
    }

    public void setWebPassword(String webPassword) {
        this.webPassword = webPassword;
    }

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }

    public boolean isWebEnable() {
        return webEnable;
    }

    public void setWebEnable(boolean webEnable) {
        this.webEnable = webEnable;
    }

    public int getMaxLogins() {
        return maxLogins;
    }

    public void setMaxLogins(int maxLogins) {
        this.maxLogins = maxLogins;
    }

    public boolean isAnonymousLoginEnabled() {
        return anonymousLoginEnabled;
    }

    public void setAnonymousLoginEnabled(boolean anonymousLoginEnabled) {
        this.anonymousLoginEnabled = anonymousLoginEnabled;
    }

    public int getMaxAnonymousLogins() {
        return maxAnonymousLogins;
    }

    public void setMaxAnonymousLogins(int maxAnonymousLogins) {
        this.maxAnonymousLogins = maxAnonymousLogins;
    }

    public int getMaxLoginFailures() {
        return maxLoginFailures;
    }

    public void setMaxLoginFailures(int maxLoginFailures) {
        this.maxLoginFailures = maxLoginFailures;
    }

    public int getLoginFailureDelay() {
        return loginFailureDelay;
    }

    public void setLoginFailureDelay(int loginFailureDelay) {
        this.loginFailureDelay = loginFailureDelay;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }
}