package club.sandtler.devid;

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

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Activity for playing videos.
 *
 * This activity needs to be passed the video id as an extra in the Intent
 * launching it, over the {@link #EXTRA_VIDEO_ID} key.
 */
public class VideoPlayerActivity extends AppCompatActivity {

    private static final String BUNDLE_VIDEO_PROGRESS = "video_progress";

    /**
     * The extra name for supplying the video id
     * in Intents starting this activity.
     */
    public static final String EXTRA_VIDEO_ID =
            "club.sandtler.devid.VideoPlayerActivity.VIDEO_ID";

    private MediaController mediaController;
    private VideoView videoView;

    /** The current video playback position in milliseconds. */
    private int currentPosition = 0;
    /** The current video id. */
    private String videoId;

    private boolean isFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            this.isFullscreen = true;

        if (savedInstanceState != null) {
            this.currentPosition = savedInstanceState
                    .getInt(VideoPlayerActivity.BUNDLE_VIDEO_PROGRESS);
        }

        Intent intent = getIntent();
        if (intent != null)
            handleIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.initPlayer();
        if (this.videoId != null)
            playVideo(this.videoId);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*
         * The video view might have already been destroyed in the
         * onSaveInstanceState() callback, we will therefore need to
         * save the video progress here.
         */
        this.currentPosition = this.videoView.getCurrentPosition();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            this.videoView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.videoView.stopPlayback();
    }

    @Override
    protected void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);

        saveState.putInt(
                VideoPlayerActivity.BUNDLE_VIDEO_PROGRESS,
                this.currentPosition
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!this.isFullscreen)
            return;

        if (hasFocus)
            hideSystemUI();
        else
            showSystemUI();
    }

    /**
     * Play the specified video id.
     *
     * @param videoId The video id.
     */
    public void playVideo(String videoId) {
        String videoUri;

        videoUri = String.format(
                "https://cdn.devid.sandtler.club/video/%s",
                videoId
        );
        this.videoView.setVideoURI(Uri.parse(videoUri));
    }

    /**
     * Handle an intent passed to this activity.
     *
     * @param intent The intent.
     */
    private void handleIntent(Intent intent) {
        if (intent.hasExtra(VideoPlayerActivity.EXTRA_VIDEO_ID)) {
            this.videoId =
                    intent.getStringExtra(VideoPlayerActivity.EXTRA_VIDEO_ID);
        }
    }

    /**
     * Initialize the player by setting its MediaController
     * and registering all required listeners.
     */
    private void initPlayer() {
        this.videoView = findViewById(R.id.view_video_player);
        this.mediaController = new MediaController(this);
        this.mediaController.setMediaPlayer(this.videoView);
        this.videoView.setMediaController(this.mediaController);

        this.videoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        int currentPosition =
                                VideoPlayerActivity.this.currentPosition;
                        final VideoView videoView = VideoPlayerActivity.this
                                .findViewById(R.id.view_video_player);

                        // If the activity has been destroyed while playing,
                        // set it back to where it was previously.
                        if (currentPosition <= 0)
                            currentPosition = 1;
                        videoView.seekTo(currentPosition);

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
