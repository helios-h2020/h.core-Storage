package eu.h2020.helios_social.core.storage;

import static junit.framework.TestCase.assertFalse;

import android.content.Context;
import android.os.Environment;

import androidx.test.core.app.ApplicationProvider;
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
public class FileContentDeleteTest implements OperationReadyListener  {
    /** Test data string */
    private String data =  "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
    OperationReadyListener listener = this;
    private boolean deleteReady = false;
    Long retval = Long.valueOf(-1);

    @Rule
    public GrantPermissionRule mRuntimePermissionRead =
            GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule mRuntimePermissionWrite =
            GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private void writeStorageFile(String filename, byte[] buffer) {
        Context appContext = ApplicationProvider.getApplicationContext();
        File helios;
        if (appContext == null) {
            File sdcard = Environment.getExternalStorageDirectory();
            helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
        } else {
            helios = new File(appContext.getFilesDir(), HeliosStorageUtils.HELIOS_DIR + "/");
        }
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

    private boolean checkStorageFile(String filename) {
        Context appContext = ApplicationProvider.getApplicationContext();
        File helios;
        if (appContext == null) {
            File sdcard = Environment.getExternalStorageDirectory();
            helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
        } else {
            helios = new File(appContext.getFilesDir(), HeliosStorageUtils.HELIOS_DIR + "/");
        }
        String[] listing = helios.list();
        if (listing == null || listing.length == 0) {
            return false;
        }
        for (String entry: listing) {
            if (filename.equals(entry)) {
                return true;
            }
        }
        return false;
    }

    @Before
    public void setUp() {
        writeStorageFile("delete42.txt", data.getBytes());
    }

    @Test
    public void deleteTest() {
        Context appContext = ApplicationProvider.getApplicationContext();
        boolean found = checkStorageFile("delete42.txt");
        if (!found) {
            fail();
        }
        new FileContentUpload(listener, data.getBytes(), appContext).execute("upload42.txt");
        while (!deleteReady)
            ;
        found = checkStorageFile("delete42.txt");
        assertTrue(found);
        deleteReady = false;
        new FileContentDelete(listener, appContext).execute("delete42.txt");
        while (!deleteReady)
            ;
        found = checkStorageFile("delete42.txt");
        assertFalse(found);
    }

    @Override
    public void operationReady(Long result) {
        retval = result;
        deleteReady = true;
    }
}
