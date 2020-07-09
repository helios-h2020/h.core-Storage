package eu.h2020.helios_social.core.storage;

import java.io.ByteArrayOutputStream;

/**
 * Interface for a listener callback method that gets called when download operation is ready.
 */
public interface DownloadReadyListener {

    /**
     * This is an interface to a callback function that is called when downloading is ready. Data
     * type and binary buffer are given as a parameter.
     * @param mimeType Multipurpose Internet Mail Extensions (MIME) data type (see RFC2045)
     * @param buffer Output stream buffer containing returned data.
     */
    void downloadReady(String mimeType, ByteArrayOutputStream buffer);
}
