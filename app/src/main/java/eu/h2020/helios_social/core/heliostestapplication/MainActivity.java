package eu.h2020.helios_social.core.heliostestapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import eu.h2020.helios_social.core.storage.DownloadReadyListener;
import eu.h2020.helios_social.core.storage.HeliosStorageManager;
import eu.h2020.helios_social.core.storage.OperationReadyListener;
import eu.h2020.helios_social.core.storage.ListingReadyListener;

public class MainActivity extends AppCompatActivity
        implements DownloadReadyListener, OperationReadyListener, ListingReadyListener {

    OperationReadyListener operationListener = this;
    ListingReadyListener listingListener = this;
    DownloadReadyListener downloadListener = this;
    ArrayAdapter<String> adapter = null;
    ArrayList<String> textArray;
    int testseq = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textArray = new ArrayList<String>();
        textArray.add("Press FAB to run storage tests (one-by-one)\n" + VersionUtils.getAndroidVersion() + "\n" + VersionUtils.getDeviceName());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adapter = new ArrayAdapter<String>(this,  R.layout.activity_listview, textArray);

        ListView listView = (ListView) findViewById(R.id.text_list);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(adapter);
        FloatingActionButton fab = findViewById(R.id.fab);

        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                String testText = "Test text";
                HeliosStorageManager storage = HeliosStorageManager.getInstance();
                String str;
                String now = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                try {
                    switch (testseq) {
                        case 0:
                            storage.upload("koenew.txt", testText.getBytes(), operationListener);
                            str = new String("Upload file " + now);
                            try {
                                adapter.add(str);
                            } catch (UnsupportedOperationException e) {
                                e.printStackTrace();
                            }
                            testseq++;
                            break;
                        case 1:
                            storage.delete("koenew.txt", operationListener);
                            str = new String("Delete file " + now);
                            try {
                                adapter.add(str);
                            } catch (UnsupportedOperationException e) {
                                e.printStackTrace();
                            }
                            testseq++;
                            break;
                        case 2:
                            storage.uploadSync("koenew.txt", testText.getBytes(), null);
                            storage.uploadSync("koenew2.txt", testText.getBytes(), null);
                            storage.list("koenew.txt", listingListener);
                            str = new String("List file " + now);
                            try {
                                adapter.add(str);
                            } catch (UnsupportedOperationException e) {
                                e.printStackTrace();
                            }
                            testseq++;
                            break;
                        case 3:
                            storage.download("koenew.txt", downloadListener);
                            str = new String("Download file " + now);
                            try {
                                adapter.add(str);
                            } catch (UnsupportedOperationException e) {
                                e.printStackTrace();
                            }
                            testseq = 0;
                            break;
                     }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear_view) {
            textArray.clear();
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void downloadReady(String mimeType, ByteArrayOutputStream buffer) {
        // This is from DownloadListener interface
        Context context = getApplicationContext();
        CharSequence text = "Download is now ready: " + mimeType + " Size=" + buffer.size();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        // Expected values
        if (testseq == 0 && buffer.size() == 9) {
            adapter.add("[Ok] " + text.toString());
        } else {
            adapter.add("[Fail] " + text.toString());
        }
    }

    public void operationReady(Long result) {
        Context context = getApplicationContext();
        CharSequence text = "Operation is now ready: " + result;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        // Expected values
        if (testseq == 1) {
            if (result == 9) {
                adapter.add("[Ok] " + text.toString());
            } else {
                adapter.add("[Fail] " + text.toString());
            }
        } else if (testseq == 2) {
            if (result == 1) {
                adapter.add("[Ok] " + text.toString());
            } else {
                adapter.add("[Fail] " + text.toString());
            }
        } else  {
            adapter.add("Internal error");
        }
    }

    public void listingReady(Long result, String[] entries) {
        Context context = getApplicationContext();
        CharSequence text;
        if (entries != null) {
            text = "Files:" + entries.length + "\n" + String.join("\n", entries);
        } else {
            text = "No entries found";
        }
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        // Expected values
        if (testseq == 3 && entries.length == 2) {
            adapter.add("[Ok] " + text.toString());
        } else {
            adapter.add("[Fail] " + text.toString());
        }
    }
}
