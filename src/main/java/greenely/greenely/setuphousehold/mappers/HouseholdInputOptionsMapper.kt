package greenely.greenely.setuphousehold.mappers

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.setuphousehold.models.HouseholdInputOptions

@OpenClassOnDebug
class HouseholdInputOptionsMapper {

    @Suppress("unused")
    @ToJson
    fun inputOptionToJson(option: HouseholdInputOptions): List<@JvmSuppressWildcards Any> =
            listOf(option.id, option.name)

    @Suppress("unused")
    @FromJson
    fun inputOptionFromJson(array: List<@JvmSuppressWildcards Any>): HouseholdInputOptions {
        val id: Double = array[0] as Double
        val name: String = array[1] as String

        return HouseholdInputOptions(id.toInt(), name)
    }
}
