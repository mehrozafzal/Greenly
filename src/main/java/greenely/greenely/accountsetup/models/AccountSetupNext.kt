package greenely.greenely.accountsetup.models

import androidx.annotation.Keep

@Keep
enum class AccountSetupNext {
    VERIFICATION,
    POA,
    HOUSEHOLD,
    NO_NEXT_STEP,
    REONBOARDING_COMPLETED
}