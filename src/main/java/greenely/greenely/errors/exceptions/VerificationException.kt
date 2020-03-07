package greenely.greenely.errors.exceptions

import com.stepstone.stepper.VerificationError

class VerificationException(verificationError: VerificationError) : Throwable(verificationError.errorMessage)
