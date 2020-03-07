package greenely.greenely.feed.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.models.DataPoint
import greenely.greenely.feed.models.json.DataPointJson
import org.joda.time.DateTime
import javax.inject.Inject

@OpenClassOnDebug
class DataPointMapper @Inject constructor() {
    fun fromJson(dataPointJson: DataPointJson): DataPoint {
        return DataPoint(
                DateTime(dataPointJson.timestamp * 1000L),
                dataPointJson.usage
        )
    }
}

