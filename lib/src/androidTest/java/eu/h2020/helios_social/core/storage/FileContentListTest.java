package eu.h2020.helios_social.core.storage;

import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class FileContentListTest implements ListingReadyListener {
    /** Test data string */
    private String data =  "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
    ListingReadyListener listener = this;
    private boolean listingReady = false;
    String[] listing;

    @Rule
    public GrantPermissionRule mRuntimePermissionRead =
            GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule mRuntimePermissionWrite =
            GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private void writeStorageFile(String filename, byte[] buffer) {
        File sdcard = Environment.getExternalStorageDirectory();
        File helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
        if (helios.isFile()) {
            boolean deleted = helios.delete();
            if (!deleted) {
                return;
            }
        }
        if (!helios.exists()) {
            boolean created = helios.mkdir();
            if (!created) {
                return;
            }
        }
        File file = new File(helios, filename);
        try {
            if (!file.exists()) {
                boolean created = file.createNewFile();
                if (!created) {
                    return;
                }
            }

            FileOutputStream out = new FileOutputStream(file);
            out.write(buffer);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void removeStorageFile(String filename) {
        File sdcard = Environment.getExternalStorageDirectory();
        File helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
        if (helios.exists() && helios.isDirectory()) {
            File file = new File(helios, filename);
            file.delete();
        }
    }

    @Before
    public void setUp() {
        writeStorageFile("testfile1.txt", data.getBytes());
        writeStorageFile("testfile2.txt", data.getBytes());
    }

    @Test
    public void listingTest() {
        boolean found1 = false;
        boolean found2 = false;
        new FileContentList(listener).execute("");
        while (!listingReady)
            ;
        if (listing != null) {
            for (String filename : listing) {
                if ("testfile1.txt".compareTo(filename) == 0) {
                    found1 = true;
                }
                if ("testfile2.txt".compareTo(filename) == 0) {
                    found2 = true;
                }
            }
        }
        assertTrue(found1 && found2);
    }

    @After
    public void tearDown() {
        removeStorageFile("testfile1.txt");
        removeStorageFile("testfile2.txt");
    }

    @Override
    public void listingReady(Long result, String[] entries) {
        listing = entries;
        listingReady = true;
    }
}
