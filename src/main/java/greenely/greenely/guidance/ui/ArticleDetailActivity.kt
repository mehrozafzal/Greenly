package greenely.greenely.guidance.ui

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import dagger.android.AndroidInjection
import greenely.greenely.R
import greenely.greenely.databinding.GuidanceArticleDetailBinding
import greenely.greenely.guidance.models.Article
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class ArticleDetailActivity : AppCompatActivity() {

    lateinit var binding: GuidanceArticleDetailBinding

    @Inject
    lateinit var tracker: Tracker

    private lateinit var itemTrackName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.guidance_article_detail)

        bindArticle()

        binding.toolbar.setNavigationOnClickListener {
            this.finish()
        }

        binding.detailText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun bindArticle() {
        val article = intent.extras?.getParcelable<Article>("article")
        binding.article = article
        itemTrackName = article?.thumbnailTitle + " " + article?.id
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("GU Articles " + itemTrackName)
    }
}
