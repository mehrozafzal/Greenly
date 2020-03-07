package greenely.greenely.feed.data

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.feed.mappers.*
import greenely.greenely.feed.models.FeedResponse
import greenely.greenely.feed.models.feeditems.DatedFeedItem
import greenely.greenely.profile.json.GetProfileResponse
import greenely.greenely.store.UserStore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenClassOnDebug
class FeedRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore,
        private val messageMapper: MessageMapper,
        private val minMaxMapper: MinMaxMapper,
        private val monthlyReportMapper: MonthlyReportMapper,
        private val newsMapper: NewsMapper,
        private val tipMapper: TipMapper,
        private val weeklyReportMapper: WeeklyReportMapper,
        private val costAnalysisMapper: CostAnalysisMapper,
        private val predictedBillMapper: PredictedSavingsMapper
) {

    fun getFeed(): Observable<List<DatedFeedItem>> =
            api.getFeed("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }
                    .map(this::createFeedItemList)


    fun hasUnreadItems(): Observable<Boolean> =
            api.getFeedNewCount("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data.count > 0 }
                    .onErrorReturn { false }

    private fun createFeedItemList(feedResponse: FeedResponse): List<DatedFeedItem> {
        return feedResponse.messages.map { messageMapper.fromJson(it) } +
                feedResponse.monthlyReports.mapNotNull { p -> p.chartData?.let { monthlyReportMapper.fromJson(p) } } +
                feedResponse.news.map { newsMapper.fromJson(it) } +
                feedResponse.weeklyReports.mapNotNull { p -> p.currentPeriodChartData?.let { weeklyReportMapper.fromJson(p) } } +
                feedResponse.costAnalysis.map { costAnalysisMapper.fromJson(it) } +
                feedResponse.predictedInvoices.map { predictedBillMapper.fromJson(it) } +
                feedResponse.predictedYearlySaving.map { predictedBillMapper.fromJson(it) }
    }

    fun getProfileResponse(): Observable<GetProfileResponse> =
            api.getProfileResponse("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map {
                        it.data
                    }
}


