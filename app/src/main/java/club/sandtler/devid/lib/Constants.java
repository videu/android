package club.sandtler.devid.lib;

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

import club.sandtler.devid.BuildConfig;

/**
 * All global constants.
 */
public final class Constants {

    /** The account type for Android's AccountManager Framework. */
    public static final String ACCOUNT_TYPE = "club.sandtler.devid";

    /** The auth token type for Android's AccountManager Framework. */
    public static final String AUTH_TOKEN_TYPE = "club.sandter.devid";

    /** The root URL for all backend requests. */
    public static final String BACKEND_ROOT = BuildConfig.DEBUG
            ? "https://devid.deg.vnet"
            : "https://backend.devid.sandtler.club";

    /** The root URL for all CDN requests (videos/profile pictures etc). */
    public static final String CDN_ROOT = "https://cdn.devid.sandtler.club";

    private Constants() {
    }

}
