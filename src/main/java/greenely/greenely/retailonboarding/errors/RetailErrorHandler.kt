package greenely.greenely.retailonboarding.errors

import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.alert.CustomVerificationExceptionHandler
import greenely.greenely.errors.alert.HttpExceptionHandler
import greenely.greenely.errors.alert.IOExceptionHandler
import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity
import javax.inject.Inject

class RetailErrorHandler @Inject constructor(
        val retailOnboardingActivity: RetailOnboardingActivity
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {

            val httpExceptionHandler = HttpExceptionHandler(retailOnboardingActivity)
            val ioExceptionHandler = IOExceptionHandler(retailOnboardingActivity)
            val verificationExceptionHandler = CustomVerificationExceptionHandler(retailOnboardingActivity)

            httpExceptionHandler.next = ioExceptionHandler
            ioExceptionHandler.next = verificationExceptionHandler

            return httpExceptionHandler
        }
}
