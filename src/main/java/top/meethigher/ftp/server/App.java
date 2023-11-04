package top.meethigher.ftp.server;

import org.slf4j.MDC;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 启动
 *
 * @author chenchuancheng
 * @since 2023/11/04 23:39
 */
public class App {
    public static int getCurrentPID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String name = runtimeMXBean.getName();
        return Integer.parseInt(name.split("@")[0]);
    }

    public static void main(String[] args) throws IOException {
        String pid = String.valueOf(getCurrentPID());
        System.setProperty("PID", pid);
        MDC.put("PID", pid);
        new ServerStarter().start();
    }
}
