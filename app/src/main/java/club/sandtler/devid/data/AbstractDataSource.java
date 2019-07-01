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

import club.sandtler.devid.lib.NetworkUtil;

/**
 * Abstract base class for all data sources.
 *
 * Data sources are classes that request data from the backend server or CDN.
 * They are only used by the *Repository classes, which maintain a local cache
 * to reduce network and server load.
 *
 * All methods in this class are blocking, meaning
 * they must not be run on the UI thread.
 */
public abstract class AbstractDataSource {

    /** The network utility for performing HTTP requests. */
    private final NetworkUtil mNetworkUtil;

    /**
     * Create a new data source.
     */
    public AbstractDataSource() {
        mNetworkUtil = NetworkUtil.getDefault();
    }

    /**
     * Create a new authenticated data source.
     *
     * @param authToken The authentication token from the backend server.
     */
    public AbstractDataSource(String authToken) {
        mNetworkUtil = new NetworkUtil(authToken);
    }

    /**
     * Return the network utility instance to perform network operations with.
     *
     * @return The network utility.
     */
    protected final NetworkUtil getNetworkUtil() {
        return mNetworkUtil;
    }

}
