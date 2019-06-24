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

import androidx.annotation.NonNull;

import java.util.HashMap;

import club.sandtler.devid.data.model.User;

/**
 * Class that requests user information from the backend server over the
 * {@link UserDataSource} and maintains an in-memory cache of them
 * to save some bandwidth (and prevent a DDoS against my server).
 */
public class UserRepository {

    /** The instance. */
    private static volatile UserRepository instance;

    /** The data source for retrieving information from the backend server. */
    private UserDataSource mDataSource;
    /** The user cache, indexed by user id. */
    private HashMap<String, User> mCacheById;
    /** The user cache, indexed by user name. */
    private HashMap<String, User> mCacheByUserName;

    /**
     * Private constructor to ensure there is only one instance of this class.
     * (because we want only one instance of our HashMaps)
     */
    private UserRepository(UserDataSource dataSource) {
        this.mDataSource = dataSource;
        this.mCacheById = new HashMap<>();
        this.mCacheByUserName = new HashMap<>();
    }

    /**
     * Return the instance of UserRepository.
     *
     * @param dataSource A new instance of {@link UserDataSource}.
     * @return The instance.
     */
    @NonNull
    public static UserRepository getInstance(UserDataSource dataSource) {
        if (instance == null) {
            instance = new UserRepository(dataSource);
        }

        return instance;
    }

    /**
     * Retrieve user information by the user id.
     *
     * @param id The user id.
     * @return The result.
     */
    @NonNull
    public Result<User> getById(String id) {
        if (mCacheById.containsKey(id)) {
            return new Result.Success<>(mCacheById.get(id));
        }

        Result<User> result = mDataSource.getById(id);
        if (result instanceof Result.Success) {
            User user = ((Result.Success<User>) result).getData();
            putToCache(user);
        }

        return result;
    }

    /**
     * Retrieve user information by the user name.
     *
     * @param userName The user name.
     * @return The result.
     */
    @NonNull
    public Result<User> getByUserName(String userName) {
        if (mCacheByUserName.containsKey(userName)) {
            return new Result.Success<>(mCacheByUserName.get(userName));
        }

        Result<User> result = mDataSource.getByUserName(userName);
        if (result instanceof Result.Success) {
            User user = ((Result.Success<User>) result).getData();
            putToCache(user);
        }

        return result;
    }

    /**
     * Store a user in the in-memory cache.
     *
     * @param user The user.
     */
    private void putToCache(User user) {
        mCacheById.put(user.getId(), user);
        mCacheByUserName.put(user.getUserName(), user);
    }
}