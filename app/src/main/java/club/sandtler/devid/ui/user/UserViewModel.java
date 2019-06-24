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

package club.sandtler.devid.ui.user;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import club.sandtler.devid.data.Result;
import club.sandtler.devid.data.UserRepository;
import club.sandtler.devid.data.model.User;
import club.sandtler.devid.ui.UserViewActivity;

/**
 * View model for users.
 */
public class UserViewModel extends ViewModel {

    /** The data repository from which to pull the data from. */
    private UserRepository mRepository;

    /** The user data that is to be exposed to the activity. */
    private MutableLiveData<Result<User>> mUser;
    /** The user's profile picture, decoded as a bitmap. */
    private MutableLiveData<Result<Bitmap>> mPP;

    /**
     * Create a new View model.
     *
     * @param repository The repository from which to pull the data from.
     */
    UserViewModel(UserRepository repository) {
        this.mRepository = repository;
    }

    /**
     * Retrieve a user by their user name and return LiveData
     * that is updated as soon as the data is ready.
     *
     * @param userName The user name.
     * @return The LiveData.
     */
    public LiveData<Result<User>> getByUserName(String userName) {
        if (this.mUser == null) {
            this.mUser = new MutableLiveData<>();
            new UserLoadTask().execute(UserViewActivity.EXTRA_USER_NAME, userName);
        }

        return this.mUser;
    }

    /**
     * Retrieve a user by their id and return LiveData
     * that is updated as soon as the data is ready.
     *
     * @param id The user id.
     * @return The LiveData.
     */
    public LiveData<Result<User>> getById(String id) {
        if (this.mUser == null) {
            this.mUser = new MutableLiveData<>();
            new UserLoadTask().execute(UserViewActivity.EXTRA_USER_ID, id);
        }

        return this.mUser;
    }

    public LiveData<Result<Bitmap>> getPP(String userId) {
        if (this.mPP == null) {
            this.mPP = new MutableLiveData<>();
            new PPLoadTask().execute(userId);
        }

        return this.mPP;
    }

    /**
     * Asynchronous task class for retrieving the
     * requested user data from the repository.
     *
     * The task takes two String arguments: The first is either
     * {@link UserViewActivity#EXTRA_USER_ID} or
     * {@link UserViewActivity#EXTRA_USER_NAME},
     * depending on whether
     * {@link UserViewModel#getById} or
     * {@link UserViewModel#getByUserName}
     * was called.
     * The second one is either the user id or the user name.
     */
    private class UserLoadTask extends AsyncTask<String, Void, Result<User>> {

        @Override
        protected Result<User> doInBackground(String... args) {
            if (args.length < 2) {
                return new Result.Error(new IllegalArgumentException());
            }

            switch (args[0]) {
                case UserViewActivity.EXTRA_USER_ID:
                    return mRepository.getById(args[1]);
                case UserViewActivity.EXTRA_USER_NAME:
                    return mRepository.getByUserName(args[1]);
            }

            return new Result.Error(new IllegalArgumentException());
        }

        @Override
        protected void onPostExecute(Result<User> result) {
            mUser.setValue(result);
        }

    }

    private class PPLoadTask extends AsyncTask<String, Void, Result<Bitmap>> {

        @Override
        protected Result<Bitmap> doInBackground(String... args) {
            if (args.length < 1) {
                return new Result.Error(new IllegalArgumentException());
            }

            return mRepository.getPP(args[0]);
        }

        @Override
        protected void onPostExecute(Result<Bitmap> result) {
            mPP.setValue(result);
        }

    }

}
