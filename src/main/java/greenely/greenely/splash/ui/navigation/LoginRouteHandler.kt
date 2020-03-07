package greenely.greenely.splash.ui.navigation

import android.app.Activity
import android.content.Intent
import greenely.greenely.login.ui.LoginActivity

class LoginRouteHandler(
        private val activity: Activity,
        var next: NavigationHandler? = null
) : NavigationHandler {

    override fun navigateTo(route: SplashRoute) {
        if (route == SplashRoute.LOGIN) {
            activity.startActivity(Intent(activity, LoginActivity::class.java))
        } else {
            next?.navigateTo(route)
        }
    }
}