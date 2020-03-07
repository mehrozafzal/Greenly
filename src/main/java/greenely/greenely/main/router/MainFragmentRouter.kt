package greenely.greenely.main.router

import greenely.greenely.R
import greenely.greenely.competefriend.ui.CompeteFriendFragment
import greenely.greenely.gamification.ui.GamificationFragment
import greenely.greenely.feed.ui.FeedFragment
import greenely.greenely.guidance.ui.GuidanceFragment
import greenely.greenely.history.HistoryFragment
import greenely.greenely.history.HistoryResolution
import greenely.greenely.home.ui.HomeFragment
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.retail.ui.RetailFragment
import javax.inject.Inject

class MainFragmentRouter @Inject constructor(activity: MainActivity) : Router {
    override var onRouteSelectedListener: RouteSelectedListener? = null

    override var currentRoute: Route
        get() = _state.currentRoute
        private set(value) {
            _state.currentRoute = value
        }

    private var _state: Router.RouterState
    override var state: Router.RouterState
        get() = _state
        set(value) {
            _state = value
            routeTo(value.currentRoute)
        }

    private val fragmentManager = activity.supportFragmentManager

    private val historyFragment = HistoryFragment.create(HistoryResolution.Year)
    private val homeFragment = HomeFragment()
    private val feedFragment = FeedFragment()
    private val guidanceFragment = GuidanceFragment()
    private val retailFragment = RetailFragment()
    private val competeFriendFragment = CompeteFriendFragment()
    private val gamificationFragment = GamificationFragment()

    init {
        _state = activity.intent?.getParcelableExtra(Router.STATE_KEY)
                ?: Router.RouterState(Route.HOME)
        routeTo(_state.currentRoute)
    }

    override fun routeTo(route: Route) {
        onRouteSelectedListener?.invoke(route)
        currentRoute = route

        val fragment: androidx.fragment.app.Fragment = when (route) {

            Route.HISTORY -> historyFragment
            Route.HOME -> homeFragment
            Route.FEED -> feedFragment
          //  Route.GUIDANCE -> guidanceFragment
            Route.RETAIL -> retailFragment
            Route.COMPETE_FRIEND -> competeFriendFragment
        }

        fragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commitAllowingStateLoss()
    }
}

