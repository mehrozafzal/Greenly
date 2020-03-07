package greenely.greenely.competefriend.errors

import greenely.greenely.competefriend.ui.CompeteFriendFragment
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.snackbar.AnyExceptionHandler
import greenely.greenely.errors.snackbar.HttpExceptionHandler
import greenely.greenely.errors.snackbar.IOExceptionHandler
import javax.inject.Inject

class CompeteFriendErrorHandler @Inject constructor(
        private val fragment: CompeteFriendFragment
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