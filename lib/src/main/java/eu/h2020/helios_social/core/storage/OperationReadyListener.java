package eu.h2020.helios_social.core.storage;

/**
 * Interface for a listener callback method that gets called when
 * upload or delete operation is ready.
 */
public interface OperationReadyListener {
    /**
     * This is an interface to a callback function that is called when operation is ready.
     * @param result Return value depends on operation that is completed
     */
    void operationReady(Long result);
}
