package eu.h2020.helios_social.core.storage;

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
public class FileContentDownloadTest implements DownloadReadyListener {
    /** Test data string */
    private String data =  "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
    private byte[] content;
    DownloadReadyListener listener = this;
    private boolean downloadReady = false;

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

    private void removeStorageFile(String filename) {
        Context appContext = ApplicationProvider.getApplicationContext();
        File helios;
        if (appContext == null) {
            File sdcard = Environment.getExternalStorageDirectory();
            helios = new File(sdcard, HeliosStorageUtils.HELIOS_DIR);
        } else {
            helios = new File(appContext.getFilesDir(), HeliosStorageUtils.HELIOS_DIR + "/");
        }
        if (helios.exists() && helios.isDirectory()) {
            File file = new File(helios, filename);
            file.delete();
        }
    }

    @Before
    public void setUp() {
        writeStorageFile("download42.txt", data.getBytes());
    }

    @Test
    public void downloadTest() {
        Context appContext = ApplicationProvider.getApplicationContext();
        new FileContentDownload(listener, appContext).execute("download42.txt");
        while (!downloadReady)
            ;
        assertArrayEquals(data.getBytes(), content);
    }

    @After
    public void tearDown() {
        removeStorageFile("download42.txt");
    }

    @Override
    public void downloadReady(String mimeType, ByteArrayOutputStream baout) {
        content = baout.toByteArray();
        downloadReady = true;
    }
}
