/*
 * Copyright (c) 2019 Felix Kopp <sandtler@sandtler.club>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package club.sandtler.devid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import club.sandtler.devid.R;

/**
 * The application's entry point activity.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Add actual action handlers for these.
        switch (item.getItemId()) {
            case R.id.menu_item_about:
                Toast.makeText(
                        getApplicationContext(),
                        "About",
                        Toast.LENGTH_SHORT
                ).show();
                break;

            case R.id.menu_item_settings:
                Toast.makeText(
                        getApplicationContext(),
                        "Settings",
                        Toast.LENGTH_SHORT
                ).show();
                break;

            case R.id.menu_item_my_account:
                openUserView();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void openUserView() {
        Intent intent = new Intent(this, UserViewActivity.class);
        intent.putExtra(UserViewActivity.EXTRA_USER_NAME, "sandtler");
        startActivity(intent);
    }

    public void playVideo(View v) {
        Intent intent;

        intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.EXTRA_VIDEO_ID, "5d1d2339e710560cdf5c5b80");
        startActivity(intent);
    }

}
