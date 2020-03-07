package greenely.greenely.main.router

import com.google.android.material.bottomnavigation.BottomNavigationView
import greenely.greenely.main.ui.MainActivity
import javax.inject.Inject

class RouterFactory @Inject constructor(private val activity: MainActivity) {
    fun create(bottomNavigation: BottomNavigationView): Router {
        val base = MainFragmentRouter(activity)
        return RouterWithBottomNavigation(base, bottomNavigation)            
    }
}

