package greenely.greenely.setuphousehold.ui.viewholder

import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import greenely.greenely.R
import greenely.greenely.databinding.CheckboxListItemBinding
import greenely.greenely.setuphousehold.models.HouseholdInputOptions
import vn.tiki.noadapter2.AbsViewHolder

class SecondaryHeatingViewHolder(
        private val binding: CheckboxListItemBinding,
        private val list: ArrayList<HouseholdInputOptions>,
        private val checkedValuesList: List<Int?>
) : AbsViewHolder(binding.root) {

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup, heatingTypesList: ArrayList<HouseholdInputOptions>,
                   checkedValuesList: List<Int?>): AbsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: CheckboxListItemBinding = DataBindingUtil.inflate(
                    inflater, R.layout.checkbox_list_item, parent, false
            )
            return SecondaryHeatingViewHolder(binding, heatingTypesList, checkedValuesList)
        }
    }

    override fun bind(option: Any?) {
        super.bind(option)

        if (option is HouseholdInputOptions) {
            binding.checkBox.isChecked = list.contains(option)
            binding.checkBox.text = option.name

            //prefill
            binding.checkBox.isChecked = checkedValuesList.isNotEmpty() && checkedValuesList.contains(option.id)
        }
    }
}
