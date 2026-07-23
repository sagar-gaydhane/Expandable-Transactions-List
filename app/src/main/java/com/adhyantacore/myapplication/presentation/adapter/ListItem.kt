package com.adhyantacore.myapplication.presentation.adapter

sealed class ListItem {

    abstract val stableId: Long

    data class MidHeader(
        val mid: Int,
        val isExpanded: Boolean,
        val tidCount: Int
    ) : ListItem() {
        override val stableId: Long = "mid_$mid".hashCode().toLong()
    }

    data class TidHeader(
        val mid: Int,
        val tid: Long,
        val isExpanded: Boolean,
        val entryCount: Int
    ) : ListItem() {
        override val stableId: Long = "tid_${mid}_$tid".hashCode().toLong()
    }

    data class TransactionRowItem(
        val mid: Int,
        val tid: Long,
        val index: Int,
        val amount: Double,
        val narration: Long
    ) : ListItem() {
        override val stableId: Long = "row_${mid}_${tid}_$index".hashCode().toLong()
    }
}
