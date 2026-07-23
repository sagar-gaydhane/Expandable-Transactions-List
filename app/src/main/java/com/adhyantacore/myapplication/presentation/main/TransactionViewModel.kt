package com.adhyantacore.myapplication.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhyantacore.myapplication.domain.model.MidGroup
import com.adhyantacore.myapplication.domain.usecase.GroupTransactionsUseCase
import com.adhyantacore.myapplication.presentation.adapter.ListItem

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val groupTransactionsUseCase: GroupTransactionsUseCase
) : ViewModel() {

    private var groups: List<MidGroup> = emptyList()

    // Both start empty (all-collapsed). Keys are the group identities.
    private val expandedMids = mutableSetOf<Int>()
    private val expandedTids = mutableSetOf<Pair<Int, Long>>()

    private val _uiState = MutableStateFlow(TransactionUiState(isLoading = true))
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { groupTransactionsUseCase() }
                .onSuccess { result ->
                    groups = result
                    // Expand the first Mid by default so the screen isn't empty on first load.
                    groups.firstOrNull()?.let { expandedMids.add(it.mid) }
                    refreshFlattenedList()
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Something went wrong"
                        )
                    }
                }
        }
    }

    fun onMidHeaderClicked(mid: Int) {
        if (!expandedMids.add(mid)) {
            expandedMids.remove(mid)
            // Collapsing a Mid also collapses every Tid inside it.
            expandedTids.removeAll { (m, _) -> m == mid }
        }
        refreshFlattenedList()
    }

    fun onTidHeaderClicked(mid: Int, tid: Long) {
        val key = mid to tid
        if (!expandedTids.add(key)) {
            expandedTids.remove(key)
        }
        refreshFlattenedList()
    }

    private fun refreshFlattenedList() {
        val flattened = buildList {
            groups.forEach { midGroup ->
                val midExpanded = midGroup.mid in expandedMids
                add(
                    ListItem.MidHeader(
                        mid = midGroup.mid,
                        isExpanded = midExpanded,
                        tidCount = midGroup.tidGroups.size
                    )
                )
                if (!midExpanded) return@forEach

                midGroup.tidGroups.forEach { tidGroup ->
                    val tidExpanded = (midGroup.mid to tidGroup.tid) in expandedTids
                    add(
                        ListItem.TidHeader(
                            mid = midGroup.mid,
                            tid = tidGroup.tid,
                            isExpanded = tidExpanded,
                            entryCount = tidGroup.entries.size
                        )
                    )
                    if (!tidExpanded) return@forEach

                    tidGroup.entries.forEachIndexed { index, entry ->
                        add(
                            ListItem.TransactionRowItem(
                                mid = midGroup.mid,
                                tid = tidGroup.tid,
                                index = index,
                                amount = entry.amount,
                                narration = entry.narration
                            )
                        )
                    }
                }
            }
        }
        _uiState.update { it.copy(isLoading = false, items = flattened, error = null) }
    }
}
