package greenely.greenely.splash.ui.navigation

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.splash.ui.IntroductionActivity
import javax.inject.Inject

@OpenClassOnDebug
class SplashNavigationHandler @Inject constructor(
        private val activity: IntroductionActivity
) : NavigationHandler {

    private val navigationHandler: NavigationHandler
        get() {
            val loginRouteHandler = LoginRouteHandler(activity)
            val registrationRouteHandler = RegistrationRouteHandler(activity)

            loginRouteHandler.next = registrationRouteHandler

            return loginRouteHandler
        }

    override fun navigateTo(route: SplashRoute) {
        navigationHandler.navigateTo(route)
    }
}

