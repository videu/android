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

package club.sandtler.devid.data;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;

import club.sandtler.devid.data.model.User;
import club.sandtler.devid.lib.NetworkUtil;

/**
 * Class for retrieving other user's data from the backend server.
 *
 * TODO: Use resource string ids rather than hard-coded error messages
 */
public class UserDataSource {

    /** The HTTP URL string for retrieving a user by their user name. */
    private static final String BY_USER_NAME_PATH = "/user/byUserName/%s";
    /** The HTTP URL string for retrieving a user by their unique id. */
    private static final String BY_ID_PATH = "/user/byId/%s";
    /** The HTTP URL string for retrieving a profile picture from the CDN. */
    private static final String PP_PATH = "/pp/%s";

    /** The network utility instance for performing the POST request. */
    private final NetworkUtil mNetworkUtil;

    /**
     * Create a new user data source.
     */
    public UserDataSource() {
        // The default instance does not carry authentication headers,
        // which is what we want here (we don't have an auth token yet).
        this.mNetworkUtil = NetworkUtil.getDefault();
    }

    /**
     * Fetch user details from the backend server.
     *
     * @param userId The user id.
     * @return The user, or an error object it the user could not be fetched.
     */
    public Result<User> getById(@NonNull String userId) {
        if (!isIdValid(userId)) {
            return new Result.Error(new IllegalArgumentException("Invalid user id format"));
        }

        final String path = String.format(UserDataSource.BY_ID_PATH, userId);
        return retrieveByPath(path);
    }

    /**
     * Fetch user details from the backend server.
     *
     * @param userName The user name.
     * @return The user, or an error object it the user could not be fetched.
     */
    public Result<User> getByUserName(@NonNull String userName) {
        if (!isUserNameValid(userName)) {
            return new Result.Error(new IllegalArgumentException("Invalid username"));
        }

        final String path = String.format(UserDataSource.BY_USER_NAME_PATH, userName);
        return retrieveByPath(path);
    }

    /**
     * Download the user's profile picture, if present.
     *
     * @param userId The user id.
     * @return The user's profile picture.
     */
    public Result<Bitmap> getPP(@NonNull String userId) {
        if (!isIdValid(userId)) {
            return new Result.Error(new IllegalArgumentException("Invalid user id format"));
        }

        Bitmap bm;

        try {
            bm = this.mNetworkUtil.getBitmap(String.format(PP_PATH, userId));
        } catch (IOException e) {
            return new Result.Error(e);
        }

        return new Result.Success<>(bm);
    }

    /**
     * Retrieve and parse user data when we already know the path.
     *
     * @param path The path to use for the GET operation.
     * @return The result of the fetch operation.
     */
    private Result<User> retrieveByPath(String path) {
        try {
            final JSONObject userData = this.mNetworkUtil.get(path);
            return new Result.Success<>(User.fromJSON(userData));
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    /**
     * Perform a sanity check on a user id.
     *
     * @param userId The user id.
     * @return Whether the user id is syntactically valid.
     */
    private boolean isIdValid(@NonNull String userId) {
        return userId.matches("^[a-f0-9]{24}$");
    }

    /**
     * Perform a sanity check on a user name.
     * No whitespace allowed.
     *
     * @param userName The user name.
     * @return Whether the user name is syntactically valid.
     */
    private boolean isUserNameValid(@NonNull String userName) {
        return userName.matches("^[a-zA-Z0-9_]{2,16}$");
    }

}
