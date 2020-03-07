package greenely.greenely.retailinvite.ui

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import greenely.greenely.R
import greenely.greenely.databinding.RetailInviteActivityBinding
import javax.inject.Inject

class RetaiInviteActivity: AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    internal lateinit var fragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    override fun supportFragmentInjector() = fragmentInjector

    private lateinit var binding: RetailInviteActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

       binding = DataBindingUtil.setContentView(this, R.layout.retail_invite_activity)

        var fragment = RetailInviteFragment()
        fragment.arguments = Bundle()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, fragment)
                .commit()
    }
}