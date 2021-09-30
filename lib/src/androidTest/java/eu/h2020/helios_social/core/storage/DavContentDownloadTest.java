package eu.h2020.helios_social.core.storage;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertArrayEquals;

@RunWith(AndroidJUnit4.class)
public class DavContentDownloadTest implements DownloadReadyListener {
    private static final String TAG = "DavContentDownloadTest";
    private MockWebServer webServer;
    private HttpUrl mockUrl;
    private DownloadReadyListener listener = this;
    private boolean downloadReady = false;
    private String data = "Hello world";
    private byte[] content;

    @Before
    public void setUp() throws IOException {
        webServer = new MockWebServer();
        webServer.setDispatcher(new okhttp3.mockwebserver.Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest request) throws InterruptedException {
                System.out.println(TAG + " Request is " + request.toString());
                switch (request.getPath()) {
                    case "/webdav/download42.txt":
                        return new MockResponse().setResponseCode(200).setBody(data).setHeader("Content-Type", "text/plain");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }
        });
        webServer.start();
        webServer.noClientAuth();
        mockUrl = webServer.url("");
        System.out.println(TAG + " URL is " + mockUrl.toString());
    }

    @Test
    public void downloadTest() throws MalformedURLException {
        new DavContentDownload(listener, null, null).execute(mockUrl + "webdav/download42.txt");
        while (!downloadReady)
            ;
        assertArrayEquals(data.getBytes(), content);
    }

    @After
    public void tearDown() throws Exception {
        webServer.shutdown();
    }

    @Override
    public void downloadReady(String mimeType, ByteArrayOutputStream buffer) {
        System.out.println(TAG + " Content-Type: " + mimeType);
        content = buffer.toByteArray();
        downloadReady = true;
    }
}
