package greenely.greenely.signature.ui.steps.confirmation_step

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import greenely.greenely.R
import greenely.greenely.signature.ui.models.ConfirmationStepListItemModel
import kotlinx.android.synthetic.main.signature_confirmation_rec_view_item.view.*

class ConfirmationScreenAdapter(val items: ArrayList<ConfirmationStepListItemModel>, val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.signature_confirmation_rec_view_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parameter: ConfirmationStepListItemModel = items[position]
        holder.parameterLabel.text = parameter.label
        holder.parameterValue.text = parameter.value
        holder.greenTick.setImageDrawable(context.resources.getDrawable(R.drawable.green_tick))
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    val parameterLabel = view.personNumerLabelTextView!!
    val parameterValue = view.personNumerTextView!!
    val greenTick = view.greenTick!!
}
