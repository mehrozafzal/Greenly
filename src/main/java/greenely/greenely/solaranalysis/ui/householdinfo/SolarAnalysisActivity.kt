package greenely.greenely.solaranalysis.ui.householdinfo

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.databinding.SolarAnalysisActivityBinding
import greenely.greenely.solaranalysis.ui.householdinfo.events.EventHandler
import greenely.greenely.solaranalysis.ui.householdinfo.events.EventHandlerFactory
import javax.inject.Inject

@OpenClassOnDebug
class SolarAnalysisActivity @Inject constructor() : AppCompatActivity(), HasSupportFragmentInjector {


    @Inject
    internal lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewModel: SolarAnalysisViewModel

    @VisibleForTesting
    internal lateinit var binding: SolarAnalysisActivityBinding

    @Inject
    internal lateinit var eventHandlerFactory: EventHandlerFactory

    @VisibleForTesting
    internal lateinit var eventHandler: EventHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, factory)[SolarAnalysisViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.solar_analysis_activity)
        binding.stepper.adapter = SolarAnalysisStepAdapter(this, supportFragmentManager)
        binding.viewModel = viewModel

        eventHandler = eventHandlerFactory.createEventHandler(binding)
        viewModel.events.observe(this, Observer {
            it?.let { eventHandler.handleEvent(it) }
        })
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingFragmentInjector
}

