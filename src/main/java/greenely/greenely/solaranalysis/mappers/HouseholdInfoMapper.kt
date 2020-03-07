package greenely.greenely.solaranalysis.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.solaranalysis.models.AddressValidationRequest
import greenely.greenely.solaranalysis.models.HouseholdInfo
import greenely.greenely.solaranalysis.models.HouseholdInfoJson
import org.grunkspin.swedishformats.unformatPostalCode
import javax.inject.Inject

@OpenClassOnDebug
class HouseholdInfoMapper @Inject constructor() {
    fun toJsonData(householdInfo: HouseholdInfo): HouseholdInfoJson {
        return HouseholdInfoJson(
                householdInfo.getFullAddress(),
                householdInfo.getRoofAngle(),
                householdInfo.getDegreesFromSouth(),
                householdInfo.getRoofSize()
        )
    }

    fun toAddressValidationRequest(householdInfo: HouseholdInfo): AddressValidationRequest {
        return AddressValidationRequest(householdInfo.getFullAddress())
    }

    private fun HouseholdInfo.getFullAddress() =
            "${address.get()} ${postalCode.get().unformatPostalCode()} ${postalRegion.get()}"

    private fun HouseholdInfo.getRoofAngle() = when (roofAngleId.get()) {
        R.id.tightAngle -> 5
        R.id.mediumAngle -> 20
        R.id.wideAngle -> 45
        else -> throw IllegalArgumentException("${roofAngleId.get()} is not a valid roof angle id.")
    }

    private fun HouseholdInfo.getDegreesFromSouth() = when (roofDirectionId.get()) {
        R.id.south -> 0
        R.id.southWest -> -40
        R.id.west -> -80
        R.id.southEast -> 50
        R.id.east -> 90
        else -> throw IllegalArgumentException(
                "${roofDirectionId.get()} is not a valid roof direction id."
        )
    }

    private fun HouseholdInfo.getRoofSize() = when (roofSizeId.get()) {
        R.id.small -> 40
        R.id.medium -> 53
        R.id.big -> 68
        else -> throw IllegalArgumentException("${roofSizeId.get()} is not a valid roof size id.")
    }
}

