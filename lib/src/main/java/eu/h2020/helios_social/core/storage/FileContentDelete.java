package eu.h2020.helios_social.core.storage;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;

/**
 * File-based asynchronous content delete task. This class can be used to initiate file delete.
 * Delete operation is done in background but there can be a listener that will be notified when
 * the operation has been completed by calling a operationReady() method.
 */
public class FileContentDelete extends AsyncTask<String, Integer, Long> {
    /* Listener class that gets notified when the operation is ready */
    private OperationReadyListener listener;

    /**
     * Constructor for file delete operation.
     * @param listener Listener will be notified when the operation has been completed.
     */
    FileContentDelete(OperationReadyListener listener) {
        this.listener = listener;
    }

    /**
     * Delete a file. Currently this always succeeds. However, there can be cases like
     * non-existent file, permission problems, attempt to remove non-empty directory. All
     * these need to be handled and different status should be returned.
     * @param strings Name of the file. This is array but only the first element is used.
     * @return Currently always returns Long value 1L.
     */
    protected Long doInBackground(String... strings) {
        File sdcard = Environment.getExternalStorageDirectory();
        File helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
        if (helios.exists() && helios.isDirectory()) {
            try {
                File file = new File(helios, strings[0]);
                file.delete();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return Long.valueOf(1);
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
     * @param result Operation status. Currently this will always be 1L.
     */
    protected void onPostExecute(Long result) {
        if (listener != null) {
            listener.operationReady(result);
        }
    }
}
