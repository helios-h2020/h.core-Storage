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
import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DavContentDeleteTest implements OperationReadyListener {
    private static final String TAG = "DavContentDeleteTest";
    private MockWebServer webServer;
    private HttpUrl mockUrl;
    private OperationReadyListener listener = this;
    private boolean deleteReady = false;
    private String data = "Hello world";
    private long retval;

    @Before
    public void setUp() throws IOException {
        webServer = new MockWebServer();
        webServer.setDispatcher(new okhttp3.mockwebserver.Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest request) throws InterruptedException {
                System.out.println(TAG + " Request is " + request.toString());
                switch (request.getPath()) {
                    case "/webdav/delete42.txt":
                        return new MockResponse().setResponseCode(200);
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
    public void deleteTest() {
        new DavContentDelete(listener,null, null).execute(mockUrl + "webdav/delete42.txt");
        while (!deleteReady)
            ;
        assertTrue(retval == 1);
    }

    @After
    public void tearDown() throws Exception {
        webServer.shutdown();
    }

    @Override
    public void operationReady(Long result) {
        retval = result;
        deleteReady = true;
    }
}
