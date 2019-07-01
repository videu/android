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

import org.json.JSONObject;

import static club.sandtler.devid.lib.Constants.URLPaths;
import club.sandtler.devid.data.model.Video;

/**
 * Class for retrieving video meta data from the backend server.
 */
public class VideoDataSource extends AbstractDataSource {

    /**
     * Fetch video meta data from the backend server.
     *
     * @param videoId The video id.
     * @return The video meta, or an error object it they could not be fetched.
     */
    public Result<Video> getById(@NonNull String videoId) {
        if (!isIdValid(videoId)) {
            return new Result.Error(new IllegalArgumentException("Invalid video id format"));
        }

        final String path = String.format(URLPaths.VIDEO_INFO_BY_ID, videoId);
        return retrieveByPath(path);
    }

    /**
     * Retrieve and parse video meta data when we already know the path.
     *
     * @param path The path to use for the GET operation.
     * @return The result of the fetch operation.
     */
    private Result<Video> retrieveByPath(String path) {
        try {
            final JSONObject videoData = getNetworkUtil().get(path);
            return new Result.Success<>(Video.fromJSON(videoData));
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    /**
     * Perform a sanity check on a video id.
     *
     * @param videoId The video id.
     * @return Whether the user id is syntactically valid.
     */
    private boolean isIdValid(@NonNull String videoId) {
        System.out.println(videoId.length());
        return videoId.matches("^[a-f0-9]{24}$");
    }

}
