package eu.h2020.helios_social.core.storage;

/**
 * Interface for a listener callback method that gets called when
 * listing operation is ready.
 */
public interface ListingReadyListener {
    /**
     * This method is called when listing operation is ready
     * @param result Result status of the listing operation
     * @param entries Listing entries as a string array
     */
    void listingReady(Long result, String[] entries);
}
