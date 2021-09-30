package eu.h2020.helios_social.core.storage;

import android.os.AsyncTask;

import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.IOException;

/**
 * WebDAV-based asynchronous content upload task. This class can be used to upload data
 * referenced by an URL string from remote storage.
 */
public class DavContentUpload extends AsyncTask<String, Integer, Long> {
    /**
     * Logging tag
     */
    private static final String TAG = "DavContentUpload";
    /**
     * Listener instance for the upload operation
     */
    private OperationReadyListener listener;
    /**
     * Data to be uploaded
     */
    private byte[] data;
    /**
     * WebDAV client implementation
     */
    private Sardine sardine;

    /**
     * Constructor for file upload operation
     *
     * @param listener Listener instance o be called when the operation has been completed
     * @param data     Data to be uploaded
     * @param username Web page username
     * @param password Web page password
     */
    DavContentUpload(OperationReadyListener listener, byte[] data, String username, String password) {
        this.listener = listener;
        this.data = data;
        this.sardine = new OkHttpSardine();
        this.sardine.setCredentials(username, password);
    }

    /**
     * Do WebDAV upload operation in background.
     * @param strings The list of urls to be uploaded (only the first will be uploaded)
     * @return Size of uploaded components
     */
    @Override
    protected Long doInBackground(String... strings) {
        try {
            sardine.put(strings[0], this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.valueOf(data.length);
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