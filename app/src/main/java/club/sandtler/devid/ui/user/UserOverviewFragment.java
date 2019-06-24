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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import club.sandtler.devid.DEvidApp;
import club.sandtler.devid.R;
import club.sandtler.devid.data.Result;
import club.sandtler.devid.data.model.User;
import club.sandtler.devid.ui.UserViewActivity;

/**
 * Fragment class for displaying the "overview" tab in the user view activity.
 */
public class UserOverviewFragment extends Fragment {

    /** The Fragment argument name for the user id */
    public static final String KEY_USER_ID = UserViewActivity.EXTRA_USER_ID;
    /** The Fragment argument name for the user name. */
    public static final String KEY_USER_NAME = UserViewActivity.EXTRA_USER_NAME;

    /** The view model. */
    private UserViewModel mViewModel;

    /** The display name text view. */
    private TextView mDisplayNameText;
    /** The user name text view. */
    private TextView mUserNameText;
    /** The profile picture view. */
    private ImageView mUserPP;

    /*
     * This is necessary because if we get the user id passed directly as an
     * argument to this Fragment, we don't need to wait for the User object
     * to be retrieved over the REST API but can rather send the image request
     * to the CDN directly (should save about 50 ms or so).
     */
    /** Whether we have already started retrieving the user's profile picture. */
    private boolean mPPRetrieveStarted = false;

    /**
     * Get a new instance of this fragment.
     *
     * @param userSpec A bundle containing either the user name or id to display.
     * @return The new fragment instance.
     */
    public static UserOverviewFragment newInstance(Bundle userSpec) {
        UserOverviewFragment frag = new UserOverviewFragment();
        frag.setArguments(userSpec);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mViewModel = ViewModelProviders.of(this, new UserViewModelFactory())
                .get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mPPRetrieveStarted = false;
        View root = inflater.inflate(R.layout.fragment_user_overview, container, false);

        this.mDisplayNameText = root.findViewById(R.id.user_overview_display_name);
        this.mUserNameText = root.findViewById(R.id.user_overview_user_name);
        this.mUserPP = root.findViewById(R.id.user_overview_pp);
        handleFragmentArgs(getArguments());

        return root;
    }

    /**
     * Process all arguments passed to this fragment.
     *
     * @param args The arguments.
     */
    private void handleFragmentArgs(@Nullable Bundle args) {
        if (args == null) {
            return;
        }

        LiveData<Result<User>> data;
        if (args.containsKey(KEY_USER_ID)) {
            data = this.mViewModel.getById(args.getString(KEY_USER_ID));
            retrievePP(args.getString(KEY_USER_ID));
        } else if (args.containsKey(KEY_USER_NAME)) {
            data = this.mViewModel.getByUserName(args.getString(KEY_USER_NAME));
        } else {
            return;
        }

        data.observe(this, new Observer<Result<User>>() {

            @Override
            public void onChanged(Result<User> userResult) {
                if (userResult instanceof Result.Success) {
                    updateUIWithUser(((Result.Success<User>) userResult).getData());
                } else if (userResult instanceof Result.Error) {
                    updateUIWithError(((Result.Error) userResult).getError());
                }
            }

        });
    }

    /**
     * Update the UI when the user data has been fetched successfully.
     *
     * @param u The user.
     */
    private void updateUIWithUser(User u) {
        retrievePP(u.getId());

        this.mDisplayNameText.setText(u.getDisplayName());

        String userNameTmpl = getResources().getString(R.string.user_name_at_prefix_tmpl);
        this.mUserNameText.setText(String.format(userNameTmpl, u.getUserName()));
    }

    /**
     * Update the UI to display an error message.
     *
     * @param e The Exception that occurred while retrieving the user data.
     */
    private void updateUIWithError(Exception e) {
        // TODO: Show an actual error message
        Toast.makeText(DEvidApp.getAppContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    // TODO: Refactor these two methods

    /**
     * Update the UI to display the user's actual image rather than an
     *
     * @param bitmap
     */
    private void updateUIWithPP(Bitmap bitmap) {
        this.mUserPP.setImageBitmap(bitmap);
    }

    /**
     * Request the user's profile picture from the view model.
     * Calling this multiple times will have no effect.
     *
     * @param userId The user id.
     */
    private void retrievePP(String userId) {
        if (this.mPPRetrieveStarted) {
            return;
        }

        this.mPPRetrieveStarted = true;
        LiveData<Result<Bitmap>> data = this.mViewModel.getPP(userId);

        data.observe(this, new Observer<Result<Bitmap>>() {

            @Override
            public void onChanged(Result<Bitmap> bitmapResult) {
                if (bitmapResult instanceof Result.Success) {
                    updateUIWithPP(((Result.Success<Bitmap>) bitmapResult).getData());
                } else if (bitmapResult instanceof Result.Error) {
                    // TODO: Check if the download failed or the user actually has no PP
                    ((Result.Error) bitmapResult).getError().printStackTrace();
                }
            }

        });
    }

}
