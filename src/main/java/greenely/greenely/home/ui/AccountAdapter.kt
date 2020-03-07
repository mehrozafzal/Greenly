package greenely.greenely.home.ui

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
import greenely.greenely.databinding.ItemAccountBinding
import greenely.greenely.databinding.ItemAccountFooterBinding
import greenely.greenely.databinding.ItemFriendListBinding
import greenely.greenely.databinding.ItemFriendListFooterBinding
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.profile.model.Account
import io.reactivex.rxkotlin.toMaybe
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.HashMap

class AccountAdapter(private val context: Context,
                     private val accountList: List<Account>,
                     private val homeHelper: HomeHelper)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_account -> {
                val inflater = LayoutInflater.from(context)
                val binding = ItemAccountBinding.inflate(inflater, parent, false)
                AccountViewHolder(binding)
            }
            R.layout.item_account_footer -> {
                val inflater = LayoutInflater.from(context)
                val binding = ItemAccountFooterBinding.inflate(inflater, parent, false)
                FooterViewHolder(binding)
            }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }

    }

    override fun getItemCount(): Int {
        return accountList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            accountList.size -> R.layout.item_account_footer
            else -> R.layout.item_account
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_account -> (holder as AccountViewHolder).bind(position)
            R.layout.item_account_footer -> (holder as FooterViewHolder).bind()
        }
    }

    inner class AccountViewHolder(val binding: ItemAccountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val account = accountList[position]
            binding.itemAccountUsername.text = account.name
            binding.itemAccountUserEmail.text = account.email
            binding.itemAccountContainer.setOnClickListener {
                homeHelper.switchAccount(account.userID)
            }
        }
    }

    inner class FooterViewHolder(val binding: ItemAccountFooterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.itemAccountFooterContainer.setOnClickListener {
                homeHelper.addAccount()
            }
        }
    }
}