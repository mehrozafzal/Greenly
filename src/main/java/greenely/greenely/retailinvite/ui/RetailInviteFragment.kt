package greenely.greenely.retailinvite.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailInviteFragmentBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.retailinvite.models.ReferralInviteResponseModel
import greenely.greenely.retail.ui.events.EventHandler
import greenely.greenely.retailinvite.ui.events.ReferralInviteEventHandlerFactory
import greenely.greenely.retailinvite.ui.events.RetailInviteErrorHandler
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.CommonUtils
import kotlinx.android.synthetic.main.retail_invite_fragment.*
import javax.inject.Inject


class RetailInviteFragment() : androidx.fragment.app.Fragment(),HasSnackbarView {
    override val snackbarView: View
        get() = binding.scroll


    private lateinit var binding: RetailInviteFragmentBinding


    private lateinit var eventHandler: EventHandler

    @Inject
    lateinit var tracker: Tracker


    private val viewModel: RetailInviteViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[RetailInviteViewModel::class.java]
    }

    @Inject
    internal lateinit var eventHandlerFactory: ReferralInviteEventHandlerFactory

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory


    private val clipboardManager: ClipboardManager by lazy {
        context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    private var clipData: ClipData? = null

    private  var bottomNavigationView: View?=null

    @Inject
    lateinit var errorHandler: RetailInviteErrorHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.referralInviteResponseModel.value == null) viewModel.fetchRetailReferral()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bottomNavigationView = activity!!.findViewById(R.id.bottomNavigation)


        eventHandler = eventHandlerFactory.createEventHandler(binding)
        viewModel.events.observe(this, Observer { it?.let { eventHandler.handleEvent(it) } })


        viewModel.referralInviteResponseModel.observe(this, Observer {
            it?.let {
                binding.model = it
                setDataForScreen(it)
            }
        })


        binding.inviteDetails.setOnClickListener {

            trackEvent("Referral Invite Friend", "clicked_referral_QA")


            val fragment = ReferralInviteQAFragment()
            fragmentManager
                    ?.beginTransaction()
                    ?.addToBackStack(this.javaClass.simpleName)
                    ?.setCustomAnimations(
                            R.anim.enter_from_right,
                            R.anim.exit_to_left,
                            R.anim.enter_from_left,
                            R.anim.exit_to_right)
                    ?.replace(R.id.content, fragment)
                    ?.commit()
        }

        binding.btnCopy.setOnClickListener {
            clipData = ClipData.newPlainText("code", txtCode.text);
            clipboardManager.setPrimaryClip(clipData)
            trackEvent("Referral Invite Friend", "clicked_copy_referral_code")

//            Toast.makeText(context, getString(R.string.copy_code_message), Toast.LENGTH_SHORT).show();

            val toast = Toast.makeText(
                    context,
                    R.string.copy_code_message,
                    Toast.LENGTH_SHORT
            ).apply {
                setGravity(Gravity.CENTER, 0, 0)
                view?.background?.setColorFilter(resources.getColor(R.color.background_floating_material_dark), PorterDuff.Mode.SRC_IN)
                val text: TextView? = view?.findViewById(android.R.id.message)
                text?.setTextColor(resources.getColor(R.color.white))
            }

            toast.show()

        }

        binding.btnRetailInvite.setOnClickListener {

            trackEvent("Referral Invite Friend", "clicked_share_with_friend")

            viewModel.referralInviteResponseModel.value?.let {

                val shareBody = it.shareMessage
                val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, String.format(getString(R.string.referral_code_share_subject), CommonUtils.getCurrencyFormat(it.discount)))
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, "Share"))

            }

        }


        viewModel.errors.observe(this, Observer {
            it?.let {
                errorHandler.handleError(it)
                binding.scroll.visibility = View.INVISIBLE
            }
        })



        setToolBarNavigation()
    }


    fun setToolBarNavigation() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_close_green)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    fun setDataForScreen(referralInviteResponseModel: ReferralInviteResponseModel) {

        binding.title.setText(String.format(getString(R.string.retail_invite_screen_label), CommonUtils.getCurrencyFormat(referralInviteResponseModel.discount)))
        binding.inviteDescription.setText(String.format(getString(R.string.retail_invite_description), CommonUtils.getCurrencyFormat(referralInviteResponseModel.discount)))


        referralInviteResponseModel.remainingCredits?.let {
            binding.txtBalance.setText(String.format("%s kr", CommonUtils.getCurrencyFormat(referralInviteResponseModel.remainingCredits)))
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)

    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.retail_invite_fragment, container, false)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        bottomNavigationView?.visibility = View.GONE
        tracker.trackScreen("Referral Invite Friend")

    }

    private fun trackEvent(label: String, name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Referral",
                label

        ))
    }


}