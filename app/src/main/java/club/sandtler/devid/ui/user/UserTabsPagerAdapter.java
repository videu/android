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

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import club.sandtler.devid.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the tabs.
 */
public class UserTabsPagerAdapter extends FragmentPagerAdapter {

    /** Tab title string resource ids. */
    @StringRes
    private static final int[] TAB_TITLES = new int[] {
            R.string.tab_text_overview,
            R.string.tab_text_videos,
            R.string.tab_text_details,
    };

    /** The context. */
    private final Context mContext;
    /** The user specification (either by user name or id) */
    private final Bundle mUserSpec;

    /**
     * Create a new adapter.
     *
     * @param context The context.
     * @param fm The fragment manager.
     * @param userSpec A Bundle that contains either the
     *                 {@link club.sandtler.devid.ui.UserViewActivity#EXTRA_USER_ID}
     *                 or
     *                 {@link club.sandtler.devid.ui.UserViewActivity#EXTRA_USER_NAME}
     *                 key.
     */
    public UserTabsPagerAdapter(Context context, FragmentManager fm, Bundle userSpec) {
        super(fm);

        this.mContext = context;
        this.mUserSpec = userSpec;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserOverviewFragment.newInstance(this.mUserSpec);
            case 1:
                return new UserVideosFragment();
            case 2:
                return new UserDetailsFragment();
        }

        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.mContext.getResources()
                .getString(UserTabsPagerAdapter.TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 3;
    }

}
