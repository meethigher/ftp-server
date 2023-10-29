package top.meethigher.ftp.server.ftpserver.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.FtpServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executors;

/**
 * FTP服务初始化
 *
 * @author chenchuancheng
 * @since 2023/10/29 19:34
 */
@Component
@Slf4j
public class FTPServerInitializer implements CommandLineRunner {


    @Resource
    private FtpServer ftpServer;

    @Override
    public void run(String... args) throws Exception {
        Executors.newFixedThreadPool(1).submit(() -> {
            try {
                ftpServer.start();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }
}
