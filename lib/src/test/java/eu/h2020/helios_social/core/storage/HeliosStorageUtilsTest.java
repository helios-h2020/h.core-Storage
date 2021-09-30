package eu.h2020.helios_social.core.storage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HeliosStorageUtilsTest {
    /** Temporary folder is automatically destroyed after tests */
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    /** Test data string */
    private String data =  "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";

    /**
     * Read all data bytes from input stream
     * @param inputStream Stream to read (the caller should close this stream)
     * @return Data bytes
     * @throws IOException
     */
    private byte[] readBytes(InputStream inputStream) throws IOException {
        byte[] b = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c;
        while ((c = inputStream.read(b)) != -1) {
            os.write(b, 0, c);
        }
        return os.toByteArray();
    }

    /**
     * Save bytes data to the output file
     * @param baseDir Directory to store the file
     * @param filename File to be created
     * @param buffer Data buffer
     * @return Created File as Java File data structure
     * @throws FileNotFoundException
     * @throws IOException
     */
    private File saveToHeliosDir(File baseDir, String filename, byte[] buffer)
            throws FileNotFoundException, IOException {
        File helios = new File(baseDir, HeliosStorageUtils.HELIOS_DIR);
        helios.mkdir();
        File outfile = new File(helios, filename);
        // String fileName = helios.getAbsolutePath() + "/" + "readfile.txt";
        FileOutputStream out = new FileOutputStream(outfile);
        // byte[] databytes = data.getBytes();
        out.write(buffer);
        out.close();
        return outfile;
    }

    /**
     * Save data using HeliosStorageUtils and read it back
     * @throws IOException
     */
    @Test
    public void saveFileTest() throws IOException {
        File baseDir = folder.newFolder("savedir");
        boolean ok = HeliosStorageUtils.saveFile(data.getBytes(), baseDir, "savefile.txt");
        if (ok) {
            FileInputStream in = new FileInputStream(baseDir.getPath() + "/HELIOS/savefile.txt");
            byte[] content = readBytes(in);
            in.close();
            assertArrayEquals(data.getBytes(), content);
        } else {
            assertTrue(ok);
        }
    }

    /**
     * Save input stream using HeliosStorageUtils and read it back
     * @throws IOException
     */
    @Test
    public void saveStreamTest() throws IOException {
        File baseDir = folder.newFolder("streamdir");
        InputStream input = new ByteArrayInputStream(data.getBytes());
        boolean ok = HeliosStorageUtils.saveFile(input, baseDir, "streamfile.txt");
        if (ok) {
            FileInputStream in = new FileInputStream(baseDir.getPath() + "/HELIOS/streamfile.txt");
            byte[] content = readBytes(in);
            in.close();
            assertArrayEquals(data.getBytes(), content);
        } else {
            assertTrue(ok);
        }
    }

    /**
     * Store data and read it using HeliosStorageUtils
     * @throws IOException
     */
    @Test
    public void getFileBytesTest() throws IOException {
        File baseDir = folder.newFolder("readdir");
        File testfile = saveToHeliosDir(baseDir, "readfile.txt", data.getBytes());
        boolean exists = testfile.exists();
        if (!exists) {
            assertTrue(exists);
        }

        byte[] content = HeliosStorageUtils.getFileBytes(baseDir, new String("readfile.txt"));
        assertArrayEquals(data.getBytes(), content);
    }

    /**
     * Create a file and removeit using HeliosStorageUtils
     * @throws IOException
     */
    @Test
    public void deleteFileTest() throws IOException {
        File baseDir = folder.newFolder("deletedir");
        File testfile = saveToHeliosDir(baseDir, "deletefile.txt", data.getBytes());
        boolean exists = testfile.exists();
        if (!exists) {
            assertTrue(exists);
        }

        boolean ok = HeliosStorageUtils.deleteFile(baseDir, "deletefile.txt");
        if (ok) {
            exists = testfile.exists();
            assertFalse(exists);
        } else {
            assertTrue(ok);
        }
    }
}
