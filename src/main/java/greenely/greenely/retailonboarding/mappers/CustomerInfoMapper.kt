package greenely.greenely.retailonboarding.mappers

import greenely.greenely.retailonboarding.models.CustomerInfoJson
import greenely.greenely.retailonboarding.models.CustomerInfoModel
import org.grunkspin.swedishformats.unformatPostalCode
import javax.inject.Inject

class CustomerInfoMapper @Inject constructor() {
    fun from(
            customerInfo: CustomerInfoModel
    ): CustomerInfoJson {
        return CustomerInfoJson(
                personal_number = customerInfo.personalNumber.get(),
                address = customerInfo.address.get(),
                zip_code = customerInfo.postalCode.get().unformatPostalCode(),
                city = customerInfo.postalRegion.get(),
                invoice_email = customerInfo.email.get(),
                cell_phone = customerInfo.phoneNumber.get(),
                promocode = customerInfo.promocode,
                poaProcessRequired = customerInfo.isFromPoaProcess.get()

        )
    }
}