package com.adhyantacore.myapplication.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adhyantacore.myapplication.R
import com.adhyantacore.myapplication.databinding.ItemMidHeaderBinding
import com.adhyantacore.myapplication.databinding.ItemTidHeaderBinding
import com.adhyantacore.myapplication.databinding.ItemTransactionRowBinding
import java.text.NumberFormat
import java.util.Locale

class ExpandableTransactionAdapter(
    private val onMidClicked: (mid: Int) -> Unit,
    private val onTidClicked: (mid: Int, tid: Long) -> Unit
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ListItem.MidHeader -> VIEW_TYPE_MID
        is ListItem.TidHeader -> VIEW_TYPE_TID
        is ListItem.TransactionRowItem -> VIEW_TYPE_ROW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MID -> MidHeaderViewHolder(
                ItemMidHeaderBinding.inflate(inflater, parent, false),
                onMidClicked
            )
            VIEW_TYPE_TID -> TidHeaderViewHolder(
                ItemTidHeaderBinding.inflate(inflater, parent, false),
                onTidClicked
            )
            else -> TransactionRowViewHolder(
                ItemTransactionRowBinding.inflate(inflater, parent, false),
                currencyFormat
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MidHeaderViewHolder -> holder.bind(getItem(position) as ListItem.MidHeader)
            is TidHeaderViewHolder -> holder.bind(getItem(position) as ListItem.TidHeader)
            is TransactionRowViewHolder -> holder.bind(getItem(position) as ListItem.TransactionRowItem)
        }
    }

    class MidHeaderViewHolder(
        private val binding: ItemMidHeaderBinding,
        private val onMidClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.MidHeader) {
            binding.tvMidTitle.text = binding.root.context.getString(R.string.mid_title_format, item.mid)
            binding.tvMidSubtitle.text = binding.root.resources.getQuantityString(
                R.plurals.tid_count, item.tidCount, item.tidCount
            )
            binding.ivMidChevron.setImageResource(
                if (item.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
            )
            binding.root.setOnClickListener { onMidClicked(item.mid) }
        }
    }

    class TidHeaderViewHolder(
        private val binding: ItemTidHeaderBinding,
        private val onTidClicked: (Int, Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.TidHeader) {
            binding.tvTidTitle.text = binding.root.context.getString(R.string.tid_title_format, item.tid)
            binding.tvTidSubtitle.text = binding.root.resources.getQuantityString(
                R.plurals.entry_count, item.entryCount, item.entryCount
            )
            binding.ivTidChevron.setImageResource(
                if (item.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
            )
            binding.root.setOnClickListener { onTidClicked(item.mid, item.tid) }
        }
    }

    class TransactionRowViewHolder(
        private val binding: ItemTransactionRowBinding,
        private val currencyFormat: NumberFormat
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListItem.TransactionRowItem) {
            binding.tvNarration.text =
                binding.root.context.getString(R.string.narration_format, item.narration)
            binding.tvAmount.text = currencyFormat.format(item.amount)
        }
    }

    companion object {
        private const val VIEW_TYPE_MID = 0
        private const val VIEW_TYPE_TID = 1
        private const val VIEW_TYPE_ROW = 2

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListItem>() {
            override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                oldItem.stableId == newItem.stableId

            override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean =
                oldItem == newItem
        }
    }
}
