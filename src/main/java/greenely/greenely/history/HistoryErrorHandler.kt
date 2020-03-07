package greenely.greenely.history

import greenely.greenely.errors.*
import greenely.greenely.errors.snackbar.AnyExceptionHandler
import greenely.greenely.errors.snackbar.HttpExceptionHandler
import greenely.greenely.errors.snackbar.IOExceptionHandler
import javax.inject.Inject

class HistoryErrorHandler @Inject constructor(private val fragment: HistoryFragment) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {
            val ioHandler = IOExceptionHandler(fragment)
            val httpHandler = HttpExceptionHandler(fragment)
            val anyHandler = AnyExceptionHandler(fragment)

            ioHandler.next = httpHandler
            httpHandler.next = anyHandler

            return ioHandler
        }
}

