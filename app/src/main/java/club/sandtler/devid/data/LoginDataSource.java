package club.sandtler.devid.data;

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

import club.sandtler.devid.data.model.LoggedInUser;
import club.sandtler.devid.lib.NetworkUtil;

/**
 * Class that handles authentication w/ login credentials
 * and retrieves user information.
 */
public class LoginDataSource {

    /** The HTTP URL string to send the login request to. */
    private static final String LOGIN_PATH = "/user/login";

    /** The network utility instance for performing the POST request. */
    private final NetworkUtil mNetworkUtil;

    /**
     * Create a new login data source.
     */
    public LoginDataSource() {
        // The default instance does not carry authentication headers,
        // which is what we want here (we don't have an auth token yet).
        this.mNetworkUtil = NetworkUtil.getDefault();
    }

    /**
     * Attempt to login with the given credentials.
     * Must not be called from the UI thread.
     *
     * @param userName The user name.
     * @param password The password.
     * @return The logged in user, or an error object it the login failed.
     */
    public Result<LoggedInUser> loginSync(String userName, String password) {
        try {
            final JSONObject request = credsToJSON(userName, password);
            final JSONObject response = this.mNetworkUtil.post(LOGIN_PATH, request);
            if (response.has("err"))
                return new Result.Error(new Exception(response.getString("err")));

            return new Result.Success<>(LoggedInUser.fromJSON(response));
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    void logout() {
        // TODO: revoke authentication
    }

    /**
     * Convert the username / password credentials to a JSON object.
     *
     * @param userName The user name.
     * @param password The password.
     * @return The username / password combo as JSON.
     * @throws JSONException Can never be thrown.
     */
    private JSONObject credsToJSON(String userName, String password) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("userName", userName);
        data.put("password", password);
        return data;
    }

}
