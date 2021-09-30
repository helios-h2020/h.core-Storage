package eu.h2020.helios_social.core.storage;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import static eu.h2020.helios_social.core.storage.HeliosStorageManager.AccessMethod.*;

/**
 * Singleton class implementing HELIOS storage interface. HeliosStorageManager is using local file
 * based storage implementation.
 */
public class HeliosStorageManager implements HeliosStorage {
    /** Logging tag */
    private static final String TAG = "HeliosStorageManager";
    /** Singleton Design Pattern is always using the one stored instance */
    private static final HeliosStorageManager ourInstance = new HeliosStorageManager();
    /** Access method alternatives */
    public enum AccessMethod {
        /** Use local filesystem (default) */
        LOCALFS,
        /** Use WebDAV-based remote storage */
        WEBDAV
    };
    /** Storage manager access method */
    private AccessMethod accessMethod = LOCALFS;
    /** Username for WebDAV storage access */
    private String username = null;
    /** Password for WebDAV storage access */
    private String password = null;

    /**
     * Instead of using constructor and new Singleton class has a static method to return a handle
     * to a singleton instace.
     * @return HeliosStorageManager singleton instance
     */
    public static HeliosStorageManager getInstance() {
        return ourInstance;
    }

    /**
     * Class constructor. As this is a private method, only other methods inside the class are able
     * to invoke the constructor.
     */
    private HeliosStorageManager() {
        // Helios Profile Manager could be used to query user's settings that
        // can be used to specify location of the personal storage. Now
        // hardcoded to File-based implementation.
        //
        // Extension modules can be used to provide alternative storage
        // implementations. Some kind of registering mechanism is needed.
    }

    /**
     * Set storage access method
     * @param accessMethod Either local filesystem or WebDAV
     */
    public void setAccessMethod(AccessMethod accessMethod) {
        this.accessMethod = accessMethod;
    }

    /**
     * Get current access method
     * @return Either local filesystem or WebDAV enum
     */
    public AccessMethod getAccessMethod() {
        return this.accessMethod;
    }

    /**
     * Set credentials for WebDAV storage access
     * @param username WebDAV storage username
     * @param password WebDAV storage password
     */
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Download an object from HELIOS persistent storage
     * @param pathname The name of the object to be downloaded
     * @param listener Class instance to be notified when download is completed
     */
    public void download(String pathname, DownloadReadyListener listener) {
        switch (accessMethod) {
            case LOCALFS:
                new FileContentDownload(listener).execute(pathname);
                break;
            case WEBDAV:
                new DavContentDownload(listener, username, password).execute(pathname);
                break;
            default:
                Log.e(TAG, "Unknown access method");
                break;
        }
    }

    // TODO
    public void download(String pathname, DownloadReadyListener listener, Context appContext) {
        switch (accessMethod) {
            case LOCALFS:
                new FileContentDownload(listener, appContext).execute(pathname);
                break;
            case WEBDAV:
                new DavContentDownload(listener, username, password).execute(pathname);
                break;
            default:
                Log.e(TAG, "Unknown access method");
                break;
        }
    }

    /**
     * Upload an object to HELIOS persistent storage
     * @param pathname The name of the object to be uploaded
     * @param data Uploaded data as raw bytes
     * @param listener Class instance to be notified when upload is completed
     */
    public void upload(String pathname, byte[] data, OperationReadyListener listener) {
        switch (accessMethod) {
            case LOCALFS:
                new FileContentUpload(listener, data).execute(pathname);
                break;
            case WEBDAV:
                new DavContentUpload(listener, data, username, password).execute(pathname);
                break;
            default:
                Log.e(TAG, "Unknown access method");
                break;
        }
    }

    /**
     * Upload an object to HELIOS persistent storage as synchronous method
     * @param pathname The name of the object to be uploaded
     * @param data Uploaded data as raw bytes
     * @param listener Class instance to be notified when upload is completed
     * @return Number of bytes uploaded or negative value for an error
     * @throws ExecutionException Upload operation failed
     * @throws InterruptedException Upload operation interrupted
     */
    public long uploadSync(String pathname, byte[] data, OperationReadyListener listener) throws ExecutionException, InterruptedException {
        switch (accessMethod) {
            case LOCALFS:
                return new FileContentUpload(listener, data).execute(pathname).get();
            case WEBDAV:
                return new DavContentUpload(listener, data, username, password).execute(pathname).get();
            default:
                Log.e(TAG, "Unknown access method");
                return -1;
        }
    }

    /**
     * Upload an object to HELIOS persistent storage as synchronous method
     * @param pathname The name of the object to be uploaded
     * @param data Uploaded data as raw bytes
     * @param listener Class instance to be notified when upload is completed
     * @return Number of bytes uploaded or negative value for an error
     * @throws ExecutionException Upload operation failed
     * @throws InterruptedException Upload operation interrupted
     */
    // TODO: check for Android Q and later
    public long uploadSync(String pathname, byte[] data, OperationReadyListener listener, Context appContext) throws ExecutionException, InterruptedException {
        switch (accessMethod) {
            case LOCALFS:
                return new FileContentUpload(listener, data, appContext).execute(pathname).get();
            case WEBDAV:
                return new DavContentUpload(listener, data, username, password).execute(pathname).get();
            default:
                Log.e(TAG, "Unknown access method");
                return -1;
        }
    }

    /**
     * Remove an object from HELIOS persistent storage
     * @param pathname The name of the object to be removed
     * @param listener Class instance to be notified when removal is completed
     */
    public void delete(String pathname, OperationReadyListener listener) {
        switch (accessMethod) {
            case LOCALFS:
                new FileContentDelete(listener).execute(pathname);
                break;
            case WEBDAV:
                new DavContentDelete(listener, username, password).execute(pathname);
                break;
            default:
                Log.e(TAG, "Unknown access method");
                break;
        }
    }

    /**
     * List objects in HELIOS persistent storage
     * @param pathname Pathname to be listed
     * @param listener Class instance to be notified when listing is completed
     */
    public void list(String pathname, ListingReadyListener listener) {
        switch (accessMethod) {
            case LOCALFS:
                new FileContentList(listener).execute(pathname);
                break;
            case WEBDAV:
                new DavContentList(listener, username, password).execute(pathname);
                break;
            default:
                Log.e(TAG, "Unknown access method");
                break;
        }
    }
}
