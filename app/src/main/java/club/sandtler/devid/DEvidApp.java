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

package club.sandtler.devid;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

/**
 * The main application class.
 * This is only used for making the application context globally accessible.
 */
public class DEvidApp extends Application {

    /** The application context. */
    /* This is ok because it gets null-referenced upon app termination. */
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        DEvidApp.sContext = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DEvidApp.sContext = null;
    }

    /**
     * Return the application context.
     *
     * @return The application context.
     */
    @Nullable
    public static Context getAppContext() {
        return sContext;
    }

}
