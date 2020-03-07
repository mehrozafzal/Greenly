package greenely.greenely.competefriend.ui

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.View
import dagger.android.AndroidInjection
import greenely.greenely.R
import greenely.greenely.databinding.ActivityCompeteFriendInfoBinding
import greenely.greenely.databinding.GuidanceArticleDetailBinding
import greenely.greenely.guidance.models.Article
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class InfoActivity : AppCompatActivity() {

    lateinit var binding: ActivityCompeteFriendInfoBinding

    @Inject
    lateinit var tracker: Tracker


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compete_friend_info)
        bindViews()
    }


    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Add Friends info")
    }

    private fun bindViews() {
        binding.competeFriendInfoClose.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

}
