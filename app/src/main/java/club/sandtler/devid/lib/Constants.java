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

package club.sandtler.devid.lib;

import club.sandtler.devid.BuildConfig;

/**
 * All global constants.
 */
public final class Constants {

    /** The account type for Android's AccountManager Framework. */
    public static final String ACCOUNT_TYPE = "club.sandtler.devid";

    /** The auth token type for Android's AccountManager Framework. */
    public static final String AUTH_TOKEN_TYPE = "club.sandter.devid";

    /** The tag to user for logging. */
    public static final String LOG_TAG = "DEvid";

    /** Template full URL for constructing a video share link. */
    public static final String VIDEO_SHARE_URL_TMPL = "https://devid.sandtler.club/watch/%s";

    /**
     * Absolute URL paths for backend operations and CDN sources.
     * <p>
     * All paths in here need to be formatted with Java's
     * {@code String.format()} API.  Paths prefixed with {@code CDN_} are
     * to be prefixed with {@link URLPaths#CDN_ROOT}, all other ones with
     * {@link URLPaths#BACKEND_ROOT}.
     */
    public static final class URLPaths {

        /** The root URL for all backend requests. */
        public static final String BACKEND_ROOT = BuildConfig.DEBUG
                ? "https://devid.deg.vnet"
                : "https://backend.devid.sandtler.club";

        /** The root URL for all CDN requests (videos/profile pictures etc). */
        public static final String CDN_ROOT = "https://cdn.devid.sandtler.club";

        /** Path to retrieve user data by the user's id. */
        public static final String USER_BY_ID = "/user/byId/%s";

        /** Path to retrieve user data by the user's user name. */
        public static final String USER_BY_USER_NAME = "/user/byUserName/%s";

        /**
         * Path to retrieve an authentication token
         * by POSTing the login credentials.
         */
        public static final String USER_LOGIN = "/user/login";

        /** Path to retrieve information on a particular video. */
        public static final String VIDEO_INFO_BY_ID = "/video/info/%s";

        /** Path to POST the user's own vote value for a video. */
        public static final String VIDEO_POST_VOTE = "/video/vote";

        /**
         * CDN path to retrieve a raw video file in default quality.
         * Different qualities are to be supported in a later version.
         */
        public static final String CDN_PP_DEFAULT = "/pp/%s";

        /**
         * CDN path to retrieve a user's profile picture
         * by their id in default quality.
         * Different qualities are to be supported in a later version.
         */
        public static final String CDN_VIDEO_DEFAULT = "/video/%s";

        private URLPaths() {
        }

    }

    /** Sizes for in-memory, database and storage cache. */
    public static final class CacheSize {

        /** The maximum amount of entries in the in-memory user cache. */
        public static final long MAX_USER_MEM = 512;

        /** The maximum amount of entries in the in-memory video meta cache. */
        public static final long MAX_VIDEO_MEM = 512;

        private CacheSize() {
        }

    }

    /**
     * The root URL for all backend requests.
     *
     * @deprecated Use {@link Constants.URLPaths#BACKEND_ROOT} instead.
     */
    @Deprecated public static final String BACKEND_ROOT = URLPaths.BACKEND_ROOT;

    /**
     * The root URL for all CDN requests (videos/profile pictures etc).
     *
     * @deprecated Use {@link Constants.URLPaths#CDN_ROOT} instead.
     */
    @Deprecated public static final String CDN_ROOT = URLPaths.CDN_ROOT;

    /**
     * The URL path for retrieving a user's profile picture.
     *
     * @deprecated Use {@link Constants.URLPaths#CDN_PP_DEFAULT} instead.
     */
    @Deprecated public static final String PP_PATH = URLPaths.CDN_PP_DEFAULT;

    private Constants() {
    }

}
