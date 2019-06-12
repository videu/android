package club.sandtler.devid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * The application's entry point activity.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Add actual action handlers for these.
        switch (item.getItemId()) {
            case R.id.menu_item_about:
                Toast.makeText(
                        this.getApplicationContext(),
                        "About",
                        Toast.LENGTH_SHORT
                ).show();
                break;

            case R.id.menu_item_settings:
                Toast.makeText(
                        this.getApplicationContext(),
                        "Settings",
                        Toast.LENGTH_SHORT
                ).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
