package greenely.greenely.signature.ui.errors

import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.alert.CustomVerificationExceptionHandler
import greenely.greenely.errors.alert.HttpExceptionHandler
import greenely.greenely.errors.alert.IOExceptionHandler
import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity
import greenely.greenely.signature.ui.SignatureActivity
import javax.inject.Inject


class PoaErrorHandler @Inject constructor(
        val activity: SignatureActivity
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {

            val httpExceptionHandler = HttpExceptionHandler(activity)
            val ioExceptionHandler = IOExceptionHandler(activity)
            val verificationExceptionHandler = CustomVerificationExceptionHandler(activity)

            httpExceptionHandler.next = ioExceptionHandler
            ioExceptionHandler.next = verificationExceptionHandler

            return httpExceptionHandler
        }
}