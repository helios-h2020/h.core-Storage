package eu.h2020.helios_social.core.storage;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;

/**
 * File-based asynchronous content listing task.
 */
public class FileContentList extends AsyncTask<String, Integer, Long> {
    /** Listener instance for the listing operation */
    private ListingReadyListener listener;
    /** File listing */
    private String[] listing;
    /** Application context to be used with Android 11 scoped storage */
    private Context appContext = null;

    /**
     * Constructor for file listing operation
     * @param listener Listener will be notified when the operation has been completed
     */
    FileContentList(ListingReadyListener listener) {
        this.listener = listener;
    }

    /**
     * Constructor for file listing operation
     * @param listener Listener will be notified when the operation has been completed
     *  @param appContext Application context
     */
    FileContentList(ListingReadyListener listener, Context appContext) {
        this.listener = listener;
        this.appContext = appContext;
    }

    /**
     * Create a listing of the directory or a file
     * @param strings Subdirectory or filename
     * @return Number of listing items
     */
    protected Long doInBackground(String... strings) {
        File helios;
        if (appContext == null) {
            File sdcard = Environment.getExternalStorageDirectory();
            helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
        } else {
            helios = new File(appContext.getFilesDir(), HeliosStorageUtils.HELIOS_DIR);
        }
        if (helios.exists() && helios.isDirectory()) {
            try {
                listing = helios.list();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return ((listing != null) ? Long.valueOf(listing.length) : 0L);
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
     * @param result The number of listed storage items.
     */
    protected void onPostExecute(Long result) {
        if (listener != null) {
            listener.listingReady(result, listing);
        }
    }
}
