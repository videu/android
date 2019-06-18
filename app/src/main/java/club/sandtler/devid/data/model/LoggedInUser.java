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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Data class that captures user information for
 * logged in users retrieved from LoginRepository.
 */
public class LoggedInUser extends User {

    public static final String KEY_ID = "id";
    public static final String KEY_AUTH_TOKEN = "authToken";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_DISPLAY_NAME = "displayName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_JOINED_DATE = "joinedDate";

    /** The login authentication token. */
    private String authToken;
    /** The email address. */
    private String email;

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

        this.authToken = authToken;
        this.email = email;
    }

    public LoggedInUser(Bundle bundle) {
        super(
                bundle.getString(LoggedInUser.KEY_ID),
                bundle.getString(LoggedInUser.KEY_USER_NAME),
                bundle.getString(LoggedInUser.KEY_DISPLAY_NAME),
                new Date(bundle.getLong(LoggedInUser.KEY_JOINED_DATE))
        );

        this.authToken = bundle.getString(LoggedInUser.KEY_AUTH_TOKEN);
        this.email = bundle.getString(LoggedInUser.KEY_EMAIL);
    }

    /**
     * Recreate an instance of LoggedInUser from its serialized JSON form.
     *
     * @param json The JSON data.
     * @return The recreated user.
     * @throws JSONException If the JSON object was malformed.
     */
    public static LoggedInUser fromJSON(JSONObject json) throws JSONException {
        JSONObject user = json.getJSONObject("user");

        final String id = user.getString("_id");
        String userName = user.getString("userName");
        String displayName = user.getString("displayName");
        Date joinedDate = new Date(user.getLong("joinedDate"));
        String email = user.getString("email");
        String authToken = json.getString("token");

        return new LoggedInUser(id, userName, displayName, joinedDate, email, authToken);
    }

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
    public JSONObject toJSON() {
        JSONObject user = super.toJSON();
        if (user == null)
                return null;

        JSONObject json = new JSONObject();
        try {
            user.put("email", this.getEmail());
            json.put("user", user);
            json.put("token", this.getAuthToken());
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
        return this.authToken;
    }

    /**
     * Return the user's email address.
     *
     * @return The email address.
     */
    public String getEmail() {
        return this.email;
    }

}
