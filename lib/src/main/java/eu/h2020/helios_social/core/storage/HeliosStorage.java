package eu.h2020.helios_social.core.storage;

/**
 * Interface for HELIOS storage operations. This is storage method agnostic interface. Real
 * implementations can implement this using local or remote storage based methodology.
 */
public interface HeliosStorage {
    /**
     * Download an object from HELIOS persistent storage
     * @param pathname The name of the object to be downloaded
     * @param listener Class instance to be notified when download is completed
     */
    void download(String pathname, DownloadReadyListener listener);

    /**
     * Upload an object to HELIOS persistent storage
     * @param pathname The name of the object to be uploaded
     * @param data Uploaded data as raw bytes
     * @param listener Class instance to be notified when upload is completed
     */
    void upload(String pathname, byte[] data, OperationReadyListener listener);

    /**
     * Remove an object from HELIOS persistent storage
     * @param pathname The name of the object to be removed
     * @param listener Class instance to be notified when removal is completed
     */
    void delete(String pathname, OperationReadyListener listener);

    /**
     * List storage entries
     * @param pathname Pathname to be listed
     * @param listener Class instance to be notified when listing is completed
     */
    void list(String pathname, ListingReadyListener listener);
}
