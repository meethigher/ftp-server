package top.meethigher.ftp.server.bugfix;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.usermanager.impl.WriteRequest;

/**
 * 官方提供的PropertiesUserManager中的getUserByName方法，对于写权限的实现有问题
 * 为了解决该问题，自行实现UserManagerFactory
 *
 * @author chenchuancheng
 * @since 2023/11/04 23:39
 */
public class MemoryWritePermission  implements Authority {

    private String permissionRoot;

    /**
     * Construct a write permission for the user home directory (/)
     */
    public MemoryWritePermission() {
        this.permissionRoot = "/";
    }

    /**
     * Construct a write permission for a file or directory relative to the user
     * home directory
     *
     * @param permissionRoot
     *            The file or directory
     */
    public MemoryWritePermission(final String permissionRoot) {
        this.permissionRoot = permissionRoot;
    }

    /**
     * @see Authority#authorize(AuthorizationRequest)
     */
    public AuthorizationRequest authorize(final AuthorizationRequest request) {
        if (request instanceof WriteRequest) {
            WriteRequest writeRequest = (WriteRequest) request;

            String requestFile = writeRequest.getFile();

            if (requestFile.startsWith(permissionRoot)) {
                return writeRequest;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @see Authority#canAuthorize(AuthorizationRequest)
     */
    public boolean canAuthorize(final AuthorizationRequest request) {
        return request instanceof WriteRequest;
    }

    public String getPermissionRoot() {
        return permissionRoot;
    }
}
