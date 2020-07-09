package eu.h2020.helios_social.core.storage;

import java.util.concurrent.ExecutionException;

/**
 * Singleton class implementing HELIOS storage interface. HeliosStorageManager is using local file
 * based storage implementation.
 */
public class HeliosStorageManager implements HeliosStorage {
    /** Singleton Design Pattern is always using the one stored instance */
    private static final HeliosStorageManager ourInstance = new HeliosStorageManager();

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
     * Download an object from HELIOS persistent storage
     * @param pathname The name of the object to be downloaded
     * @param listener Class instance to be notified when download is completed
     */
    public void download(String pathname, DownloadReadyListener listener) {
        new FileContentDownload(listener).execute(pathname);
    }

    /**
     * Upload an object to HELIOS persistent storage
     * @param pathname The name of the object to be uploaded
     * @param data Uploaded data as raw bytes
     * @param listener Class instance to be notified when upload is completed
     */
    public void upload(String pathname, byte[] data, OperationReadyListener listener) {
        new FileContentUpload(listener, data).execute(pathname);
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
        return new FileContentUpload(listener, data).execute(pathname).get();
    }

    /**
     * Remove an object from HELIOS persistent storage
     * @param pathname The name of the object to be removed
     * @param listener Class instance to be notified when removal is completed
     */
    public void delete(String pathname, OperationReadyListener listener) {
        new FileContentDelete(listener).execute(pathname);
    }

    /**
     * List objects in HELIOS persistent storage
     * @param pathname Pathname to be listed
     * @param listener Class instance to be notified when listing is completed
     */
    public void list(String pathname, ListingReadyListener listener) {
        new FileContentList(listener).execute(pathname);
    }
}
