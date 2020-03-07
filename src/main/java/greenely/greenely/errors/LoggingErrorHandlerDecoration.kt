package greenely.greenely.errors

import android.util.Log

class LoggingErrorHandlerDecoration(errorHandler: ErrorHandler) : ErrorHandler by errorHandler {
    override fun handleError(error: Throwable) {
        Log.e("Error", "Error", error)
    }
}

