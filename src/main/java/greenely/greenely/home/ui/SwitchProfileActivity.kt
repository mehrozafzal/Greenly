package greenely.greenely.home.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import greenely.greenely.R
import greenely.greenely.databinding.ActivitySwitchProfileBinding
import greenely.greenely.login.ui.LoginActivity
import greenely.greenely.registration.ui.RegistrationActivity

class SwitchProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivitySwitchProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_switch_profile)
        bindViews()
    }

    private fun bindViews() {
        binding.switchProfileCreateNewAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
        binding.switchProfileAddAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.switchProfileClose.setOnClickListener {
            finish()
        }
    }
}
