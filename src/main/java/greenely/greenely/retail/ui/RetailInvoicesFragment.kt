package greenely.greenely.retail.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailInvoicesBinding
import greenely.greenely.retail.models.Invoice
import greenely.greenely.retail.ui.events.EventHandler
import greenely.greenely.retail.ui.events.RetailInvoiceEventHandlerFactory
import greenely.greenely.retail.ui.viewholders.RetailInvoicesViewHolder
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.pdfview.PdfRendererView
import vn.tiki.noadapter2.OnlyAdapter
import javax.inject.Inject

class RetailInvoicesFragment : androidx.fragment.app.Fragment(), RetailInvoicesBackPressedListener {

    internal lateinit var binding: RetailInvoicesBinding

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RetailViewModel

    private lateinit var invoicesAdapter: OnlyAdapter

    @Inject
    internal lateinit var eventHandlerFactory: RetailInvoiceEventHandlerFactory

    private lateinit var eventHandler: EventHandler

    @Inject
    lateinit var tracker: Tracker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = RetailInvoicesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpRecyclerView()

        trackInvoicesScreen()

        viewModel = ViewModelProviders.of(this, viewModelFactory)[RetailViewModel::class.java]

        viewModel.retailInvoices.observe(this, Observer {
            it?.let {
                setUpInvoices(it)
            }
        })

        eventHandler = eventHandlerFactory.createEventHandler(binding)
        viewModel.events.observe(this, Observer { it?.let { eventHandler.handleEvent(it) } })

        binding.arrowBack.setOnClickListener {
            loadRetailFragment()
        }
    }

    private fun loadRetailFragment() {
        binding.appbar.visibility = View.GONE
        binding.toolbar.visibility = View.GONE
        binding.scroll.visibility = View.GONE
        binding.noInvoiceText.visibility = View.GONE

        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.add(R.id.content, RetailFragment())
                ?.setCustomAnimations(R.anim.slide_down_animation, R.anim.slide_up_animation)
                ?.commitNow()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun setUpRecyclerView() {
        setUpAdapter()
        binding.invoiceRecyclerView.adapter = invoicesAdapter
        binding.invoiceRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        binding.invoiceRecyclerView.isNestedScrollingEnabled = true
    }

    private fun setUpAdapter() {
        invoicesAdapter = OnlyAdapter.Builder()
                .viewHolderFactory { parent, _ -> RetailInvoicesViewHolder.create(parent) }
                .onItemClickListener { _: View, item: Any, _: Int ->
                    if (item is Invoice) {
                        activity?.let {
                            it.startActivity(Intent(context, PdfRendererView::class.java).apply { putExtra("url", item.pdfUrl) })

                            tracker.track(TrackerFactory().trackingEvent(
                                    "retail_opened_invoice",
                                    "Retail",
                                    item.month
                            ))
                        }
                    }
                }
                .build()
    }

    private fun setUpInvoices(invoices: List<Invoice>) {
        invoicesAdapter.setItems(invoices)

        if (invoices.isEmpty()) {
            binding.noInvoiceText.visibility = View.VISIBLE
        }
    }

    fun trackInvoicesScreen() {
        tracker.trackScreen("Retail Invoice")
    }

    override fun handleBackPressed() {
        loadRetailFragment()
    }
}