package greenely.greenely.gamification.reward.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import greenely.greenely.databinding.ItemRedeemBinding

class RewardListAdapter(val context: Context) : RecyclerView.Adapter<RewardListAdapter.RewardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemRedeemBinding.inflate(inflater, parent, false)
        return RewardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        holder.bind()
    }

    inner class RewardViewHolder(val binding: ItemRedeemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

}