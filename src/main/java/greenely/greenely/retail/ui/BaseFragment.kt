package greenely.greenely.retail.ui

import android.content.Context
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection

open abstract class BaseFragment:Fragment() {

    /**
     * Inject when attaching to [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}