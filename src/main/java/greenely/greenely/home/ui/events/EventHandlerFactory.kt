package greenely.greenely.home.ui.events

import greenely.greenely.databinding.HomeFragmentBinding
import greenely.greenely.home.ui.HomeFragment
import greenely.greenely.main.ui.MainActivity
import javax.inject.Inject

class EventHandlerFactory @Inject constructor(
        private val fragment: HomeFragment,
        private val activity: MainActivity
) {

    fun createEventHandler(binding: HomeFragmentBinding): EventHandler {
        val showLoader = LoadingHandler(binding.loader, binding.scroll)
        val startActivity = StartSignatureNavigationHandler(fragment, activity)

        showLoader.next = startActivity
        return showLoader
    }
}

