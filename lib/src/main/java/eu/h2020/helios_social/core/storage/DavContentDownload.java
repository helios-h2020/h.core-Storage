package eu.h2020.helios_social.core.storage;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * WebDAV-based asynchronous content download task. This class can be used to download data
 * referenced by an URL string from remote storage.
 */
public class DavContentDownload extends AsyncTask<String, Integer, Long> {
    /** Log tag */
    private static final String TAG = "DavContentDownload";
    /** Listener instance for the download operation */
    private DownloadReadyListener listener;
    /** WebDAV client implementation */
    private Sardine sardine;
    /** Output buffer */
    private ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
    /** Output data type */
    private String contentType = null;

    /**
     * Constructor for WebDAV download operation
     * @param listener Listener will be notified when the operation has been completed.
     * @param username Web page username
     * @param password Web page password
     */
    DavContentDownload(DownloadReadyListener listener, String username, String password) {
        this.listener = listener;
        this.sardine = new OkHttpSardine();
        this.sardine.setCredentials(username, password);
    }

    /**
     * Do WebDAV download operation in background.
     * @param strings The list of urls to be downloaded
     * @return Sum of the sizes of downloaded components
     */
    @Override
    protected Long doInBackground(String... strings) {
        long totalSize = 0;
        MimeTypeMap mimeMapper = MimeTypeMap.getSingleton();
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(strings[0]);
        contentType = mimeMapper.getMimeTypeFromExtension(fileExt);
        try {
            InputStream davstream = sardine.get(strings[0]);
            InputStream in = new BufferedInputStream(davstream);
            byte[] buffer = new byte[1024];
            int nbytes;
            do {
                nbytes = in.read(buffer);
                if (nbytes > 0) {
                    outbuf.write(buffer, 0, nbytes);
                    totalSize += nbytes;
                }
            } while (nbytes > 0);
        } catch (IOException e) {
            Log.e(TAG, "IO exception");
            e.printStackTrace();
        }
        return totalSize;
    }

    /**
     * Optionally update progress bar. Currently not used.
     * @param progress Progress indicator
     */
    protected void onProgressUpdate(Integer... progress) {

    }

    /**
     * This method is called when the operation is completed. If the listener has been configured
     * it is called.
     * @param result The length of the downloaded file.
     */
    protected void onPostExecute(Long result) {
        listener.downloadReady(this.contentType, this.outbuf);
    }
}
