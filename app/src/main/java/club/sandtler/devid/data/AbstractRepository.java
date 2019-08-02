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

import android.util.Log;

import club.sandtler.devid.lib.Constants;

/**
 * Abstract base class for all repositories.
 * <p>
 * Child classes should override at least one of the
 * cache cleaning methods defined here.
 */
public abstract class AbstractRepository {

    /**
     * Return the repository instance.
     *
     * @param <T> The repository type.
     * @return The repository instance.
     */
    public static <T extends AbstractRepository> T getInstance() {
        throw new UnsupportedOperationException("Unimplemented getter");
    }

    /**
     * Return the repository instance and update the authentication token.
     *
     * @param authToken The authentication token.
     * @param <T> The repository type.
     * @return The repository instance.
     */
    public static <T extends AbstractRepository> T getInstance(String authToken) {
        return getInstance();
    }

    /**
     * Clear the in-memory data cache.
     *
     * This method should only be called if the system is low on memory
     * or the user requests it explicitly.
     */
    public void clearMemCache() {
        Log.v(Constants.LOG_TAG, "clearMemCache()");
    }

    /**
     * Clear the database cache.
     *
     * This method should only be called for debug purposes.
     */
    public void clearDbCache() {
        Log.v(Constants.LOG_TAG, "clearDbCache()");
    }

    /**
     * Clear the persistent storage cache.
     *
     * This method should only be called when the user requests it explicitly;
     * the Android system will automatically clear our file cache if
     * it runs low on storage.
     */
    public void clearStorageCache() {
        Log.v(Constants.LOG_TAG, "clearStorageCache()");
    }

    /** Clear all caches. */
    public void clearAll() {
        Log.v(Constants.LOG_TAG, "clearAll()");
        clearMemCache();
        clearDbCache();
        clearStorageCache();
    }

}
