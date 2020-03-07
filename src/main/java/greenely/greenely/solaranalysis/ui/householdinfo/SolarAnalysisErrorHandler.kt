package greenely.greenely.solaranalysis.ui.householdinfo

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.alert.AnyExceptionHandler
import greenely.greenely.errors.alert.HttpExceptionHandler
import greenely.greenely.errors.alert.IOExceptionHandler
import greenely.greenely.errors.alert.VerificationExceptionHandler
import javax.inject.Inject

@OpenClassOnDebug
class SolarAnalysisErrorHandler @Inject constructor(
        private val activity: SolarAnalysisActivity
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {
            val httpExceptionHandler = HttpExceptionHandler(activity)
            val ioExceptionHandler = IOExceptionHandler(activity)
            val verificationExceptionHandler = VerificationExceptionHandler(activity)
            val anyExceptionHandler = AnyExceptionHandler(activity)

            httpExceptionHandler.next = ioExceptionHandler
            ioExceptionHandler.next = verificationExceptionHandler
            verificationExceptionHandler.next = anyExceptionHandler

            return httpExceptionHandler
        }
}

