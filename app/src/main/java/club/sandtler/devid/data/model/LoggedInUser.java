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

package club.sandtler.devid.data.model;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Data class that captures user information for logged in users
 * retrieved from {@link club.sandtler.devid.data.LoginRepository}.
 */
@SuppressWarnings("WeakerAccess")
public final class LoggedInUser extends User {

    /** JSON object key for the authentication token. */
    public static final String KEY_AUTH_TOKEN = "token";
    /** JSON object key for the email address. */
    public static final String KEY_EMAIL = "email";
    /** JSON object key for the embedded user object. */
    public static final String KEY_USER_OBJ = "user";

    /** The login authentication token. */
    private String mAuthToken;
    /** The email address. */
    private String mEmail;

    /**
     * Create a new logged in user model.
     *
     * @param id The user id.
     * @param userName The user name.
     * @param displayName The display name.
     * @param joinedDate The date this user signed up.
     * @param email The user email.
     * @param authToken The authentication token.
     */
    public LoggedInUser(final String id, String userName, String displayName, final Date joinedDate,
                        String email, String authToken) {
        super(id, userName, displayName, joinedDate);

        mAuthToken = authToken;
        mEmail = email;
    }

    /**
     * Recreate a new LoggedInUser from a Bundle.
     *
     * @param bundle The bundle containing all data.
     */
    public LoggedInUser(Bundle bundle) {
        super(
                bundle.getString(LoggedInUser.KEY_ID),
                bundle.getString(LoggedInUser.KEY_USER_NAME),
                bundle.getString(LoggedInUser.KEY_DISPLAY_NAME),
                new Date(bundle.getLong(LoggedInUser.KEY_JOINED_DATE))
        );

        mAuthToken = bundle.getString(LoggedInUser.KEY_AUTH_TOKEN);
        mEmail = bundle.getString(LoggedInUser.KEY_EMAIL);
    }

    /**
     * Recreate an instance of LoggedInUser from its serialized JSON form.
     *
     * @param json The JSON data.
     * @return The recreated user.
     * @throws JSONException If the JSON object was malformed.
     */
    public static LoggedInUser fromJSON(JSONObject json) throws JSONException {
        JSONObject user = json.getJSONObject(KEY_USER_OBJ);

        final String id = user.getString(KEY_ID);
        String userName = user.getString(KEY_USER_NAME);
        String displayName = user.getString(KEY_DISPLAY_NAME);
        final Date joinedDate = new Date(user.getLong(KEY_JOINED_DATE));
        String email = user.getString(KEY_EMAIL);
        String authToken = json.getString(KEY_AUTH_TOKEN);

        return new LoggedInUser(id, userName, displayName, joinedDate, email, authToken);
    }

    /**
     * Return a bundle containing all data necessary
     * to reconstruct this instance.
     *
     * @return The bundled LoggedInUser.
     */
    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putString(LoggedInUser.KEY_AUTH_TOKEN, getAuthToken());
        bundle.putString(LoggedInUser.KEY_EMAIL, getEmail());
        bundle.putLong(LoggedInUser.KEY_JOINED_DATE, getJoinedDate().getTime());
        bundle.putString(LoggedInUser.KEY_USER_NAME, getUserName());
        bundle.putString(LoggedInUser.KEY_DISPLAY_NAME, getDisplayName());
        bundle.putString(LoggedInUser.KEY_ID, getId());

        return bundle;
    }

    /**
     * Return a {@link JSONObject} representing this instance.
     *
     * @return The JSONObject.
     */
    @Override
    @Nullable
    public JSONObject toJSON() {
        JSONObject user = super.toJSON();
        if (user == null) {
            return null;
        }

        JSONObject json = new JSONObject();
        try {
            user.put(KEY_EMAIL, getEmail());
            json.put(KEY_USER_OBJ, user);
            json.put(KEY_AUTH_TOKEN, getAuthToken());
        } catch (JSONException e) {
            return null;
        }

        return json;
    }

    /**
     * Return the authentication token.
     *
     * @return The authentication token.
     */
    public String getAuthToken() {
        return mAuthToken;
    }

    /**
     * Return the user's email address.
     *
     * @return The email address.
     */
    public String getEmail() {
        return mEmail;
    }

}
