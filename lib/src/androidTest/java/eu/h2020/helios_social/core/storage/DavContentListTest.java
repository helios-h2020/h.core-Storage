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
public class DavContentListTest implements ListingReadyListener {
    private static final String TAG = "DavContentListTest";
    private MockWebServer webServer;
    private HttpUrl mockUrl;
    private ListingReadyListener listener = this;
    private boolean listingReady = false;
    private long retval;
    private String[] listed;

    @Before
    public void setUp() throws IOException {
        webServer = new MockWebServer();
        webServer.setDispatcher(new okhttp3.mockwebserver.Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest request) throws InterruptedException {
                System.out.println(TAG + " Request is " + request.toString());
                switch (request.getPath()) {
                    case "/webdav/":
                        return new MockResponse().setResponseCode(207).setBody(getTestResponseMessage()).setHeader("Content-Type", "text/xml; charset=\"utf-8\"");
                    default:
                        return new MockResponse().setResponseCode(404);
                }
            }

            private String getTestResponseMessage() {
                return new String(
                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<D:multistatus xmlns:D=\"DAV:\" xmlns:ns0=\"DAV:\">\n" +
                        "<D:response xmlns:lp1=\"DAV:\" xmlns:lp2=\"http://apache.org/dav/props/\">\n" +
                        "<D:href>/webdav/</D:href>\n" +
                        "<D:propstat>\n" +
                        "<D:prop>\n" +
                        "<lp1:resourcetype><D:collection/></lp1:resourcetype>\n" +
                        "<lp1:creationdate>2020-12-20T20:09:16Z</lp1:creationdate>\n" +
                        "<lp1:getlastmodified>Sun, 20 Dec 2020 20:09:16 GMT</lp1:getlastmodified>\n" +
                        "<lp1:getetag>\"1000-5b6eaea885f7f\"</lp1:getetag>\n" +
                        "<D:supportedlock>\n" +
                        "<D:lockentry>\n" +
                        "<D:lockscope><D:exclusive/></D:lockscope>\n" +
                        "<D:locktype><D:write/></D:locktype>\n" +
                        "</D:lockentry>\n" +
                        "<D:lockentry>\n" +
                        "<D:lockscope><D:shared/></D:lockscope>\n" +
                        "<D:locktype><D:write/></D:locktype>\n" +
                        "</D:lockentry>\n" +
                        "</D:supportedlock>\n" +
                        "<D:lockdiscovery/>\n" +
                        "<D:getcontenttype>httpd/unix-directory</D:getcontenttype>\n" +
                        "</D:prop>\n" +
                        "<D:status>HTTP/1.1 200 OK</D:status>\n" +
                        "</D:propstat>\n" +
                        "</D:response>\n" +
                        "<D:response xmlns:lp1=\"DAV:\" xmlns:lp2=\"http://apache.org/dav/props/\">\n" +
                        "<D:href>/webdav/testfile2</D:href>\n" +
                        "<D:propstat>\n" +
                        "<D:prop>\n" +
                        "<lp1:resourcetype/>\n" +
                        "<lp1:creationdate>2020-12-20T19:56:56Z</lp1:creationdate>\n" +
                        "<lp1:getcontentlength>27</lp1:getcontentlength>\n" +
                        "<lp1:getlastmodified>Sun, 20 Dec 2020 19:56:56 GMT</lp1:getlastmodified>\n" +
                        "<lp1:getetag>\"1b-5b6eabe6c0c7d\"</lp1:getetag>\n" +
                        "<lp2:executable>F</lp2:executable>\n" +
                        "<D:supportedlock>\n" +
                        "<D:lockentry>\n" +
                        "<D:lockscope><D:exclusive/></D:lockscope>\n" +
                        "<D:locktype><D:write/></D:locktype>\n" +
                        "</D:lockentry>\n" +
                        "<D:lockentry>\n" +
                        "<D:lockscope><D:shared/></D:lockscope>\n" +
                        "<D:locktype><D:write/></D:locktype>\n" +
                        "</D:lockentry>\n" +
                        "</D:supportedlock>\n" +
                        "<D:lockdiscovery/>\n" +
                        "</D:prop>\n" +
                        "<D:status>HTTP/1.1 200 OK</D:status>\n" +
                        "</D:propstat>\n" +
                        "</D:response>\n" +
                        "<D:response xmlns:lp1=\"DAV:\" xmlns:lp2=\"http://apache.org/dav/props/\">\n" +
                        "<D:href>/webdav/testfile</D:href>\n" +
                        "<D:propstat>\n" +
                        "<D:prop>\n" +
                        "<lp1:resourcetype/>\n" +
                        "<lp1:creationdate>2020-12-20T20:09:16Z</lp1:creationdate>\n" +
                        "<lp1:getcontentlength>9</lp1:getcontentlength>\n" +
                        "<lp1:getlastmodified>Sun, 20 Dec 2020 20:09:16 GMT</lp1:getlastmodified>\n" +
                        "<lp1:getetag>\"9-5b6eaea831fb9\"</lp1:getetag>\n" +
                        "<lp2:executable>F</lp2:executable>\n" +
                        "<D:supportedlock>\n" +
                        "<D:lockentry>\n" +
                        "<D:lockscope><D:exclusive/></D:lockscope>\n" +
                        "<D:locktype><D:write/></D:locktype>\n" +
                        "</D:lockentry>\n" +
                        "<D:lockentry>\n" +
                        "<D:lockscope><D:shared/></D:lockscope>\n" +
                        "<D:locktype><D:write/></D:locktype>\n" +
                        "</D:lockentry>\n" +
                        "</D:supportedlock>\n" +
                        "<D:lockdiscovery/>\n" +
                        "</D:prop>\n" +
                        "<D:status>HTTP/1.1 200 OK</D:status>\n" +
                        "</D:propstat>\n" +
                        "</D:response>\n" +
                        "</D:multistatus>\n");
            }
        });
        webServer.start();
        webServer.noClientAuth();
        mockUrl = webServer.url("");
        System.out.println(TAG + " URL is " + mockUrl.toString());
    }

    @Test
    public void listingTest() {
        new DavContentList(listener,null, null).execute(mockUrl + "webdav/");
        // new DavContentList(listener,"test", "Ahgh2Lul").execute("http://10.0.0.56/webdav/");
        while (!listingReady)
            ;
        System.out.println(TAG + " retval=" + retval);
        assertTrue(retval == 3);
    }

    @After
    public void tearDown() throws Exception {
        webServer.shutdown();
    }

    @Override
    public void listingReady(Long result, String[] entries) {
        retval = result;
        listed = entries;
        listingReady = true;
    }
}
