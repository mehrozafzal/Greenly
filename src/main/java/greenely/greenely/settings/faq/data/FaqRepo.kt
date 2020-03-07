package greenely.greenely.settings.faq.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.settings.faq.mappers.FaqItemMapper
import greenely.greenely.settings.faq.ui.models.FaqItem
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@OpenClassOnDebug
@Singleton
class FaqRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore,
        private val faqMapper: FaqItemMapper
) {
    fun getFaqItems(): Observable<List<FaqItem>> {
        return api.getFAQItems("JWT ${userStore.token}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { userStore.token = it.jwt }
                .map { faqMapper.fromJson(it.data) }
    }
}

