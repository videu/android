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

package club.sandtler.devid.ui.video;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import club.sandtler.devid.R;
import club.sandtler.devid.lib.Constants;

import static club.sandtler.devid.lib.Constants.URLPaths;

/**
 * Fragment for the video player w/ controls.
 */
public class VideoPlayerFragment extends Fragment implements Player.EventListener {

    /** Fragment argument for specifying the video id. */
    public static final String KEY_VIDEO_ID =
            "club.sandtler.devid.ui.player.VideoPlayerFragment.VIDEO_ID";

    /** The player instance. */
    private SimpleExoPlayer mPlayer;
    /** The player's view root. */
    private PlayerView mPlayerView;
    private ProgressBar mProgressBar;
//    private SeekBar mSeekBar;
    /**
     * The current playback position.
     * Used for recreating the player on configuration changes.
     */
    private long mCurrentPosition = 0L;
    /** The video id. */
    private String mVideoId = null;

    /**
     * Create a new instance of this fragment.
     *
     * @param videoId The video id to show.
     * @return The new fragment.
     */
    public static VideoPlayerFragment newInstance(String videoId) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(KEY_VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    /** {@inheritDoc} */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mPlayer = ExoPlayerFactory.newSimpleInstance(context);
    }

    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle savedInstanceState) throws IllegalStateException {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Bundle args = getArguments();
        if (args != null) {
            mVideoId = args.getString(KEY_VIDEO_ID);
        }
    }

    /** {@inheritDoc} */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_player, container, false);

        mPlayerView = root.findViewById(R.id.player_view);
        mProgressBar = root.findViewById(R.id.player_view_loading_bar);
//        mSeekBar = root.findViewById(R.id.video_player_seek_bar);

        return root;
    }

    /** {@inheritDoc} */
    @Override
    public void onStart() {
        super.onStart();

        setupPlayer();
        mPlayer.setPlayWhenReady(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onDetach() {
        super.onDetach();

        mCurrentPosition = mPlayer.getCurrentPosition();
        mPlayer.release();
        mPlayer = null;
    }

    /** {@inheritDoc} */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_IDLE:
                mProgressBar.setVisibility(View.GONE);
                Log.v(Constants.LOG_TAG, "STATE_IDLE");
                break;
            case Player.STATE_BUFFERING:
                mProgressBar.setVisibility(View.VISIBLE);
                Log.v(Constants.LOG_TAG, "STATE_BUFFERING");
                break;
            case Player.STATE_READY:
                mProgressBar.setVisibility(View.GONE);
                Log.v(Constants.LOG_TAG, "STATE_READY");
                break;
            case Player.STATE_ENDED:
                mProgressBar.setVisibility(View.GONE);
                Log.v(Constants.LOG_TAG, "STATE_ENDED");
                break;
        }
    }

    /**
     * Craft a new {@code MediaSource} to be used with the {@code ExoPlayer}
     * instance.  The source URI will be generated from the current video id.
     *
     * @return The MediaSource.
     * @see #mVideoId
     */
    private MediaSource createMediaSource() {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                requireContext(),
                Util.getUserAgent(
                        requireContext(),
                        getString(R.string.app_name)
                )
        );
        Uri videoUri = Uri.parse(
                URLPaths.CDN_ROOT
                        + String.format(URLPaths.CDN_VIDEO_DEFAULT, mVideoId)
        );
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);
    }

    /**
     * Setup the player by referencing UI components
     * and passing all other required parameters.
     */
    private void setupPlayer() {
        mPlayer.addListener(this);
        mPlayerView.setPlayer(mPlayer);
        mPlayer.prepare(createMediaSource());
        mPlayer.seekTo(mCurrentPosition);
    }

}
