package greenely.greenely.feed.mappers

import android.app.Application
import greenely.greenely.R
import greenely.greenely.feed.models.CostAnalysisChartData
import greenely.greenely.feed.models.feeditems.CostAnalysisFeedItem
import greenely.greenely.feed.models.json.CostAnalysisJson
import greenely.greenely.utils.CommonUtils
import greenely.greenely.utils.toCurrenyWithDecimalCheck
import org.joda.time.DateTime
import javax.inject.Inject

class CostAnalysisMapper @Inject constructor(val application: Application,
                                             val costAnalysisDataPointMapper: CostAnalysisDataPointMapper) {


    fun fromJson(costAnalysisJson: CostAnalysisJson): CostAnalysisFeedItem {
        val date = DateTime(costAnalysisJson.created * 1000L)

       var chartData= CostAnalysisChartData(costAnalysisJson.chartData.map { costAnalysisDataPointMapper.fromJson(it)})

        return CostAnalysisFeedItem(
                date,costAnalysisJson.title,
                costAnalysisJson.extra.subtitle,
                costAnalysisJson.text,
                costAnalysisJson.extra.marketCostInKr,
                costAnalysisJson.extra.savingsInKr,
                costAnalysisJson.extra.avgMarketPrice.toFloat(),
                costAnalysisJson.extra.avgGreenelyPrice,
                getAvgPriceStr(costAnalysisJson.extra.avgMarketPrice.toFloat()),
                getAvgPriceStr(costAnalysisJson.extra.avgGreenelyPrice),
                CommonUtils.getCurrencyFormat(costAnalysisJson.extra.savingsInKr),
                costAnalysisJson.extra.greenelyCostInKr,
                costAnalysisJson.extra.marketCostInKr,
                costAnalysisJson.extra.greenelyCostInKr.toCurrenyWithDecimalCheck(),
                costAnalysisJson.extra.marketCostInKr.toCurrenyWithDecimalCheck(),
                costAnalysisJson.newEntry,
                chartData)
    }

    private fun getAvgPriceStr(value:Float):String {
        var str=""
        value?.let {
            var avgInKr= value/100;
            str=String.format(application.getString(R.string.avg_price_cost_analysis),avgInKr.toCurrenyWithDecimalCheck())
        }
        return str
    }
}