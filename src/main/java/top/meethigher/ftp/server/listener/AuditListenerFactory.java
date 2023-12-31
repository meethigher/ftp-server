package top.meethigher.ftp.server.listener;

import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerConfigurationException;
import org.apache.ftpserver.ipfilter.SessionFilter;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.mina.filter.firewall.Subnet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * 重写用于日志审计
 *
 * @author chenchuancheng
 * @since 2023/11/05 00:01
 */
public class AuditListenerFactory {

    private String serverAddress;

    private int port = 21;

    private SslConfiguration ssl;

    private boolean implicitSsl = false;

    private DataConnectionConfiguration dataConnectionConfig = new DataConnectionConfigurationFactory()
            .createDataConnectionConfiguration();

    private int idleTimeout = 300;

    private List<InetAddress> blockedAddresses;

    private List<Subnet> blockedSubnets;

    /**
     * The Session filter
     */
    private SessionFilter sessionFilter = null;

    /**
     * Default constructor
     */
    public AuditListenerFactory() {
        // do nothing
    }

    /**
     * Copy constructor, will copy properties from the provided listener.
     *
     * @param listener The listener which properties will be used for this factory
     */
    public AuditListenerFactory(Listener listener) {
        serverAddress = listener.getServerAddress();
        port = listener.getPort();
        ssl = listener.getSslConfiguration();
        implicitSsl = listener.isImplicitSsl();
        dataConnectionConfig = listener.getDataConnectionConfiguration();
        idleTimeout = listener.getIdleTimeout();
        // TODO remove the next two lines if and when we remove the deprecated
        // methods.
        blockedAddresses = listener.getBlockedAddresses();
        blockedSubnets = listener.getBlockedSubnets();
        this.sessionFilter = listener.getSessionFilter();
    }

    /**
     * Create a listener based on the settings of this factory. The listener is immutable.
     *
     * @return The created listener
     */
    public Listener createListener() {
        try {
            InetAddress.getByName(serverAddress);
        } catch (UnknownHostException e) {
            throw new FtpServerConfigurationException("Unknown host", e);
        }
        // Deal with the old style black list and new session Filter here.
        if (sessionFilter != null) {
            if (blockedAddresses != null || blockedSubnets != null) {
                throw new IllegalStateException(
                        "Usage of SessionFilter in combination with blockedAddesses/subnets is not supported. ");
            }
        }
        if (blockedAddresses != null || blockedSubnets != null) {
            return new AuditNioListener(serverAddress, port, implicitSsl, ssl,
                    dataConnectionConfig, idleTimeout, blockedAddresses,
                    blockedSubnets);
        } else {
            return new AuditNioListener(serverAddress, port, implicitSsl, ssl,
                    dataConnectionConfig, idleTimeout, sessionFilter);
        }
    }

    /**
     * Is listeners created by this factory in SSL mode automatically or must the client explicitly
     * request to use SSL
     *
     * @return true is listeners created by this factory is automatically in SSL mode, false
     * otherwise
     */
    public boolean isImplicitSsl() {
        return implicitSsl;
    }

    /**
     * Should listeners created by this factory be in SSL mode automatically or must the client
     * explicitly request to use SSL
     *
     * @param implicitSsl true is listeners created by this factory should automatically be in SSL mode,
     *                    false otherwise
     */
    public void setImplicitSsl(boolean implicitSsl) {
        this.implicitSsl = implicitSsl;
    }

    /**
     * Get the port on which listeners created by this factory is waiting for requests.
     *
     * @return The port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port on which listeners created by this factory will accept requests. Or set to 0
     * (zero) is the port should be automatically assigned
     *
     * @param port The port to use.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get the {@link InetAddress} used for binding the local socket. Defaults
     * to null, that is, the server binds to all available network interfaces
     *
     * @return The local socket {@link InetAddress}, if set
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Set the {@link InetAddress} used for binding the local socket. Defaults
     * to null, that is, the server binds to all available network interfaces
     *
     * @param serverAddress The local socket {@link InetAddress}
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * Get the {@link SslConfiguration} used for listeners created by this factory
     *
     * @return The {@link SslConfiguration}
     */
    public SslConfiguration getSslConfiguration() {
        return ssl;
    }

    /**
     * Set the {@link SslConfiguration} to use by listeners created by this factory
     *
     * @param ssl The {@link SslConfiguration}
     */
    public void setSslConfiguration(SslConfiguration ssl) {
        this.ssl = ssl;
    }

    /**
     * Get configuration for data connections made within listeners created by this factory
     *
     * @return The data connection configuration
     */
    public DataConnectionConfiguration getDataConnectionConfiguration() {
        return dataConnectionConfig;
    }

    /**
     * Set configuration for data connections made within listeners created by this factory
     *
     * @param dataConnectionConfig The data connection configuration
     */
    public void setDataConnectionConfiguration(
            DataConnectionConfiguration dataConnectionConfig) {
        this.dataConnectionConfig = dataConnectionConfig;
    }

    /**
     * Get the number of seconds during which no network activity
     * is allowed before a session is closed due to inactivity.
     *
     * @return The idle time out
     */
    public int getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * Set the number of seconds during which no network activity
     * is allowed before a session is closed due to inactivity.
     *
     * @param idleTimeout The idle timeout in seconds
     */
    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    /**
     * @return The list of {@link InetAddress}es
     * @deprecated Replaced by the IpFilter.
     * Retrieves the {@link InetAddress} for which listeners created by this factory blocks
     * connections
     */
    @Deprecated
    public List<InetAddress> getBlockedAddresses() {
        return blockedAddresses;
    }

    /**
     * @param blockedAddresses The list of {@link InetAddress}es
     * @deprecated Replaced by the IpFilter.
     * Sets the {@link InetAddress} that listeners created by this factory will block from
     * connecting
     */
    @Deprecated
    public void setBlockedAddresses(List<InetAddress> blockedAddresses) {
        this.blockedAddresses = blockedAddresses;
    }

    /**
     * @return The list of {@link Subnet}s
     * @deprecated Replaced by the IpFilter.
     * Retrives the {@link Subnet}s for which listeners created by this factory blocks connections
     */
    @Deprecated
    public List<Subnet> getBlockedSubnets() {
        return blockedSubnets;
    }

    /**
     * Sets the {@link Subnet}s that listeners created by this factory will block from connecting
     *
     * @param blockedSubnets The list of {@link Subnet}s
     * @deprecated Replaced by the IpFilter.
     */
    @Deprecated
    public void setBlockedSubnets(List<Subnet> blockedSubnets) {
        this.blockedSubnets = blockedSubnets;
    }

    /**
     * Returns the currently configured <code>SessionFilter</code>, if any.
     *
     * @return the currently configured <code>SessionFilter</code>, if any.
     * Returns <code>null</code>, if no <code>SessionFilter</code> is
     * configured.
     */
    public SessionFilter getSessionFilter() {
        return sessionFilter;
    }

    /**
     * Sets the session filter to the given filter.
     *
     * @param sessionFilter the session filter.
     */
    public void setSessionFilter(SessionFilter sessionFilter) {
        this.sessionFilter = sessionFilter;
    }

}
