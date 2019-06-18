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

package club.sandtler.devid.data;

/**
 * A generic data store for the result of an operation.
 *
 * Results may be instances of either {@link Result.Success}
 * or {@link Result.Error}, depending on the success of the operation.
 *
 * @param <T> The type of the result's data, if it was successful.
 */
public abstract class Result<T> {

    /**
     * Return a String representing this result.
     *
     * @return The String representation of this result.
     */
    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }
        return "";
    }

    /**
     * Represents a successful result.
     *
     * @param <T> The result data type.
     */
    public final static class Success<T> extends Result {

        /** The data resulting from the operation. */
        private T data;

        /**
         * Construct a new successful result.
         *
         * @param data The result data.
         */
        public Success(T data) {
            this.data = data;
        }

        /**
         * Return the result's data.
         *
         * @return The data.
         */
        public T getData() {
            return this.data;
        }

    }

    /**
     * Represents an error result.
     */
    public final static class Error extends Result {

        /** The exception that occurred while processing the result. */
        private Exception error;

        /**
         * Construct a new error result.
         *
         * @param error The exception that occurred in the attempt to process
         *              the result.
         */
        public Error(Exception error) {
            this.error = error;
        }

        /**
         * Return the exception that caused the error to occur.
         *
         * @return The exception.
         */
        public Exception getError() {
            return this.error;
        }

    }

}
