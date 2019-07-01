package club.sandtler.devid.data;

import java.util.HashMap;

import club.sandtler.devid.data.model.Video;
import club.sandtler.devid.lib.Constants;

/**
 * Class that requests video meta data from the backend server over the
 * {@link VideoDataSource} and maintains an in-memory cache of them.
 */
public class VideoRepository {

    /** The instance (singleton access). */
    private static volatile VideoRepository sInstance;

    /** The data source. */
    private final VideoDataSource mDataSource;
    /** The in-memory cache. */
    private HashMap<String, Video> mCache;

    private VideoRepository(VideoDataSource dataSource) {
        this.mDataSource = dataSource;
        this.mCache = new HashMap<>();
    }

    /**
     * Return an instance of this repository.
     *
     * @param dataSource The data source to use.
     * @return An instance.
     */
    public static VideoRepository getInstance(VideoDataSource dataSource) {
        if (sInstance == null) {
            sInstance = new VideoRepository(dataSource);
        }

        return sInstance;
    }

    /**
     * Get a video by its id.
     *
     * @param id The video id.
     * @return The result.
     */
    public Result<Video> getById(String id) {
        if (mCache.containsKey(id)) {
            return new Result.Success<Video>(mCache.get(id));
        }

        Result<Video> result = mDataSource.getById(id);
        if (result instanceof Result.Success) {
            if (mCache.size() > Constants.CacheSize.MAX_VIDEO_MEM) {
                mCache.remove(mCache.keySet().iterator().next());
            }
            mCache.put(id, ((Result.Success<Video>) result).getData());
        }

        return result;
    }

}
