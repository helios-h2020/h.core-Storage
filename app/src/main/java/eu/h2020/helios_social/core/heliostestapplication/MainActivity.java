package eu.h2020.helios_social.core.heliostestapplication;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import eu.h2020.helios_social.core.storage.DownloadReadyListener;
import eu.h2020.helios_social.core.storage.HeliosStorageManager;
import eu.h2020.helios_social.core.storage.OperationReadyListener;
import eu.h2020.helios_social.core.storage.ListingReadyListener;

public class MainActivity extends AppCompatActivity
        implements DownloadReadyListener, OperationReadyListener, ListingReadyListener {

    OperationReadyListener listener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                String testText = "Test text";
                HeliosStorageManager storage = HeliosStorageManager.getInstance();
                //storage.upload("koenew.txt", testText.getBytes(), null);
                storage.delete("koenew.txt", null);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void downloadReady(String mimeType, ByteArrayOutputStream buffer) {
        // This is from DownloadListener interface
        Context context = getApplicationContext();
        CharSequence text = "Download is now ready: " + mimeType + "Size=" + buffer.size();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }

    public void operationReady(Long result) {
        Context context = getApplicationContext();
        CharSequence text = "Operation is now ready: " + result;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void listingReady(Long result, String[] entries) {
        Context context = getApplicationContext();
        CharSequence text = "Files:" + entries.length + "\n" + String.join("\n", entries);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
