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

package club.sandtler.devid.ui;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import club.sandtler.devid.R;
import club.sandtler.devid.ui.user.UserTabsPagerAdapter;

/**
 * Activity for displaying a user profile.
 *
 * This Activity needs either the {@link UserViewActivity#EXTRA_USER_ID} or
 * {@link UserViewActivity#EXTRA_USER_NAME} extra passed to the launching
 * intent, otherwise it will terminate immediately.
 *
 * TODO: Pass user data to the fragments
 */
public class UserViewActivity extends AppCompatActivity {

    /** Intent extra key for supplying the user id. */
    public static final String EXTRA_USER_ID =
            "club.sandtler.devid.ui.UserViewActivity.USER_ID";

    /** Intent extra key for supplying the user name. */
    public static final String EXTRA_USER_NAME =
            "club.sandtler.devid.ui.UserViewActivity.USER_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        UserTabsPagerAdapter sectionsPagerAdapter = new UserTabsPagerAdapter(
                this,
                getSupportFragmentManager(),
                getUserSpec()
        );

        ViewPager viewPager = findViewById(R.id.user_view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    private Bundle getUserSpec() {
        Intent intent = getIntent();
        Bundle bundle = new Bundle();

        if (intent.hasExtra(EXTRA_USER_ID)) {
            bundle.putString(EXTRA_USER_ID, intent.getStringExtra(EXTRA_USER_ID));
        } else if (intent.hasExtra(EXTRA_USER_NAME)) {
            bundle.putString(EXTRA_USER_NAME, intent.getStringExtra(EXTRA_USER_NAME));
        }

        return bundle;
    }

}
