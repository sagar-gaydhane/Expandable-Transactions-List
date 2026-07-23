package com.adhyantacore.myapplication.domain.model

/** One raw row exactly as it comes from the source. */
data class TransactionRow(
    val mid: Int,
    val tid: Long,
    val amount: Double,
    val narration: Long
)

/** A single amount/narration entry under a Tid. Tid itself is not unique -
 *  the same Tid can repeat with different (or duplicate) amount/narration pairs. */
data class TransactionEntry(
    val amount: Double,
    val narration: Long
)

/** Level 2 group: all entries sharing the same Tid, under one Mid. */
data class TidGroup(
    val tid: Long,
    val entries: List<TransactionEntry>
)

/** Level 1 group: all Tid groups sharing the same Mid. Mid is unique. */
data class MidGroup(
    val mid: Int,
    val tidGroups: List<TidGroup>
)
