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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Data store class for any user.
 */
public class User {

    /** JSON object key for the user id. */
    public static final String KEY_ID = "_id";
    /** JSON object key for the user name. */
    public static final String KEY_USER_NAME = "userName";
    /** JSON object key for the display name. */
    public static final String KEY_DISPLAY_NAME = "displayName";
    /** JSON object key for the sign-up date. */
    public static final String KEY_JOINED_DATE = "joinedDate";

    /** The unique user id. */
    protected final String mId;
    /** The user (@) name. */
    private String mUserName;
    /** The display name. */
    private String mDisplayName;
    /** The date this user signed up. */
    private final Date mJoinedDate;

    /**
     * Create a new user instance.
     *
     * @param id The unique user id.
     * @param userName The user name.
     * @param displayName The display name.
     * @param joinedDate The date this user signed up.
     */
    public User(final String id, String userName, String displayName, final Date joinedDate) {
        mId = id;
        mUserName = userName;
        mDisplayName = displayName;
        mJoinedDate = joinedDate;
    }

    /**
     * Recreate a user instance from its serialized JSON form.
     *
     * @param json The JSON data.
     * @return The user.
     * @throws JSONException If the JSON object was malformed.
     */
    public static User fromJSON(JSONObject json) throws JSONException {
        final String id = json.getString(KEY_ID);
        String userName = json.getString(KEY_USER_NAME);
        String displayName = json.getString(KEY_DISPLAY_NAME);
        Date joinedDate = new Date(json.getLong(KEY_JOINED_DATE));

        return new User(id, userName, displayName, joinedDate);
    }

    /**
     * Return aa {@link JSONObject} representing this user instance.
     *
     * @return The JSONObject.
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put(KEY_ID, getId());
            json.put(KEY_USER_NAME, getUserName());
            json.put(KEY_DISPLAY_NAME, getDisplayName());
            json.put(KEY_JOINED_DATE, getJoinedDate().getTime());
        } catch (JSONException e) {
            return null;
        }

        return json;
    }

    /**
     * Return the unique user id.
     *
     * @return The user id.
     */
    public String getId() {
        return mId;
    }

    /**
     * Return the user name.
     *
     * @return The user name.
     */
    public String getUserName() {
        return mUserName;
    }

    /**
     * Return the display name.
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
     * Return the date this user signed up.
     *
     * @return The sign up date.
     */
    public Date getJoinedDate() {
        return mJoinedDate;
    }

}
