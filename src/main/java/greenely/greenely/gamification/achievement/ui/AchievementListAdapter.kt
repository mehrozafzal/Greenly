package greenely.greenely.gamification.achievement.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import greenely.greenely.R
import greenely.greenely.databinding.ItemAchievementsLayout1Binding
import greenely.greenely.databinding.ItemAchievementsLayout2Binding

class AchievementListAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_achievements_layout1 -> {
                val inflater = LayoutInflater.from(context)
                val binding = ItemAchievementsLayout1Binding.inflate(inflater, parent, false)
                AchievementItem1ViewHolder(binding)
            }
            R.layout.item_achievements_layout2 -> {
                val inflater = LayoutInflater.from(context)
                val binding = ItemAchievementsLayout2Binding.inflate(inflater, parent, false)
                AchievementItem2ViewHolder(binding)
            }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }

    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            R.layout.item_achievements_layout1
        } else {
            R.layout.item_achievements_layout2
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_achievements_layout1 -> (holder as AchievementItem1ViewHolder).bind()
            R.layout.item_achievements_layout2 -> (holder as AchievementItem2ViewHolder).bind()
        }
    }


    inner class AchievementItem1ViewHolder(val binding: ItemAchievementsLayout1Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }

    inner class AchievementItem2ViewHolder(val binding: ItemAchievementsLayout2Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }
}