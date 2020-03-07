package greenely.greenely.accountsetup

import android.app.Activity
import android.content.Intent
import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.errors.alert.AnyExceptionHandler
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.setuphousehold.ui.SetupHouseholdActivity
import greenely.greenely.setuphousehold.ui.SetupHouseholdActivity.Companion.REONBOARDING_COMPLETED

interface AccountSetupNextHandler {
    fun navigateTo(route: AccountSetupNext)
}

class HouseholdHandler(
        private val activity: Activity,
        private val intentFlags: Int? = null,
        var next: AccountSetupNextHandler? = null
) : AccountSetupNextHandler {
    override fun navigateTo(route: AccountSetupNext) {
        if (route == AccountSetupNext.HOUSEHOLD) {
            activity.startActivity(Intent(activity, SetupHouseholdActivity::class.java).apply {
                intentFlags?.let { setFlags(it) }
            })
        } else {
            next?.navigateTo(route)
        }
    }
}

class PoAHandler(
        private val activity: Activity,
        var next: AccountSetupNextHandler? = null
) : AccountSetupNextHandler {
    override fun navigateTo(route: AccountSetupNext) {
        val anyErrorHandler = AnyExceptionHandler(activity)
        if (route == AccountSetupNext.POA) {
            anyErrorHandler.handleError(Throwable())
        } else {
            next?.navigateTo(route)
        }
    }
}

class NoNextStepHandler(
        private val activity: Activity,
        private val intentFlags: Int? = null,
        var next: AccountSetupNextHandler? = null
) : AccountSetupNextHandler {
    override fun navigateTo(route: AccountSetupNext) {
        if (route == AccountSetupNext.NO_NEXT_STEP) {
            activity.startActivity(Intent(activity, MainActivity::class.java).apply {
                intentFlags?.let { setFlags(it) }
            })
        } else if (route == AccountSetupNext.REONBOARDING_COMPLETED) {
            activity.finish()
        } else {
            next?.navigateTo(route)
        }
    }
}

class WithFinish(
        private val activity: Activity,
        private val accountSetupNextHandler: AccountSetupNextHandler) : AccountSetupNextHandler by accountSetupNextHandler {
    override fun navigateTo(route: AccountSetupNext) {
        accountSetupNextHandler.navigateTo(route)
        activity.finish()
    }
}

class MainAccountSetupNextHandler constructor(activity: Activity) : AccountSetupNextHandler {

    private val accountSetupNextHandler by lazy {
        val poaHandler = PoAHandler(activity)
        val householdHandler = HouseholdHandler(activity)
        val noNextStepHandler = NoNextStepHandler(activity, Intent.FLAG_ACTIVITY_CLEAR_TASK)

        poaHandler.next = householdHandler
        householdHandler.next = noNextStepHandler

        WithFinish(activity, poaHandler)
    }

    override fun navigateTo(route: AccountSetupNext) {
        accountSetupNextHandler.navigateTo(route)
    }
}
