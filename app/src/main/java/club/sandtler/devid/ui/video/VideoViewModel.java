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

package club.sandtler.devid.ui.video;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import club.sandtler.devid.data.Result;
import club.sandtler.devid.data.VideoRepository;
import club.sandtler.devid.data.model.Video;

/**
 * View model for videos.
 */
public class VideoViewModel extends ViewModel {

    /** The repository to pull data from. */
    private VideoRepository mRepository;
    /** The video view data exposed to the UI layer. */
    private MutableLiveData<Result<Video>> mVideo;

    /**
     * Create a new view model.
     *
     * To be called from {@link VideoViewModelFactory} only.
     * @param repository The repository to pull data from.
     */
    VideoViewModel(VideoRepository repository) {
        mRepository = repository;
    }

    /**
     * Return video details to be displayed on the UI.
     * @param videoId The video id.
     * @return The video data.
     */
    public LiveData<Result<Video>> getVideo(String videoId) {
        if (mVideo == null) {
            mVideo = new MutableLiveData<>();
            new VideoRetrieveTask().execute(videoId);
        }

        return mVideo;
    }

    /**
     * Async task to fetch video data from the repository.
     */
    private class VideoRetrieveTask extends AsyncTask<String, Void, Result<Video>> {

        @Override
        public Result<Video> doInBackground(String... args) {
            return mRepository.getById(args[0]);
        }

        @Override
        public void onPostExecute(Result<Video> result) {
            mVideo.setValue(result);
        }

    }

}
