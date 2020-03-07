package greenely.greenely.feed.utils.errors

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.errors.*
import greenely.greenely.errors.snackbar.AnyExceptionHandler
import greenely.greenely.errors.snackbar.HttpExceptionHandler
import greenely.greenely.errors.snackbar.IOExceptionHandler
import greenely.greenely.feed.ui.FeedFragment
import javax.inject.Inject

@OpenClassOnDebug
class FeedErrorHandler @Inject constructor(
        private val fragment: FeedFragment
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {
            val httpExceptionHandler = HttpExceptionHandler(fragment)
            val ioExceptionHandler = IOExceptionHandler(fragment)
            val anyExceptionHandler = AnyExceptionHandler(fragment)

            httpExceptionHandler.next = ioExceptionHandler
            ioExceptionHandler.next = anyExceptionHandler

            return httpExceptionHandler
        }

}

