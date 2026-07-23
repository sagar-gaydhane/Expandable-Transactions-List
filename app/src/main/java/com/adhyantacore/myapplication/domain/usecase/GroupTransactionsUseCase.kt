package com.adhyantacore.myapplication.domain.usecase

import com.adhyantacore.myapplication.domain.model.MidGroup
import com.adhyantacore.myapplication.domain.model.TidGroup
import com.adhyantacore.myapplication.domain.model.TransactionEntry
import com.adhyantacore.myapplication.domain.model.TransactionRow
import com.adhyantacore.myapplication.domain.repository.TransactionRepository


/**
 * Builds the two-level expandable structure the UI needs:
 *   Mid (unique, sorted ascending)
 *     -> Tid (grouped, sorted ascending)
 *          -> amount/narration entries (kept in original order, duplicates included)
 */
class GroupTransactionsUseCase(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(): List<MidGroup> {
        val rows = repository.getTransactions()
        return rows
            .groupBy { it.mid }
            .toSortedMap()
            .map { (mid, midRows) -> mid to midRows.buildTidGroups() }
            .map { (mid, tidGroups) -> MidGroup(mid = mid, tidGroups = tidGroups) }
    }

    private fun List<TransactionRow>.buildTidGroups(): List<TidGroup> {
        return groupBy { it.tid }
            .toSortedMap()
            .map { (tid, tidRows) ->
                TidGroup(
                    tid = tid,
                    entries = tidRows.map { TransactionEntry(it.amount, it.narration) }
                )
            }
    }
}
