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

import club.sandtler.devid.data.model.LoggedInUser;

/**
 * Class that requests authentication and mUser information from
 * the backend server and maintains an in-memory cache of
 * login status and user credentials information.
 */
public class LoginRepository {

    /** The instance (singleton access). */
    private static volatile LoginRepository sInstance;

    /** The data source. */
    private LoginDataSource mDataSource;

    /** The logged in user cache. */
    private LoggedInUser mUser = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.mDataSource = dataSource;
    }

    /**
     * Return the instance of this repository.
     *
     * @param dataSource The data source to use.
     * @return The instance.
     */
    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (LoginRepository.sInstance == null) {
            LoginRepository.sInstance = new LoginRepository(dataSource);
        }

        return LoginRepository.sInstance;
    }

    /**
     * Return whether the user is logged in at the moment.
     *
     * @return true if the user is logged in.
     */
    public boolean isLoggedIn() {
        return mUser != null;
    }

    /**
     * De-authenticate the currently logged in user.
     * This has no effect if the user is not logged in.
     */
    public void logout() {
        mUser = null;
        mDataSource.logout();
    }

    /**
     * Send a login request to the backend server and return the result.
     *
     * @param userName The user name.
     * @param password The password.
     * @return Either the result with the {@link LoggedInUser} instance,
     *         or an error containing the exception that occurred.
     */
    public Result<LoggedInUser> login(String userName, String password) {
        if (mUser != null) {
            if (mUser.getUserName().equals(userName)) {
                return new Result.Success<>(mUser);
            } else {
                logout();
            }
        }

        Result<LoggedInUser> result = mDataSource.login(userName, password);
        if (result instanceof Result.Success) {
            mUser = ((Result.Success<LoggedInUser>) result).getData();
        } else if (result instanceof Result.Error) {
            ((Result.Error) result).getError().printStackTrace();
        }

        return result;
    }

}
