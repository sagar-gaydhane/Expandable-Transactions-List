package com.adhyantacore.myapplication.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adhyantacore.myapplication.data.datasource.TransactionLocalDataSource
import com.adhyantacore.myapplication.data.repository.TransactionRepositoryImpl
import com.adhyantacore.myapplication.domain.usecase.GroupTransactionsUseCase

/**
 * A custom ViewModelProvider.Factory that handles creating our TransactionViewModel.
 * 
 * Since we are not using an automated dependency injection library (like Hilt or Dagger), 
 * this factory manually creates all the necessary dependencies in order:
 * DataSource -> Repository -> UseCase -> ViewModel.
 */
class TransactionViewModelFactory(
    private val applicationContext: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 1. Create the local data source which reads the raw JSON file from the assets folder.
        val dataSource = TransactionLocalDataSource(applicationContext)
        
        // 2. Pass the data source into the repository, which provides a clean data interface.
        val repository = TransactionRepositoryImpl(dataSource)
        
        // 3. Pass the repository into our use case, which handles sorting and grouping the data.
        val useCase = GroupTransactionsUseCase(repository)
        
        // 4. Finally, inject the use case into the ViewModel and return it for the Activity to use.
        @Suppress("UNCHECKED_CAST")
        return TransactionViewModel(useCase) as T
    }
}
