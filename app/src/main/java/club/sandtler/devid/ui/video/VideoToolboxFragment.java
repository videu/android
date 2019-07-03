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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import club.sandtler.devid.R;
import club.sandtler.devid.data.Result;
import club.sandtler.devid.data.model.Video;

/**
 * Fragment containing a box of various video tool buttons like vote or share.
 */
public class VideoToolboxFragment extends Fragment {

    public static final String KEY_VIDEO_ID =
            "club.sandtler.devid.ui.video.VideoToolboxFragment.VIDEO_ID";

    private VideoViewModel mViewModel;
    private String mVideoId;

    private ImageView mUpvoteButton;
    private ImageView mDownvoteButton;
    private TextView mUpvotesText;
    private TextView mDownvotesText;

    /**
     * Create a new instance of this fragment.
     *
     * @param videoId The video id.
     * @return The new fragment instance.
     */
    public static VideoToolboxFragment newInstance(String videoId) {
        VideoToolboxFragment fragment = new VideoToolboxFragment();
        Bundle args = new Bundle();
        args.putString(KEY_VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    public VideoToolboxFragment() {
        // Required public empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        mViewModel = ViewModelProviders.of(this, new VideoViewModelFactory())
                .get(VideoViewModel.class);

        handleFragmentArgs(args);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_toolbox, container, false);

        mUpvotesText = root.findViewById(R.id.video_toolbox_upvote_text);
        mDownvotesText = root.findViewById(R.id.video_toolbox_downvote_text);

        mUpvoteButton = root.findViewById(R.id.video_toolbox_upvote_button);
        mDownvoteButton = root.findViewById(R.id.video_toolbox_downvote_button);

        View.OnClickListener voteClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.video_toolbox_upvote_button:
                        vote(Video.RATING_LIKE);
                        break;
                    case R.id.video_toolbox_downvote_button:
                        vote(Video.RATING_DISLIKE);
                        break;
                }
            }

        };
        root.findViewById(R.id.video_toolbox_upvote_container)
                .setOnClickListener(voteClickListener);
        root.findViewById(R.id.video_toolbox_downvote_container)
                .setOnClickListener(voteClickListener);

        return root;
    }

    /**
     * Handle arguments passed to this fragment.
     *
     * @param args The arguments.
     */
    private void handleFragmentArgs(@NonNull Bundle args) {
        mVideoId = args.getString(KEY_VIDEO_ID);

        final LiveData<Result<Video>> video = mViewModel.getVideo(mVideoId);
        video.observe(this, new Observer<Result<Video>>() {

            @Override
            public void onChanged(Result<Video> videoResult) {
                if (videoResult instanceof Result.Success) {
                    updateUiWithVideo(((Result.Success<Video>) videoResult).getData());
                }
            }

        });
    }

    /**
     * Update the user interface to show an updated video
     * @param video
     */
    private void updateUiWithVideo(Video video) {
        mUpvotesText.setText(String.valueOf(video.getLikes()));
        mDownvotesText.setText(String.valueOf(video.getDislikes()));

        switch (video.getOwnRating()) {
            case Video.RATING_NEUTRAL:
                mUpvoteButton.setImageResource(R.drawable.ic_thumb_up_24dp);
                mDownvoteButton.setImageResource(R.drawable.ic_thumb_down_24dp);
                break;
            case Video.RATING_LIKE:
                mUpvoteButton.setImageResource(R.drawable.ic_thumb_up_color_24dp);
                mDownvoteButton.setImageResource(R.drawable.ic_thumb_down_24dp);
                break;
            case Video.RATING_DISLIKE:
                mUpvoteButton.setImageResource(R.drawable.ic_thumb_up_24dp);
                mDownvoteButton.setImageResource(R.drawable.ic_thumb_down_color_24dp);
                break;
        }
    }

    /**
     * Callback for up or downvote button clicks.
     *
     * @param rating Either
     */
    private void vote(byte rating) {
        mViewModel.vote(mVideoId, rating);
    }

}
