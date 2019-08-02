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

import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;

import club.sandtler.devid.data.model.LoggedInUser;
import club.sandtler.devid.lib.Constants;

/**
 * Class that handles authentication w/ login credentials
 * and retrieves user information.
 */
public class LoginDataSource extends AbstractDataSource {

    /**
     * Attempt to login with the given credentials.
     * Must not be called from the UI thread.
     *
     * @param userName The user name.
     * @param password The password.
     * @return The logged in user, or an error object it the login failed.
     */
    @SuppressWarnings("unchecked")
    public Result<LoggedInUser> login(String userName, String password) {
        try {
            final JSONObject request = credsToJSON(userName, password);
            final JSONObject response = getNetworkUtil()
                    .post(Constants.URLPaths.USER_LOGIN, request);

            if (response.has("err")) {
                return new Result.Error(new LoginException(response.getString("err")));
            }

            return new Result.Success<>(LoggedInUser.fromJSON(response));
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    void logout() {
        // TODO: revoke authentication

        // This may actually not be necessary at all since
        // the auth token just needs to be deleted.
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
