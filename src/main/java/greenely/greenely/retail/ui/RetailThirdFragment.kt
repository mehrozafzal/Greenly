package greenely.greenely.retail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import greenely.greenely.R
import greenely.greenely.databinding.RetailStartThirdViewBinding
import greenely.greenely.databinding.RetialStartViewSecondBinding

class RetailThirdFragment : BaseFragment() {

    private lateinit var binding: RetailStartThirdViewBinding

    /**
     * Create the view.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.retail_start_third_view, container, false)
        return binding.root
    }


}