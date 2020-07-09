package eu.h2020.helios_social.core.storage;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File-based asynchronous content upload task.
 */
public class FileContentUpload extends AsyncTask<String, Integer, Long> {
    /** Logging tag */
    private static final String TAG = "FileContentUpload";
    /** Listener instance for the upload operation */
    private OperationReadyListener listener;
    /** Data to be uploaded */
    private byte[] data;

    /**
     * Constructor for file upload operation
     * @param listener Listener instance o be called when the operation has been completed
     * @param data Data to be uploaded
     */
    FileContentUpload(OperationReadyListener listener, byte[] data) {
        this.listener = listener;
        this.data = data;
    }

    /**
     * Create a new file and upload data.
     * @param strings Filename to be assigned to data
     * @return Size of stored data in bytes or negative value for an error
     */
    protected Long doInBackground(String... strings) {
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            File helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
            if (helios.isFile()) {
                boolean deleted = helios.delete();
                if (!deleted) {
                    Log.e(TAG, "Helios file delete failed");
                    return Long.valueOf(-1);
                }
            }
            if (!helios.exists()) {
                boolean created = helios.mkdir();
                if (!created) {
                    Log.e(TAG, "Helios subdirectory creation failed");
                    return Long.valueOf(-1);
                }
            }
            File file = new File(helios, strings[0]);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.valueOf(data.length);
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
     * @param result The size of stored data or negative value for an error
     */
    protected void onPostExecute(Long result) {
        if (listener != null) {
            listener.operationReady(result);
        }
    }
}
