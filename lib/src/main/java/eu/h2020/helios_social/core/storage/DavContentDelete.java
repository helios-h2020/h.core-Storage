package eu.h2020.helios_social.core.storage;

import android.os.AsyncTask;
import android.util.Log;

import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * WebDAV-based asynchronous content delete task. This class can be used to delete data
 * referenced by an URL string from remote storage.
 */
public class DavContentDelete extends AsyncTask<String, Integer, Long> {
    /** Log tag */
    private static final String TAG = "DavContentDelete";
    /** Listener instance for the delete operation */
    private OperationReadyListener listener;
    /** WebDAV client implementation */
    private Sardine sardine;

    /**
     * Constructor for WebDAV delete operation
     * @param listener Listener will be notified when the operation has been completed.
     * @param username Web page username
     * @param password Web page password
     */
    DavContentDelete(OperationReadyListener listener, String username, String password) {
        this.listener = listener;
        this.sardine = new OkHttpSardine();
        this.sardine.setCredentials(username, password);
    }

    /**
     * Do WebDAV delete operation in background.
     * @param strings The list of urls to be downloaded
     * @return Long value 1L if successful
     */
    @Override
    protected Long doInBackground(String... strings) {
        int count = strings.length;
        for (int i = 0; i < count; i++) {
            try {
                sardine.delete(strings[i]);
            } catch (IOException e) {
                Log.e(TAG, "IO exception");
            }
        }
        return Long.valueOf(1);
    }

    /**
     * Optionally update progress bar. Currently not used.
     *
     * @param progress Progress indicator
     */
    protected void onProgressUpdate(Integer... progress) {

    }

    /**
     * This method is called when the operation is completed. If the listener has been configured
     * it is called.
     *
     * @param result The size of stored data or negative value for an error
     */
    protected void onPostExecute(Long result) {
        if (listener != null) {
            listener.operationReady(result);
        }
    }
}
