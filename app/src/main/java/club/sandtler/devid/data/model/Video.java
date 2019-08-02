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
@SuppressWarnings("WeakerAccess")
public class Video {

    /** VideoEntity key for like. */
    public static final byte RATING_LIKE = 1;
    /** VideoEntity key for dislike. */
    public static final byte RATING_DISLIKE = -1;
    /** Key for no own rating. */
    public static final byte RATING_NEUTRAL = 0;

    /** JSON object key for the video id. */
    public static final String KEY_VIDEO_ID = "_id";
    /** JSON object key for the user id. */
    public static final String KEY_USER_ID = "user_id";
    /** JSON object key for the video title. */
    public static final String KEY_TITLE = "title";
    /** JSON object key for the video description. */
    public static final String KEY_DESCRIPTION = "description";
    /** JSON object key for the video upload date. */
    public static final String KEY_UPLOAD_DATE = "time";
    /** JSON object key for the video duration in milliseconds. */
    public static final String KEY_DURATION = "duration";
    /** JSON object key for the object containing the video rating info. */
    public static final String KEY_RATING_OBJ = "rating";
    /** JSON obejct key for the amount of likes. */
    public static final String KEY_RATING_LIKES = "likes";
    /** JSON object key for the amount of dislikes. */
    public static final String KEY_RATING_DISLIKES = "dislikes";
    /** JSON object key for the own video rating. */
    public static final String KEY_OWN_RATING = "own";

    /** The video id. */
    private final String mId;
    /** The user id who uploaded the video. */
    private final String mUserId;
    /** The video title. */
    private String mTitle;
    /** The video description. */
    private String mDescription;
    /** The video upload date. */
    private Date mUploadDate;
    /** The video duration in seconds. */
    private long mDuration;
    /** The amount of likes this video has so far. */
    private long mLikes;
    /** The amount of dislikes this video has so far. */
    private long mDislikes;
    /** The currently logged in user's own video rating. */
    private byte mOwnRating;

    /**
     * Create a new Video.
     *
     * @param id The video id.
     * @param userId The user id who uploaded the video.
     * @param title The video title.
     * @param description The video description.
     * @param uploadDate The upload date.
     * @param duration The duration in seconds.
     * @param likes The amount of likes this video got so far.
     * @param dislikes The amount of dislikes this video got so far.
     * @param ownRating The user's own rating.
     */
    public Video(final String id, final String userId, String title, String description,
                 Date uploadDate, long duration, long likes, long dislikes, byte ownRating) {
        mId = id;
        mUserId = userId;
        mTitle = title;
        mDescription = description;
        mUploadDate = uploadDate;
        mDuration = duration;
        mLikes = likes;
        mDislikes = dislikes;
        mOwnRating = ownRating;
    }

    /**
     * Create a new Video.
     *
     * @param id The video id.
     * @param userId The user id who uploaded the video.
     * @param title The video title.
     * @param description The video description.
     * @param uploadDate The upload date.
     * @param duration The duration in seconds.
     * @param likes The amount of likes this video got so far.
     * @param dislikes The amount of dislikes this video got so far.
     */
    public Video(final String id, final String userId, String title, String description,
                 Date uploadDate, long duration, long likes, long dislikes) {
        this(
                id, userId, title, description, uploadDate, duration,
                likes, dislikes, Video.RATING_NEUTRAL
        );
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
        String videoId = json.getString(KEY_VIDEO_ID);
        String userId = json.getString(KEY_USER_ID);
        String title = json.getString(KEY_TITLE);
        String description = json.getString(KEY_DESCRIPTION);
        Date uploadedDate = new Date(json.getLong(KEY_UPLOAD_DATE));
        long duration = json.getLong(KEY_DURATION);

        long likes = 0, dislikes = 0;
        byte ownVote = RATING_NEUTRAL;

        if (json.has(KEY_RATING_OBJ)) {
            JSONObject rating = json.getJSONObject(KEY_RATING_OBJ);
            likes = rating.getLong(KEY_RATING_LIKES);
            dislikes = rating.getLong(KEY_RATING_DISLIKES);

            if (rating.has(KEY_OWN_RATING)) {
                ownVote = (byte) rating.getInt(KEY_OWN_RATING);
            }
        }

        return new Video(
                videoId,
                userId,
                title,
                description,
                uploadedDate,
                duration,
                likes,
                dislikes,
                ownVote
        );
    }

    /**
     * Return the video' unique id.
     *
     * @return The video id.
     */
    public String getId() {
        return mId;
    }

    /**
     * Return the user id who uploaded this video.
     *
     * @return The user id.
     */
    public String getUserId() {
        return mUserId;
    }

    /**
     * Return the video title.
     *
     * @return The title.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Return the video description.
     *
     * @return The description.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Return the video upload date.
     *
     * @return The upload date.
     */
    public Date getUploadDate() {
        return mUploadDate;
    }

    /**
     * Return the video duration in seconds.
     *
     * @return The video duration, in seconds.
     */
    public long getDuration() {
        return mDuration;
    }

    /**
     * Return the amount of likes this video has so far.
     *
     * @return The amount of likes.
     */
    public long getLikes() {
        return mLikes;
    }

    /**
     * Return the amount of dislikes this video has so far.
     *
     * @return The amount of dislikes.
     */
    public long getDislikes() {
        return mDislikes;
    }

    /**
     * Return the total amount of ratings (likes + dislikes) this video has so far.
     *
     * @return The total amount of video ratings.
     */
    public long getRatings() {
        return mLikes + mDislikes;
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
        return mOwnRating;
    }

}
