package greenely.greenely.main.router

import com.google.android.material.bottomnavigation.BottomNavigationView
import greenely.greenely.R

class RouterWithBottomNavigation(private val router: Router, private val bottomNavigationView: BottomNavigationView) : Router by router {
    private var shouldRoute: Boolean = true

    init {
        setUpPageChangeListener()
    }

    override fun routeTo(route: Route) {
        router.routeTo(route)

        shouldRoute = false
        bottomNavigationView.selectedItemId = route.menuId()
    }

    private fun setUpPageChangeListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            if (shouldRoute) routeTo(routeFromMenuItemId(it.itemId))
            else shouldRoute = true
            true
        }
    }

    private fun Route.menuId() = when (this) {
        Route.HOME -> R.id.home
        // Route.GUIDANCE -> R.id.diary
        Route.HISTORY -> R.id.history
        Route.FEED -> R.id.messages
        Route.RETAIL -> R.id.retail
        Route.COMPETE_FRIEND -> R.id.compete
    }

    private fun routeFromMenuItemId(menuId: Int): Route = when (menuId) {
        R.id.home -> Route.HOME
        //   R.id.diary -> Route.GUIDANCE
        R.id.history -> Route.HISTORY
        R.id.retail -> Route.RETAIL
        R.id.messages -> Route.FEED
        R.id.compete -> Route.COMPETE_FRIEND
        else -> throw IllegalArgumentException("Could not find route for $menuId")
    }
}

