package eu.h2020.helios_social.core.storage;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * HELIOS Persistent Storage utiity methods
 */
public class HeliosStorageUtils {
    /** Logging tag */
    private static final String TAG = "HeliosStorageUtils";
    /** Storage directory */
    public static final String HELIOS_DIR = "HELIOS";
    /** Use native file separator */
    public static final String FILE_SEPARATOR = File.separator;
    // Store media files to  external storage for now, so they can be accessed by other apps directly.
    public static final String HELIOS_DATETIME_PATTERN = "YYYYMMddhhmmss";

    /**
     * Prevent direct instantiation of the class
     */
    private HeliosStorageUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get HELIOS storage file directly from local file based storage
     * @param baseDir Directory path
     * @param fileName Filename
     * @return File as a byte array
     */
    public static byte[] getFileBytes(final File baseDir, final String fileName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File helios = new File(baseDir, HELIOS_DIR);
        if (helios.exists() && helios.isDirectory()) {
            File file = new File(helios, fileName);
            try (InputStream in = new FileInputStream(file)) {
                int byteRead;
                while ((byteRead = in.read()) != -1) {
                    baos.write(byteRead);
                }
            } catch (IOException e) {
                Log.e(TAG, "error reading file");
                e.printStackTrace();
            }
        }

        return baos.toByteArray();
    }

    /**
     * Save data from buffer directly to local file-based HELIOS storage
     * @param data Data buffer to be saved
     * @param baseDir Storage directory pathname
     * @param fileName Storage filename
     * @return True if saving was successful and otherwise False
     */
    public static boolean saveFile(final byte[] data, final File baseDir, final String fileName) {
        File helios = new File(baseDir, HELIOS_DIR);

        if (helios.isFile()) {
            boolean deleted = helios.delete();
            if (!deleted) {
                Log.e(TAG, "file delete failed");
                return false;
            }
        }
        if (!helios.exists()) {
            boolean created = helios.mkdir();
            if (!created) {
                Log.e(TAG, "HELIOS_DIR creation failed");
                return false;
            }
        }

        File file = new File(helios, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "file creation failed");
                return false;
            }
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(data);
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, "error writing file.");
            return false;
        }

        return true;
    }

    /**
     * Save data from input stream directly to local file-based HELIOS storage
     * @param inputStream Input stream to be ssaved
     * @param baseDir Storage directory pathname
     * @param fileName Storage filename
     * @return True if saving was successful and otherwise False
     */
    public static boolean saveFile(final InputStream inputStream, final File baseDir, final String fileName) {
        boolean res = true;

        // Check and create directory if necessary
        File helios = new File(baseDir, HELIOS_DIR);
        if (helios.isFile()) {
            boolean deleted = helios.delete();
            if (!deleted) {
                Log.e(TAG, "file delete failed");
                return false;
            }
        }
        if (!helios.exists()) {
            boolean created = helios.mkdir();
            if (!created) {
                Log.e(TAG, "HELIOS_DIR creation failed");
                return false;
            }
        }

        // Save file
        try {
            File file = new File(helios, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "error writing file.");
            return false;
        }

        return true;
    }

    /**
     * Directly delete a file from localfile based HELIOS storage
     * @param baseDir Directory path
     * @param fileName Filename
     * @return True if delete was successful and otherwise False
     */
    public static boolean deleteFile(final File baseDir, final String fileName) {
        File helios = new File(baseDir, HELIOS_DIR);
        File file = new File(helios, fileName);
        // Check this is a file
        if (file.isFile()) {
            boolean deleted = file.delete();
            if (!deleted) {
                Log.e(TAG, "file delete failed");
                return false;
            }
        } else {
            Log.e(TAG, "trying to delete a directory.");
            return false;
        }

        return true;
    }

}
