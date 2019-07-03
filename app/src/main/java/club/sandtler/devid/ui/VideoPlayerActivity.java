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
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import club.sandtler.devid.R;
import club.sandtler.devid.ui.video.VideoDetailsFragment;
import club.sandtler.devid.ui.video.VideoPlayerFragment;

/**
 * Activity for playing videos.
 *
 * This activity needs to be passed the video id as an extra in the Intent
 * launching it, over the {@link #EXTRA_VIDEO_ID} key.
 *
 * TODO: Fix fullscreen (landscape layout) behavior
 */
public class VideoPlayerActivity extends AppCompatActivity {

    /**
     * The extra name for supplying the video id
     * in Intents starting this activity.
     */
    public static final String EXTRA_VIDEO_ID =
            "club.sandtler.devid.ui.VideoPlayerActivity.VIDEO_ID";

    /** If true, the player is in fullscreen mode. */
    private boolean mIsFullscreen = false;
    /** If true, the individual fragments have already been initialized. */
    private boolean mFragmentsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mIsFullscreen = true;
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Can be received when the activity is paused (because the user
        // is in another app, for example) and an app link is clicked.
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!mIsFullscreen) {
            return;
        }

        if (hasFocus) {
            hideSystemUI();
        } else {
            showSystemUI();
        }
    }

    /**
     * Initialize the activity's fragments.
     * Calling this multiple times will have no effect.
     *
     * @param videoId The video id.
     */
    private void setupFragments(@Nullable String videoId) {
        if (videoId == null || mFragmentsInitialized) {
            return;
        }
        mFragmentsInitialized = true;

        VideoPlayerFragment playerFragment = VideoPlayerFragment.newInstance(videoId);
        VideoDetailsFragment detailsFragment = VideoDetailsFragment.newInstance(videoId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.video_player_fragment_container, playerFragment);
        fragmentTransaction.add(R.id.video_details_fragment_container, detailsFragment);
        fragmentTransaction.commit();
    }

    /**
     * Handle an intent passed to this activity.
     *
     * @param intent The intent.
     */
    private void handleIntent(Intent intent) {
        String videoId = null;

        if (intent.hasExtra(EXTRA_VIDEO_ID)) {
            videoId = intent.getStringExtra(EXTRA_VIDEO_ID);
        } else {
            // Check if the activity has been launched by the user clicking
            // on a link to https://devid.sandtler.club/watch/<videoId>
            String appLinkAction = intent.getAction();
            Uri appLinkData = intent.getData();

            if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
                List<String> segments = appLinkData.getPathSegments();
                try {
                    videoId = segments.get(1);
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(this, "Invalid video link", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }

        setupFragments(videoId);
    }

    /**
     * Enable "lean back" fullscreen mode.
     * This should only be called when the orientation is landscape.
     */
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    /**
     * Show the system UI when the activity has lost focus,
     * i.e. when the status bar is being pulled down.
     */
    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

}
