package com.adhyantacore.myapplication.presentation.main


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhyantacore.myapplication.R
import com.adhyantacore.myapplication.databinding.ActivityTransactionBinding
import com.adhyantacore.myapplication.presentation.adapter.ExpandableTransactionAdapter
import kotlinx.coroutines.launch

class TransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionBinding


    private val viewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory(applicationContext)
    }

    private val adapter = ExpandableTransactionAdapter(
        onMidClicked = { mid -> viewModel.onMidHeaderClicked(mid) },
        onTidClicked = { mid, tid -> viewModel.onTidHeaderClicked(mid, tid) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = adapter

        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility =
                        if (state.isLoading) android.view.View.VISIBLE else android.view.View.GONE
                    binding.tvError.visibility =
                        if (state.error != null) android.view.View.VISIBLE else android.view.View.GONE
                    binding.tvError.text = state.error
                    binding.rvTransactions.visibility =
                        if (!state.isLoading && state.error == null) android.view.View.VISIBLE else android.view.View.GONE
                    adapter.submitList(state.items)
                }
            }
        }
    }
}
