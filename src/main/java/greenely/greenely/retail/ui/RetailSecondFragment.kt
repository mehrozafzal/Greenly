package greenely.greenely.retail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import greenely.greenely.R
import greenely.greenely.databinding.RetialStartViewSecondBinding

class RetailSecondFragment : BaseFragment() {

    private lateinit var binding: RetialStartViewSecondBinding

    /**
     * Create the view.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.retial_start_view_second, container, false)
        return binding.root
    }


}