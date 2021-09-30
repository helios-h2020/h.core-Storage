package eu.h2020.helios_social.core.storage;

import android.os.AsyncTask;

import com.thegrizzlylabs.sardineandroid.DavResource;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.IOException;
import java.util.List;

/**
 * WebDAV-based asynchronous content listing task. This class can be used to create a directory
 * listing of data referenced by an URL string from remote storage.
 */
public class DavContentList extends AsyncTask<String, Integer, Long> {
    /** Log tag */
    private static final String TAG = "DavContentList";
    /** Listener instance for the listing operation */
    private ListingReadyListener listener;
    /** WebDAV client implementation */
    private Sardine sardine;
    /** File listing */
    private String[] listing;

    /**
     * Constructor for WebDAV download operation
     * @param listener Listener will be notified when the operation has been completed.
     * @param username Web page username
     * @param password Web page password
     */
    DavContentList(ListingReadyListener listener, String username, String password) {
        this.listener = listener;
        this.sardine = new OkHttpSardine();
        this.sardine.setCredentials(username, password);
    }

    /**
     * Do WebDAV listing operation in background.
     * @param strings The list of urls to be downloaded
     * @return Number of listed items
     */
    @Override
    protected Long doInBackground(String... strings) {
        try {
            List<DavResource> davlist = this.sardine.list(strings[0]);
            int len = davlist.size();
            this.listing = new String[len];
            for (int i = 0; i < len; i++) {
                listing[i] = davlist.get(i).getName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Long.valueOf(listing.length);
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
