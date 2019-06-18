package club.sandtler.devid.data.model;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Data store class for any user.
 */
public class User {

    /** The unique user id. */
    protected final String id;
    /** The user (@) name. */
    private String userName;
    /** The display name. */
    private String displayName;
    /** The date this user signed up. */
    private final Date joinedDate;

    /**
     * Create a new user instance.
     *
     * @param id The unique user id.
     * @param userName The user name.
     * @param displayName The display name.
     * @param joinedDate The date this user signed up.
     */
    public User(final String id, String userName, String displayName, final Date joinedDate) {
        this.id = id;
        this.userName = userName;
        this.displayName = displayName;
        this.joinedDate = joinedDate;
    }

    /**
     * Recreate a user instance from its serialized JSON form.
     *
     * @param json The JSON data.
     * @return The user.
     * @throws JSONException If the JSON object was malformed.
     */
    public static User fromJSON(JSONObject json) throws JSONException {
        final String id = json.getString("_id");
        String userName = json.getString("userName");
        String displayName = json.getString("displayName");
        Date joinedDate = new Date(json.getLong("joinedDate"));

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
            json.put("_id", this.getId());
            json.put("userName", this.getUserName());
            json.put("displayName", this.getDisplayName());
            json.put("joinedDate", this.getJoinedDate().getTime());
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
        return this.id;
    }

    /**
     * Return the user name.
     *
     * @return The user name.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Return the display name.
     *
     * @return The display name.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Return the date this user signed up.
     *
     * @return The sign up date.
     */
    public Date getJoinedDate() {
        return this.joinedDate;
    }

}
