package greenely.greenely.signature.ui.models

sealed class PreFillInfo {
    data class Success(val needsAddressCheck: Boolean) : PreFillInfo()
    object Failed : PreFillInfo()
}

