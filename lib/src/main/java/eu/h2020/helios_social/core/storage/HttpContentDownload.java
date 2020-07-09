package eu.h2020.helios_social.core.storage;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;

/**
 * HTTP-based asynchronous content download task. This class can be used to download data
 * referenced by an URL from remote storage.
 */
public class HttpContentDownload extends AsyncTask<URL, Integer, Long> {
    /** Log tag */
    private static final String TAG = "HttpContentDownload";
    /** Listener instance for the download operation */
    private DownloadReadyListener listener;
    /** Output buffer */
    private ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
    /** Output data type */
    private String contentType = null;

    /**
     * Constructor for HTTP download operation
     * @param listener Listener will be notified when the operation has been completed.
     */
    HttpContentDownload(DownloadReadyListener listener)
    {
        this.listener = listener;
    }

    /**
     * Do HTTP download operation in background. This method supports multiple HTTP downloads.
     * @param urls The list of urls to be downloaded
     * @return Sum of the sizes of downloaded components
     */
    protected Long doInBackground(URL... urls){
        long totalSize = 0;
        int count = urls.length;
        for (int i = 0; i < count; i++) {
            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) urls[i].openConnection();
                urlConnection.connect();
                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Server returned HTTP " + urlConnection.getResponseCode()
                            + " " + urlConnection.getResponseMessage());
                    Long aLong = Long.valueOf(0);
                    return aLong;
                }
                int len = urlConnection.getContentLength();
                this.contentType = urlConnection.getContentType();
                if (len < 0) {
                    Log.e(TAG, "Negative size");
                }
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                byte[] buffer = new byte[1024];
                int nbytes;
                do {
                    nbytes = in.read(buffer);
                    if (nbytes > 0) {
                        outbuf.write(buffer, 0, nbytes);
                        totalSize += nbytes;
                    }
                } while (nbytes > 0);
                urlConnection.disconnect();
            } catch (IOException e) {
                Log.e(TAG, "IO exception");
            }
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
