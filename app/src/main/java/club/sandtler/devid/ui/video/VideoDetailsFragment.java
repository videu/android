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

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.SimpleDateFormat;

import club.sandtler.devid.R;
import club.sandtler.devid.data.Result;
import club.sandtler.devid.data.model.Video;
import club.sandtler.devid.ui.user.UserBriefFragment;

/**
 * Fragment for displaying details to a video.
 * Usually embedded below the player.
 */
public class VideoDetailsFragment extends Fragment {

    public static final String KEY_VIDEO_ID =
            "club.sandtler.devid.ui.video.VideoDetailsFragment.VIDEO_ID";

    /** The video id. */
    private String mVideoId;

    /** The view model. */
    private VideoViewModel mViewModel;

    /** View element for the video title. */
    private TextView mVideoTitleView;
    private TextView mVideoUploadDateView;
    /** View element for the video description. */
    private TextView mVideoDescriptionView;
    /** View root for the user brief fragment. */
    private UserBriefFragment mUserBriefFragment;
    private VideoToolboxFragment mVideoToolboxFragment;

    /**
     * Create a new instance of this fragment.
     *
     * @param videoId The video id.
     * @return The new fragment instance.
     */
    public static VideoDetailsFragment newInstance(String videoId) {
        VideoDetailsFragment fragment = new VideoDetailsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    public VideoDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, new VideoViewModelFactory())
                .get(VideoViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_details, container, false);

        mVideoTitleView = root.findViewById(R.id.video_details_title);
        mVideoUploadDateView = root.findViewById(R.id.video_details_upload_date);
        mVideoDescriptionView = null;

        Bundle args = getArguments();
        if (args != null) {
            handleFragmentArgs(args);
        }

        return root;
    }

    /**
     * Handle arguments passed to this fragment and update the UI accordingly.
     *
     * @param args The arguments.
     */
    private void handleFragmentArgs(@NonNull Bundle args) {
        mVideoId = args.getString(KEY_VIDEO_ID);

        mViewModel.getVideo(mVideoId).observe(this, videoResult -> {
            if (videoResult instanceof Result.Success) {
                updateUiWithVideo(((Result.Success<Video>) videoResult).getData());
            } else if (videoResult instanceof Result.Error) {
                ((Result.Error) videoResult).getError().printStackTrace();
            }
        });
    }

    /**
     * Update the UI to show the video information after
     * the ViewModel's LiveData changed.
     *
     * @param video The video.
     */
    private void updateUiWithVideo(Video video) {
        mVideoTitleView.setText(video.getTitle());
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        mVideoUploadDateView.setText(String.format(
                getResources().getString(R.string.upload_date),
                sdf.format(video.getUploadDate())
        ));
        setupFragments(video);
    }

    /**
     * Instantiate all child fragments and add them to the layout.
     *
     * @param video The video.
     */
    private void setupFragments(Video video) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

//        if (mUserBriefFragment == null) {
            mUserBriefFragment = UserBriefFragment.newInstance(video.getUserId());
            fragmentTransaction.add(
                    R.id.video_details_user_brief_fragment_container,
                    mUserBriefFragment
            );
//        }
//        if (mVideoToolboxFragment == null) {
            mVideoToolboxFragment = VideoToolboxFragment.newInstance(video.getId());
            fragmentTransaction.add(
                    R.id.video_details_toolbox_fragment_container,
                    mVideoToolboxFragment
            );
//        }

        fragmentTransaction.commit();
    }

}
