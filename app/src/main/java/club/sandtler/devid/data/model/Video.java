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

package club.sandtler.devid.data.model;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Data class that stores all meta data about a video.
 */
public class Video {

    /** Rating key for like. */
    public static final byte RATING_LIKE = 1;
    /** Rating key for dislike. */
    public static final byte RATING_DISLIKE = -1;
    /** Key for no own rating. */
    public static final byte RATING_NEUTRAL = 0;

    /** The video id. */
    private final String id;
    /** The video title. */
    private String title;
    /** The video description. */
    private String description;
    /** The video upload date. */
    private Date uploadDate;
    /** The video duration in seconds. */
    private long duration;
    /** The amount of likes this video has so far. */
    private long likes;
    /** The amount of dislikes this video has so far. */
    private long dislikes;
    /** The currently logged in user's own video rating. */
    private byte ownRating;

    /**
     * Create a new Video.
     *
     * @param id The video id.
     * @param title The video title.
     * @param description The video description.
     * @param uploadedDate The upload date.
     * @param duration The duration in seconds.
     * @param likes The amount of likes this video got so far.
     * @param dislikes The amount of dislikes this video got so far.
     */
    public Video(final String id, String title, String description, Date uploadedDate,
                 long duration, long likes, long dislikes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.uploadDate = uploadedDate;
        this.duration = duration;
        this.likes = likes;
        this.dislikes = dislikes;
        this.ownRating = Video.RATING_NEUTRAL;
    }

    /**
     * Create a new Video.
     *
     * @param id The video id.
     * @param title The video title.
     * @param description The video description.
     * @param uploadedDate The upload date.
     * @param duration The duration in seconds.
     * @param likes The amount of likes this video got so far.
     * @param dislikes The amount of dislikes this video got so far.
     * @param ownRating The user's own rating.
     */
    public Video(final String id, String title, String description, Date uploadedDate,
                 long duration, long likes, long dislikes, byte ownRating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.uploadDate = uploadedDate;
        this.duration = duration;
        this.likes = likes;
        this.dislikes = dislikes;
        this.ownRating = ownRating;
    }

    /**
     * Parse a JSON object into a video.
     *
     * @param json The JSON object, as retrieved from the server.
     * @return The parsed video.
     * @throws JSONException If an error occurred while parsing the JSON object.
     */
    @NonNull
    public static Video fromJSON(JSONObject json) throws JSONException {
        String id = json.getString("_id");
        String title = json.getString("title");
        String description = json.getString("description");
        Date uploadedDate = new Date(json.getLong("uploaded"));
        long duration = json.getLong("duration");

        JSONObject rating = json.getJSONObject("rating");
        long likes = rating.getLong("likes");
        long dislikes = rating.getLong("dislikes");

        return new Video(id, title, description, uploadedDate, duration, likes, dislikes);
    }

    /**
     * Return the video' unique id.
     *
     * @return The video id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Return the video title.
     *
     * @return The title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Return the video description.
     *
     * @return The description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Return the video upload date.
     *
     * @return The upload date.
     */
    public Date getUploadDate() {
        return this.uploadDate;
    }

    /**
     * Return the video duration in seconds.
     *
     * @return The video duration, in seconds.
     */
    public long getDuration() {
        return this.duration;
    }

    /**
     * Return the amount of likes this video has so far.
     *
     * @return The amount of likes.
     */
    public long getLikes() {
        return this.likes;
    }

    /**
     * Return the amount of dislikes this video has so far.
     *
     * @return The amount of dislikes.
     */
    public long getDislikes() {
        return this.dislikes;
    }

    /**
     * Return the total amount of ratings (likes + dislikes) this video has so far.
     *
     * @return The total amount of video ratings.
     */
    public long getRatings() {
        return this.likes + this.dislikes;
    }

    /**
     * Return the user's own video rating.
     *
     * @return The user's own rating, or {@link Video#RATING_NEUTRAL} if not logged in.
     * @see Video#RATING_LIKE
     * @see Video#RATING_DISLIKE
     * @see Video#RATING_NEUTRAL
     */
    public byte getOwnRating() {
        return this.ownRating;
    }

}
