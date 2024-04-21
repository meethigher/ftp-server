package top.meethigher.ftp.server.utils;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private boolean enabled;
    private String name = "test";
    private String password = "test";
    private String homeDir = "/";
    private String write = "no";
    private int maxConcurrentLogins = 5;
    private int maxConcurrentLoginsPerIP = 1;
    private int maxDownloadRate = 1024000;
    private int maxUploadRate = 1024000;


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public String getWrite() {
        return write;
    }

    public void setWrite(String write) {
        this.write = write;
    }

    public int getMaxConcurrentLogins() {
        return maxConcurrentLogins;
    }

    public void setMaxConcurrentLogins(int maxConcurrentLogins) {
        this.maxConcurrentLogins = maxConcurrentLogins;
    }

    public int getMaxConcurrentLoginsPerIP() {
        return maxConcurrentLoginsPerIP;
    }

    public void setMaxConcurrentLoginsPerIP(int maxConcurrentLoginsPerIP) {
        this.maxConcurrentLoginsPerIP = maxConcurrentLoginsPerIP;
    }

    public int getMaxDownloadRate() {
        return maxDownloadRate;
    }

    public void setMaxDownloadRate(int maxDownloadRate) {
        this.maxDownloadRate = maxDownloadRate;
    }

    public int getMaxUploadRate() {
        return maxUploadRate;
    }

    public void setMaxUploadRate(int maxUploadRate) {
        this.maxUploadRate = maxUploadRate;
    }

    public UserModel(String enabled, String name, String password, String homeDir, String write, String maxConcurrentLogins, String maxConcurrentLoginsPerIP, String maxDownloadRate, String maxUploadRate) {
        this.enabled = Boolean.parseBoolean(enabled);
        this.name = name;
        this.password = password;
        this.homeDir = homeDir;
        this.write = write;
        this.maxConcurrentLogins = Integer.parseInt(maxConcurrentLogins);
        this.maxConcurrentLoginsPerIP = Integer.parseInt(maxConcurrentLoginsPerIP);
        this.maxDownloadRate = Integer.parseInt(maxDownloadRate);
        this.maxUploadRate = Integer.parseInt(maxUploadRate);
    }

    public UserModel() {
    }

    public Map<String,Object> toMap() {
        Map<String,Object> map=new HashMap<>();
        map.put("enabled",this.enabled);
        map.put("name",this.name);
        map.put("password",this.password);
        map.put("homeDir",this.homeDir);
        map.put("write",this.write);
        map.put("maxConcurrentLogins",this.maxConcurrentLogins);
        map.put("maxConcurrentLoginsPerIP",this.maxConcurrentLoginsPerIP);
        map.put("maxDownloadRate",this.maxUploadRate);
        map.put("maxUploadRate",this.maxUploadRate);

        return map;
    }
}