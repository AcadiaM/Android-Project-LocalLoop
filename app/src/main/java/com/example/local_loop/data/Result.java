package com.example.local_loop.data;

import androidx.annotation.NonNull;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result<T> {
    private Result() {
    }

    @NonNull
    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success<?> success = (Result.Success<?>) this;
            return "Success[data=" + success.getData() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error<?> error = (Result.Error<?>) this;
            return "Error[exception=" + error.getError() + "]";
        }
        return "";
    }

    // Success sub-class
    public static final class Success<T> extends Result<T> {   // ✅ extends Result<T>
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Error sub-class
    public static final class Error<T> extends Result<T> {     // ✅ extends Result<T>
        private final Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
