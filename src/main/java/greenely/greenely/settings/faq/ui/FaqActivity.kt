package greenely.greenely.settings.faq.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.databinding.FaqActivityBinding
import greenely.greenely.settings.faq.ui.events.FaqEventHandler
import vn.tiki.noadapter2.OnlyAdapter
import vn.tiki.noadapter2.databinding.BindingBuilder
import javax.inject.Inject

@OpenClassOnDebug
class FaqActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var eventHandler: FaqEventHandler

    private lateinit var viewModel: FaqViewModel
    private lateinit var adapter: OnlyAdapter

    @VisibleForTesting
    lateinit var binding: FaqActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        createAdapter()
        setUpViewModel()
        setUpBinding()

        viewModel.fetch()
    }

    private fun createAdapter() {
        adapter = BindingBuilder().layoutFactory { R.layout.faq_card }.build()
    }

    private fun setUpBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.faq_activity)
        binding.viewModel = viewModel
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)[FaqViewModel::class.java]
        viewModel.items.observe(this, Observer { it?.let { adapter.setItems(it) } })
        viewModel.events.observe(this, Observer { it?.let { eventHandler.handleEvent(it) } })
    }
}

