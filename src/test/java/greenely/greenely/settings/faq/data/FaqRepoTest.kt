package greenely.greenely.settings.faq.data

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.rubylichtenstein.rxtest.assertions.shouldEmit
import com.rubylichtenstein.rxtest.extentions.test
import greenely.greenely.api.models.Response
import greenely.greenely.api.GreenelyApi
import greenely.greenely.settings.faq.mappers.FaqItemMapper
import greenely.greenely.settings.faq.ui.models.FaqItem
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test


class FaqRepoTest {
    private lateinit var api: GreenelyApi
    private lateinit var userStore: UserStore
    private lateinit var mapper: FaqItemMapper
    private lateinit var repo: FaqRepo

    private val expectedFeedItems = listOf(FaqItem("Question", "Answer"))

    @Before
    fun setUp() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }

        val mockFaqJson = listOf(listOf("Question", "Answer"))

        api = mock {
            on {
                getFAQItems("JWT token")
            } doReturn Observable.just(Response(mockFaqJson, "token"))
        }
        userStore = mock {
            on { token } doReturn "token"
        }

        mapper = mock {
            on {
                fromJson(mockFaqJson)
            } doReturn expectedFeedItems
        }

        repo = FaqRepo(api, userStore, mapper)
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    @Test
    fun testGetFaqItems() {
        // When
        val observable = repo.getFaqItems().subscribeOn(Schedulers.from { it.run() })

        // Then
        observable.test {
            it shouldEmit expectedFeedItems
        }
    }
}
