package greenely.greenely.retail.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailErrorBinding
import io.intercom.android.sdk.Intercom

class FailedStateFragment : androidx.fragment.app.Fragment() {

    internal lateinit var binding: RetailErrorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = RetailErrorBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.inflateMenu(R.menu.retail_menu)
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.intercom) {
                Intercom.client().displayMessenger()
                true
            } else {
                false
            }
        }

        binding.retailFailedStateDescription1.text = Html.fromHtml(
                getString(R.string.retail_error_contact_us) +
                        " <a href=\"mailto:" + getString(R.string.greenely_email) + "?subject=" +
                        getString(R.string.retail_support_email_subject) + "\" >" +
                        getString(R.string.greenely_email) + "</a>"
        )

        binding.retailFailedStateDescription1.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}