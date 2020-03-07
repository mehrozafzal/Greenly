package greenely.greenely.competefriend.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import greenely.greenely.R
import greenely.greenely.competefriend.json.rankListJson.FriendsItemJson
import greenely.greenely.databinding.ItemFriendListBinding
import greenely.greenely.databinding.ItemFriendListFooterBinding
import greenely.greenely.main.ui.MainActivity
import io.reactivex.rxkotlin.toMaybe
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class FriendsAdapter(private val context: Context,
                     private val friendList: MutableList<FriendsItemJson>,
                     private val competeFriendHelper: CompeteFriendHelper)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val measurementFormat = DecimalFormat()
    val symbols = DecimalFormatSymbols(Locale("sv", "SE"))


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_friend_list -> {
                val inflater = LayoutInflater.from(context)
                val binding = ItemFriendListBinding.inflate(inflater, parent, false)
                CellViewHolder(binding)
            }
            R.layout.item_friend_list_footer -> {
                val inflater = LayoutInflater.from(context)
                val binding = ItemFriendListFooterBinding.inflate(inflater, parent, false)
                FooterViewHolder(binding)
            }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }

    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    fun removeFriendFromList(position: Int) {
        friendList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            friendList.size -> R.layout.item_friend_list_footer
            else -> R.layout.item_friend_list
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_friend_list -> (holder as CellViewHolder).bind(position, friendList[position])
            R.layout.item_friend_list_footer -> (holder as FooterViewHolder).bind()
        }
    }


    inner class CellViewHolder(val binding: ItemFriendListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, item: FriendsItemJson) {
            when (item.ranking) {
                1 -> {
                    toggleBadgeView(binding, item, R.drawable.gold_medal)
                }
                2 -> {
                    toggleBadgeView(binding, item, R.drawable.silver_medal)
                }
                3 -> {
                    toggleBadgeView(binding, item, R.drawable.bronze_medal)
                }
                else -> {
                    binding.itemFriendBadgeText.visibility = View.VISIBLE
                    binding.itemFriendBadge.visibility = View.GONE
                    if (item.ranking == null)
                        binding.itemFriendBadgeText.text = "-"
                    else
                        binding.itemFriendBadgeText.text = item.ranking.toString()
                }
            }


            binding.itemFriendName.text = item.friendAlias
            if (item.avatarUrl == null) {
                binding.itemFriendUserProfileImage.visibility = View.GONE
                binding.itemFriendTagContainer.visibility = View.VISIBLE
                binding.itemFriendNameTag.text = item.friendAlias?.get(0).toString().trim().toUpperCase()
            } else {
                binding.itemFriendUserProfileImage.visibility = View.VISIBLE
                binding.itemFriendTagContainer.visibility = View.GONE
                binding.itemFriendUserProfileImage.load(item.avatarUrl) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            }

            binding.circleView.circleColor = Color.parseColor(item.avatarColor)
            if (item.measurement != null) {
                val measurement = item.measurement.toString().toDouble()
                if (measurement < 10) {
                    symbols.decimalSeparator = ','
                    measurementFormat.maximumFractionDigits = 1
                    measurementFormat.decimalFormatSymbols = symbols
                    binding.itemFriendKwh.text = measurementFormat.format(measurement)
                } else {
                    binding.itemFriendKwh.text = (item.measurement.toString()).toDouble().toInt().toString()
                }

                binding.itemFriendKwh.setTextAppearance(context, R.style.TextAppearance_ranking1)
                binding.itemFriendKwh.setTextColor(context.resources.getColor(R.color.green_3))
                binding.itemFriendKwh.visibility = View.VISIBLE
                binding.itemFriendInviteFriend.visibility = View.GONE
            } else {
                if (item.friendId == null && item.facilityState.equals("no_poa_signed")) {
                    binding.itemFriendInviteFriend.visibility = View.VISIBLE
                    binding.itemFriendKwh.visibility = View.GONE
                } else {
                    binding.itemFriendInviteFriend.visibility = View.GONE
                    binding.itemFriendKwh.visibility = View.VISIBLE
                    binding.itemFriendKwh.text = item.message
                    binding.itemFriendKwh.setTextAppearance(context, R.style.TextAppearance_ranking2)
                    binding.itemFriendKwh.setTextColor(context.resources.getColor(R.color.grey2))
                }
            }

            binding.root.setOnClickListener {
                competeFriendHelper.onItemClicked(adapterPosition, item)
            }

            binding.itemFriendInviteFriend.setOnClickListener {
                competeFriendHelper.onSignPoaClicked()
            }
        }
    }

    private fun toggleBadgeView(binding: ItemFriendListBinding, item: FriendsItemJson, drawable: Int) {
        if (item.ranking == null) {
            binding.itemFriendBadgeText.visibility = View.VISIBLE
            binding.itemFriendBadge.visibility = View.GONE
            binding.itemFriendBadgeText.text = "-"
        } else {
            binding.itemFriendBadgeText.visibility = View.GONE
            binding.itemFriendBadge.visibility = View.VISIBLE
            binding.itemFriendBadge.setBackgroundResource(drawable)
        }
    }

    inner class FooterViewHolder(val binding: ItemFriendListFooterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.competeFriendInviteUserSecondBtn.setOnClickListener {
                competeFriendHelper.onShareBtnClicked()
            }
        }
    }
}