package greenely.greenely.retailonboarding.models

import greenely.greenely.signature.ui.events.Event

sealed class PoaType {
    object NormalPoa : PoaType()
    object CombinedPOA : PoaType()


    companion object {
        fun fromPoaType(type: PoaType): Event {
            return if(type is NormalPoa) Event.ReadPoa else Event.ReadCombinedPOA
        }
    }

    fun isCombinedPOA()= this is CombinedPOA

}