package greenely.greenely.feed.mappers

import greenely.greenely.feed.models.feeditems.PredictedInvoiceItem
import greenely.greenely.feed.models.feeditems.PredictedYearlySavingsFeedItem
import greenely.greenely.feed.models.json.PredictedInvoiceJson
import greenely.greenely.feed.models.json.PredictedYearlySavingsJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import greenely.greenely.feed.utils.getYearAndMonth
import greenely.greenely.utils.toCurrenyWithDecimalCheck
import org.joda.time.DateTime
import javax.inject.Inject

class PredictedSavingsMapper  @Inject constructor(
        private val feedItemBodyFormatter: FeedItemBodyFormatter
) {
    fun fromJson(predictedInvoiceJson: PredictedInvoiceJson): PredictedInvoiceItem {
        return PredictedInvoiceItem(
                DateTime(predictedInvoiceJson.created * 1000L),
                predictedInvoiceJson.contextDate.getYearAndMonth().capitalize(),
                predictedInvoiceJson.title,
                feedItemBodyFormatter.formatBody(predictedInvoiceJson.text),
                predictedInvoiceJson.extra.subtext,
                predictedInvoiceJson.extra.predictionInKr.toCurrenyWithDecimalCheck(),
                predictedInvoiceJson.newEntry
        )
    }

    fun fromJson(predictedYearlySavingsJson: PredictedYearlySavingsJson): PredictedYearlySavingsFeedItem {
        return PredictedYearlySavingsFeedItem(
                DateTime(predictedYearlySavingsJson.created * 1000L),
                predictedYearlySavingsJson.contextDate.getYearAndMonth().capitalize(),
                predictedYearlySavingsJson.title,
                predictedYearlySavingsJson.text?.let {feedItemBodyFormatter.formatBody(predictedYearlySavingsJson.text) },
                predictedYearlySavingsJson.extra.predictionInKr.toCurrenyWithDecimalCheck(),
                predictedYearlySavingsJson.newEntry
        )
    }
}