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

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import club.sandtler.devid.data.UserDataSource;
import club.sandtler.devid.data.UserRepository;
import club.sandtler.devid.ui.login.LoginViewModel;

/**
 * ViewModel provider factory to instantiate UserViewModel.
 * Required given UserViewModel has a non-empty constructor.
 */
public class UserViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(UserRepository.getInstance(new UserDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
