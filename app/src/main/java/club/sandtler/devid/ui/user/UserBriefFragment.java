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

package club.sandtler.devid.ui.user;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import club.sandtler.devid.R;
import club.sandtler.devid.data.Result;
import club.sandtler.devid.data.model.User;

/**
 * Fragment class for displaying a brief and compact user summary
 * including profile picture, display name and subscriber count.
 *
 * This is mainly used by the video details fragment
 * for displaying information on the uploader.
 */
public class UserBriefFragment extends Fragment {

    public static final String KEY_USER_ID =
            "club.sandtler.devid.ui.user.UserBriefFragment.USER_ID";

    /** The view model. */
    private UserViewModel mViewModel;

    private TextView mDisplayNameText;
    private TextView mSubCountText;
    private ImageView mUserPP;

    /**
     * Create a new instance of this fragment.
     *
     * @param userId The user id.
     * @return The new fragment instance.
     */
    public static UserBriefFragment newInstance(String userId) {
        UserBriefFragment fragment = new UserBriefFragment();
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public UserBriefFragment() {
        // required public empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, new UserViewModelFactory())
                .get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_brief, container, false);

        mDisplayNameText = root.findViewById(R.id.user_brief_display_name);
        mSubCountText = root.findViewById(R.id.user_brief_sub_count);
        mUserPP = root.findViewById(R.id.user_brief_pp);

        Bundle args = getArguments();
        if (args != null) {
            handleFragmentArgs(args);
        }

        return root;
    }

    /**
     * Handle the arguments passed to the fragment on its creation.
     * @param args
     */
    private void handleFragmentArgs(@NonNull Bundle args) {
        String userId = args.getString(KEY_USER_ID);

        mViewModel.getById(userId).observe(this, new Observer<Result<User>>() {

            @Override
            public void onChanged(Result<User> userResult) {
                if (userResult instanceof Result.Success) {
                    updateUiWithUser(((Result.Success<User>) userResult).getData());
                } else if (userResult instanceof Result.Error) {
                    // TODO: Handle errors properly
                    ((Result.Error) userResult).getError().printStackTrace();
                }
            }

        });

        mViewModel.getPP(userId).observe(this, new Observer<Result<Bitmap>>() {

            @Override
            public void onChanged(Result<Bitmap> bitmapResult) {
                if (bitmapResult instanceof Result.Success) {
                    updatePP(((Result.Success<Bitmap>) bitmapResult).getData());
                } else if (bitmapResult instanceof Result.Error) {
                    // TODO: Handle errors properly
                    ((Result.Error) bitmapResult).getError().printStackTrace();
                }
            }

        });
    }

    /**
     * Update the UI ith new user data.
     * Called when the ViewModel's LiveData have changed after retrieving the user data.
     * @param u The user.
     */
    private void updateUiWithUser(User u) {
        mDisplayNameText.setText(u.getDisplayName());
        // TODO: Add backend feature to transmit subscriber count
        mSubCountText.setText(String.format(getResources().getString(R.string.sub_count), 420));
    }

    /**
     * Update the user's profile picture.
     *
     * @param pp The profile picture as a bitmap.
     */
    private void updatePP(Bitmap pp) {
        mUserPP.setImageBitmap(pp);
    }

}
