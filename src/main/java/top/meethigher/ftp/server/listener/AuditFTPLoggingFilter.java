package top.meethigher.ftp.server.listener;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.logging.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重写用于日志审计
 *
 * @author chenchuancheng
 * @since 2023/11/05 00:01
 */
public class AuditFTPLoggingFilter extends LoggingFilter {

    private boolean maskPassword = true;

    private final Logger logger;

    /**
     * @see LoggingFilter#LoggingFilter()
     */
    public AuditFTPLoggingFilter() {
        this(AuditFTPLoggingFilter.class.getName());
    }

    /**
     * @see LoggingFilter#LoggingFilter(Class)
     */
    public AuditFTPLoggingFilter(Class<?> clazz) {
        this(clazz.getName());
    }

    /**
     * @see LoggingFilter#LoggingFilter(String)
     */
    public AuditFTPLoggingFilter(String name) {
        super(name);

        logger = LoggerFactory.getLogger(name);
    }

    /**
     * @see LoggingFilter#messageReceived(org.apache.mina.core.filterchain.IoFilter.NextFilter,
     * IoSession, Object)
     */
    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session,
                                Object message) throws Exception {
        String request = (String) message;

        String logMessage;
        if (maskPassword) {

            if (request.trim().toUpperCase().startsWith("PASS ")) {
                logMessage = "PASS *****";
            } else {
                logMessage = request;
            }
        } else {
            logMessage = request;
        }

        Object user = session.getAttribute("org.apache.ftpserver.user");
        if (user == null) {
            logger.info("received: {}", logMessage);
        } else {
            logger.info("received from {}[{}]: {}", user, session.getRemoteAddress().toString(), logMessage);
        }
        nextFilter.messageReceived(session, message);
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        Object user = session.getAttribute("org.apache.ftpserver.user");
        if (user == null) {
            logger.info("sent: {}", writeRequest.getOriginalMessage());
        } else {
            logger.info("sent to {}[{}]: {}", user, session.getRemoteAddress().toString(), writeRequest.getOriginalMessage());
        }
        nextFilter.messageSent(session, writeRequest);
    }

    /**
     * Are password masked?
     *
     * @return true if passwords are masked
     */
    public boolean isMaskPassword() {
        return maskPassword;
    }

    /**
     * Mask password in log messages
     *
     * @param maskPassword true if passwords should be masked
     */
    public void setMaskPassword(boolean maskPassword) {
        this.maskPassword = maskPassword;
    }
}
