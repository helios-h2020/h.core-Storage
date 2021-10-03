package eu.h2020.helios_social.core.storage;

import android.content.Context;
import android.os.Environment;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class FileContentUploadTest implements OperationReadyListener {
    /** Test data string */
    private String data =  "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
    OperationReadyListener listener = this;
    private boolean uploadReady = false;
    Long retval = Long.valueOf(-1);

    @Rule
    public GrantPermissionRule mRuntimePermissionRead =
            GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule mRuntimePermissionWrite =
            GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private byte[] readStorageFile(String filename) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
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
            try (InputStream in = new FileInputStream(file)) {
                int byteRead;
                while ((byteRead = in.read()) != -1) {
                    out.write(byteRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return out.toByteArray();
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

    @Test
    public void uploadTest() {
        Context appContext = ApplicationProvider.getApplicationContext();
        new FileContentUpload(listener, data.getBytes(), appContext).execute("upload42.txt");
        while (!uploadReady)
            ;
        byte[] content = readStorageFile("upload42.txt");
        if (retval > 0) {
            assertArrayEquals(data.getBytes(), content);
        } else {
            fail();
        }
    }

    @After
    public void tearDown() {
        removeStorageFile("upload42.txt");
    }

    @Override
    public void operationReady(Long result) {
        retval = result;
        uploadReady = true;
    }
}
