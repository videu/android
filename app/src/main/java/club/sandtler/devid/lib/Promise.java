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

package club.sandtler.devid.lib;

import android.os.AsyncTask;
import android.os.OperationCanceledException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A JavaScript-style implementation of asynchronous operations.
 *
 * As their name suggests, Promises are a <i>promise</i> that requested data are
 * available in the future.  This promise can either be <i>resolved</i>, meaning
 * the data could successfully be obtained, or <i>rejected</i> in case an
 * Exception was raised during the process.
 * <p>
 * The entity requesting that data is returned an instance of this class.
 * It is this entity's responsibility to invoke the {@link Promise#then} method
 * and, on the instance of {@link IncompletePromiseState} returned by this call,
 * either {@link IncompletePromiseState#expect} or
 * {@link IncompletePromiseState#execute}.  The latter call will provide a
 * generic implementation of {@link RejectCallback} automatically and log errors
 * to the console.  This is discouraged on release builds, however.
 * <h2>Example</h2>
 * Suppose you have an Object {@code foo} that has a method
 * {@code getSomething()} that returns a Promise.  The full implementation with
 * both resolve and reject handler would look something like this:
 * <code>
 * foo.getSomething()                       // Returns a Promise
 *     .then(result -> handleResult(result) // Set the resolve handler
 *     .expect(error -> showError(error)    // Set the reject handler
 *     .execute();                          // Actually execute the Promise
 * </code>
 * Where the {@code expect()} line could actually be left out if you are on a
 * rush.  Both callbacks are executed on the same thread as the one where
 * {@code getSomething()} was called from.
 * <p>
 * This API is not used at all BTW, I wrote it because I had nothing else to do.
 *
 * @param <T> The type of the promised data.
 */
@SuppressWarnings("WeakerAccess")
public final class Promise<T> {

    /** The Promise executor. */
    @NonNull
    private final TypedRunnable<T> mExecutor;

    /** Whether the {@link #then} method has been called already. */
    private boolean mIsThenCalled = false;

    /**
     * Whether to run the Promise executor in an {@link AsyncTask}
     * or on the same thread.  Default is {@code true}.
     */
    private boolean mRunAsync;

    /**
     * Create a new Promise.
     * The {@code executor} will be run in a separate thread that is a child of
     * the thread calling the {@link IncompletePromiseState#execute()} method.
     * Promises must always be instantiated by the entity of which data is
     * requested from, not the requesting one.
     *
     * @param executor What to run in order to get the requested information.
     */
    public Promise(@NonNull TypedRunnable<T> executor) {
        this(executor, true);
    }

    /**
     * Create a new Promise.
     * The {@code executor} will be run in a separate thread that is a child of
     * the thread calling the {@link IncompletePromiseState#execute()} method.
     * Promises must always be instantiated by the entity of which data is
     * requested from, not the requesting one.
     *
     * @param executor What to run in order to get the requested information.
     * @param runAsync If {@code true}, the Promise executor will be run in a
     *                 separate {@link AsyncTask} (the default behavior).
     */
    public Promise(@NonNull TypedRunnable<T> executor, boolean runAsync) {
        mExecutor = executor;
        mRunAsync = runAsync;
    }

    /**
     * Set the resolve handler.
     * This changes the Promise to an incomplete state which allows to either
     * register a reject callback or execute it directly with this API's default
     * reject handler implementation.
     *
     * @param resolveCallback The callback to execute when (or if) the promise
     *                        has been resolved.
     * @return The incomplete Promise waiting for execution.
     */
    public IncompletePromiseState<T> then(@NonNull ResolveCallback<T> resolveCallback) {
        if (mIsThenCalled) {
            throw new IllegalStateException("Promises may only have one resolve callback");
        }
        mIsThenCalled = true;

        return new IncompletePromiseState<>(mExecutor, resolveCallback, mRunAsync);
    }

    /**
     * Represents a Promise that has a resolve callback, but has not yet been
     * executed.  The entity requesting
     * @param <T>
     */
    public static final class IncompletePromiseState<T> {

        /** The promise executor. */
        private final TypedRunnable<T> mExecutor;
        /** The resolve callback. */
        private final ResolveCallback<T> mResolveCallback;
        /** The reject callback. */
        private RejectCallback mRejectCallback = null;

        /** Whether the Promise executor has been run already. */
        private boolean mIsExecuted = false;
        /** Whether to run the Promise executor in a separate thread. */
        private final boolean mRunAsync;

        /**
         * Create a new incomplete Promise state.
         *
         * @param executor The Promise executor.
         * @param resolveCallback The resolve callback.
         */
        private IncompletePromiseState(TypedRunnable<T> executor,
                                       ResolveCallback<T> resolveCallback,
                                       boolean runAsync) {
            mExecutor = executor;
            mResolveCallback = resolveCallback;
            mRunAsync = runAsync;
        }

        /**
         * Start a new {@link AsyncTask} and run the Promise executor there.
         * If an uncaught exception is encountered during this call, the Promise
         * turns into the rejected state.
         * This method must be called after calling {@link #expect}
         * If the {@link #expect} method has not been called before this
         */
        public void execute() {
            if (mIsExecuted) {
                throw new IllegalStateException("The Promise has already been executed");
            }
            mIsExecuted = true;

            if (mRejectCallback == null) {
                mRejectCallback = error -> Log.e(
                        Constants.LOG_TAG,
                        "Uncaught (in Promise) " + error.getClass().getSimpleName(),
                        error
                );
            }

            if (mRunAsync) {
                new PromiseExecutorTask<>(mExecutor, mResolveCallback, mRejectCallback).execute();
            } else {
                try {
                    mResolveCallback.onResolve(mExecutor.run());
                } catch (Exception e) {
                    mRejectCallback.onReject(e);
                }
            }
        }

        /**
         * Start a new {@link AsyncTask} and run the Promise executor there.
         * If an uncaught exception is encountered during this call, the Promise
         * turns into the rejected state.
         *
         * @param rejectCallback The callback to execute if the Promise is
         *                       rejected.
         * @return The instance itself for chaining {@link #execute}
         *         conveniently.
         */
        public IncompletePromiseState<T> expect(RejectCallback rejectCallback) {
            if (mIsExecuted) {
                throw new IllegalStateException(
                        "The reject callback must be defined before executing the promise"
                );
            }

            mRejectCallback = rejectCallback;
            return this;
        }

    }

    /**
     * An entity that is meant to execute something in order to acquire the
     * promised data.
     *
     * @param <T> The Promise data type.
     */
    public interface TypedRunnable<T> {
        /**
         * Do everything required to obtain the promised data.
         * If this call throws an Exception, the promise gets rejected and
         * said Exception is passed on to the reject callback.
         *
         * @return The requested data.
         */
        T run() throws Exception;
    }

    /**
     * A callback for promise results.
     *
     * @param <T> The type of the result data.
     */
    public interface ResolveCallback<T> {
        /**
         * A callback that is executed when the promise has been resolved.
         *
         * @param result The result data.
         */
        void onResolve(@Nullable T result);
    }

    /**
     * A callback for rejected promises.
     */
    public interface RejectCallback {
        /**
         * Executed in case the Promise has been rejected.
         *
         * @param error The error that caused the Promise to be rejected.
         */
        void onReject(@NonNull Exception error);
    }

    /**
     * Async Task for running the promise executor.
     *
     * @param <T> The return data type.
     *            Must be the same as in the {@link Promise} instance.
     */
    private static class PromiseExecutorTask<T> extends AsyncTask<Void, Void, T> {

        /** The Promise executor. */
        private final TypedRunnable<T> mExecutor;
        /** The Resolve callback. */
        private final ResolveCallback<T> mResolveCallback;
        /** The Reject callback. */
        private final RejectCallback mRejectCallback;

        /**
         * The Exception that was thrown while running the Promise executor,
         * if applicable.
         */
        private Exception mError = null;

        /**
         * Create a new Promise executor task.
         *
         * @param executor The executor to get the data.
         * @param resolveCallback The callback for resolved Promise state.
         * @param rejectCallback The callback for rejected Promise state.
         */
        private PromiseExecutorTask(TypedRunnable<T> executor,
                                    ResolveCallback<T> resolveCallback,
                                    RejectCallback rejectCallback) {
            mExecutor = executor;
            mResolveCallback = resolveCallback;
            mRejectCallback = rejectCallback;
        }

        /** {@inheritDoc} */
        @Override
        protected T doInBackground(Void... params) {
            try {
                return mExecutor.run();
            } catch (Exception e) {
                mError = e;
                return null;
            }
        }

        /** {@inheritDoc} */
        @Override
        public void onPostExecute(T result) {
            if (mError == null) {
                mResolveCallback.onResolve(result);
            } else {
                mRejectCallback.onReject(mError);
            }
        }

        /** {@inheritDoc} */
        @Override
        protected void onCancelled() {
            mRejectCallback.onReject(new OperationCanceledException());
        }

        /** {@inheritDoc} */
        @Override
        protected void onCancelled(T result) {
            mResolveCallback.onResolve(result);
        }

    }

}
