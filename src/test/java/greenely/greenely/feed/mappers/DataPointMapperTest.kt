package greenely.greenely.feed.mappers

import greenely.greenely.feed.models.DataPoint
import greenely.greenely.feed.models.json.DataPointJson
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class DataPointMapperTest {

    private lateinit var mapper: DataPointMapper

    @Before
    fun setUp() {
        mapper = DataPointMapper()
    }

    @Test
    fun testFromJson() {
        // Given
        val date = DateTime(2017, 1, 1, 0, 0)
        val json = DataPointJson(date.millis / 1000, 200,200,200)

        // When
        val dataPoint = mapper.fromJson(json)

        // Then
        assertThat(dataPoint).isEqualTo(DataPoint(date, 200))
    }
}