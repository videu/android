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
import static club.sandtler.devid.lib.Constants.URLPaths;

/**
 * Class for retrieving other user's data from the backend server.
 *
 * TODO: Use resource string ids rather than hard-coded error messages
 */
public class UserDataSource extends AbstractDataSource {

    /**
     * Fetch user details from the backend server.
     *
     * @param userId The user id.
     * @return The user, or an error object it the user could not be fetched.
     */
    @SuppressWarnings("unchecked")
    public Result<User> getById(@NonNull String userId) {
        if (!isIdValid(userId)) {
            return new Result.Error(new IllegalArgumentException("Invalid user id format"));
        }

        final String path = String.format(URLPaths.USER_BY_ID, userId);
        return retrieveByPath(path);
    }

    /**
     * Fetch user details from the backend server.
     *
     * @param userName The user name.
     * @return The user, or an error object it the user could not be fetched.
     */
    @SuppressWarnings("unchecked")
    public Result<User> getByUserName(@NonNull String userName) {
        if (!isUserNameValid(userName)) {
            return new Result.Error(new IllegalArgumentException("Invalid username"));
        }

        final String path = String.format(URLPaths.USER_BY_USER_NAME, userName);
        return retrieveByPath(path);
    }

    /**
     * Download and parse the user's profile picture, if present.
     *
     * @param userId The user id.
     * @return The user's profile picture.
     */
    @SuppressWarnings("unchecked")
    public Result<Bitmap> getPP(@NonNull String userId) {
        if (!isIdValid(userId)) {
            return new Result.Error(new IllegalArgumentException("Invalid user id format"));
        }

        Bitmap bm;

        try {
            bm = getNetworkUtil().getBitmap(String.format(URLPaths.CDN_PP_DEFAULT, userId));
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
    @SuppressWarnings("unchecked")
    private Result<User> retrieveByPath(String path) {
        try {
            final JSONObject userData = getNetworkUtil().get(path);
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
