package eu.h2020.helios_social.core.storage;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * File-based asynchronous content download task. This class can be used to download data
 * referenced by an URL from local HELIOS storage. Local storage does not have to use
 * asynchronous methods. However, the idea is to have location agnostic storage and if the
 * storage is in remote location asynchronous methods have to be used. That is why asynchronous
 * methods are also used with local storage.
 */
public class FileContentDownload extends AsyncTask<String, Integer, Long> {
    /** Listener instance for the download operation */
    private DownloadReadyListener listener;
    /** Output buffer */
    private ByteArrayOutputStream outbuf = new ByteArrayOutputStream();
    /** Output data type */
    private String mimeType = null;
    // TODO
    private Context appContext = null;

    /**
     * Constructor for file download operation
     * @param listener Listener will be notified when the operation has been completed.
     */
    FileContentDownload(DownloadReadyListener listener) {
        this.listener = listener;
    }
    // TODO
    FileContentDownload(DownloadReadyListener listener, Context appContext) {
        this.listener = listener;
        this.appContext = appContext;
    }

    /**
     * Do file download operation in background.
     * @param strings Filename to be downloaded
     * @return Length of a file to be downloaded
     */
    protected Long doInBackground(String... strings) {

        File helios;
        if(appContext == null) {
            File sdcard = Environment.getExternalStorageDirectory();
            helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
        } else {
            helios = new File(appContext.getFilesDir(), HeliosStorageUtils.HELIOS_DIR);
        }
        MimeTypeMap mimeMapper = MimeTypeMap.getSingleton();
        String fileExt = MimeTypeMap.getFileExtensionFromUrl(strings[0]);
        mimeType = mimeMapper.getMimeTypeFromExtension(fileExt);
        //
        if (helios.exists() && helios.isDirectory()) {
            File file = new File(helios, strings[0]);
            try (InputStream in = new FileInputStream(file) ){
                int byteRead;
                while ((byteRead = in.read()) != -1) {
                    outbuf.write(byteRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Long.valueOf(outbuf.size());
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
        if (listener != null) {
            listener.downloadReady(this.mimeType, this.outbuf);
        }
    }
}
