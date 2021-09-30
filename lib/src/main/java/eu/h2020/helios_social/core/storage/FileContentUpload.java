package eu.h2020.helios_social.core.storage;

import android.content.Context;
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
    // TODO
    private Context appContext = null;

    /**
     * Constructor for file upload operation
     * @param listener Listener instance o be called when the operation has been completed
     * @param data Data to be uploaded
     */
    FileContentUpload(OperationReadyListener listener, byte[] data) {
        this.listener = listener;
        this.data = data;
    }

    FileContentUpload(OperationReadyListener listener, byte[] data, Context appContext) {
        this.listener = listener;
        this.data = data;
        this.appContext = appContext;
    }

    /**
     * Create a new file and upload data.
     * @param strings Filename to be assigned to data
     * @return Size of stored data in bytes or negative value for an error
     */
    protected Long doInBackground(String... strings) {
        try {
            // TODO
            File helios;
            if(appContext == null) {
                File sdcard = Environment.getExternalStorageDirectory();
                helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
            } else {
                helios = new File(appContext.getFilesDir(), HeliosStorageUtils.HELIOS_DIR);
            }
            if (helios.isFile()) {
                boolean deleted = helios.delete();
                if (!deleted) {
                    Log.e(TAG, "Helios file delete failed");
                    return (long) -1;
                }
            }
            if (!helios.exists()) {
                boolean created = helios.mkdir();
                if (!created) {
                    Log.e(TAG, "Helios subdirectory creation failed");
                    return (long) -2;
                }
            }
            File file = new File(helios, strings[0]);
            if (!file.exists()) {
                boolean created = file.createNewFile();
                if (!created) {
                   Log.e(TAG, "New file creation failed");
                   return (long) -4;
                }
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return (long) -3;
        }
        return (long) data.length;
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
