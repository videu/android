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

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import club.sandtler.devid.R;
import club.sandtler.devid.lib.Constants;

/**
 * Fragment for the video player w/ controls.
 */
public class VideoPlayerFragment extends Fragment {

    /** Fragment argument for specifying the video id. */
    public static final String KEY_VIDEO_ID =
            "club.sandtler.devid.ui.player.VideoPlayerFragment.VIDEO_ID";

    /** The player instance. */
    private SimpleExoPlayer mPlayer;
    /** The player's view root. */
    private PlayerView mPlayerView;
    /** The video id. */
    private String mVideoId = null;

    /**
     * Create a new instance of this fragment.
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

    @Override
    public void onCreate(Bundle savedInstanceState) throws IllegalStateException {
        super.onCreate(savedInstanceState);

        mPlayer = ExoPlayerFactory.newSimpleInstance(requireContext());

        Bundle args = getArguments();
        if (args != null) {
            mVideoId = args.getString(KEY_VIDEO_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_player, container, false);

        if (mVideoId != null) {
            mPlayerView = root.findViewById(R.id.player_view);
            setupPlayer();
        }

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }

    /**
     * Setup the player by referencing UI components
     * and passing all other required parameters.
     */
    private void setupPlayer() {
        mPlayerView.setPlayer(this.mPlayer);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                requireContext(),
                Util.getUserAgent(
                        requireContext(),
                        getResources().getString(R.string.app_name)
                )
        );
        Uri videoUri = Uri.parse(
                Constants.URLPaths.CDN_ROOT
                        + String.format(Constants.URLPaths.CDN_VIDEO_DEFAULT, mVideoId)
        );
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);
        mPlayer.prepare(videoSource);
    }

}
