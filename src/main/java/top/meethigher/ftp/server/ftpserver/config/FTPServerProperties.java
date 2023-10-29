package top.meethigher.ftp.server.ftpserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * FTP服务配置
 *
 * @author chenchuancheng
 * @since 2023/10/29 19:14
 */
@Data
@ConfigurationProperties(prefix = "ftp-server")
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


}
