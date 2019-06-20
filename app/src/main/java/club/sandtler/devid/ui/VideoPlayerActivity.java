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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import club.sandtler.devid.R;
import club.sandtler.devid.lib.Constants;

/**
 * Activity for playing videos.
 *
 * This activity needs to be passed the video id as an extra in the Intent
 * launching it, over the {@link #EXTRA_VIDEO_ID} key.
 */
public class VideoPlayerActivity extends AppCompatActivity {

    /** Bundle key for storing the video progress in seconds. */
    private static final String BUNDLE_VIDEO_PROGRESS = "video_progress";

    /**
     * The extra name for supplying the video id
     * in Intents starting this activity.
     */
    public static final String EXTRA_VIDEO_ID =
            "club.sandtler.devid.ui.VideoPlayerActivity.VIDEO_ID";

    private MediaController mMediaController;
    private VideoView mVideoView;

    /** The current video playback position in milliseconds. */
    private int mCurrentPosition = 0;
    /** The current video id. */
    private String mVideoId;

    private boolean mIsFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.mIsFullscreen = true;
        }

        if (savedInstanceState != null) {
            this.mCurrentPosition = savedInstanceState
                    .getInt(VideoPlayerActivity.BUNDLE_VIDEO_PROGRESS);
        }

        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.initPlayer();
        if (this.mVideoId != null) {
            playVideo(this.mVideoId);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Can be received when the activity is paused (because the user
        // is in another app, for example) and clicked on an app link.
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*
         * The video view might have already been destroyed in the
         * onSaveInstanceState() callback, we will therefore need to
         * save the video progress here.
         */
        this.mCurrentPosition = this.mVideoView.getCurrentPosition();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            this.mVideoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.mVideoView.stopPlayback();
    }

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);

        saveState.putInt(
                VideoPlayerActivity.BUNDLE_VIDEO_PROGRESS,
                this.mCurrentPosition
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!this.mIsFullscreen) {
            return;
        }

        if (hasFocus) {
            hideSystemUI();
        } else {
            showSystemUI();
        }
    }

    /**
     * Play the specified video id.
     *
     * @param videoId The video id.
     */
    public void playVideo(String videoId) {
        String videoUri;

        videoUri = String.format(
                Constants.CDN_ROOT + "/video/%s",
                videoId
        );
        this.mVideoView.setVideoURI(Uri.parse(videoUri));
    }

    /**
     * Handle an intent passed to this activity.
     *
     * @param intent The intent.
     */
    private void handleIntent(Intent intent) {
        if (intent.hasExtra(VideoPlayerActivity.EXTRA_VIDEO_ID)) {
            this.mVideoId = intent.getStringExtra(VideoPlayerActivity.EXTRA_VIDEO_ID);
        } else {
            // Check if the activity has been launched by the user clicking
            // on a link to https://devid.sandtler.club/watch/<videoId>
            String appLinkAction = intent.getAction();
            Uri appLinkData = intent.getData();

            if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
                List<String> segments = appLinkData.getPathSegments();
                try {
                    this.mVideoId = segments.get(1);
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(this, "Invalid video link", Toast.LENGTH_LONG)
                            .show();
                    finish();
                }
            }
        }
    }

    /**
     * Initialize the player by setting its MediaController
     * and registering all required listeners.
     */
    private void initPlayer() {
        this.mVideoView = findViewById(R.id.view_video_player);
        this.mMediaController = new MediaController(this);
        this.mMediaController.setMediaPlayer(this.mVideoView);
        this.mVideoView.setMediaController(this.mMediaController);

        this.mVideoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        final VideoView videoView = findViewById(R.id.view_video_player);

                        // If the activity has been destroyed while playing,
                        // set it back to where it was previously.
                        if (mCurrentPosition <= 0) {
                            mCurrentPosition = 1;
                        }
                        videoView.seekTo(mCurrentPosition);

                        videoView.requestFocus();
                        videoView.start();
                    }
                }
        );
    }

    /**
     * Enable "lean back" fullscreen mode.
     * This should only be called when the orientation is landscape.
     */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
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
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

}
