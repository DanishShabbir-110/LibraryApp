package com.example.libraryapp.presentation.dashboard

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentDashboardBinding
import com.example.libraryapp.presentation.base.BaseFragment
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding, DashboardViewModel>(
    FragmentDashboardBinding::inflate,
    DashboardViewModel::class.java
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionListeners()
        observeUiState()
    }

    private fun setupActionListeners() {
        binding.clIssueReturn.setOnClickListener {
            findNavController().navigate(R.id.issueReturnBookFragment)
        }
    }

    private fun observeUiState() {
        loadAndRepeatOnStarted {
            binding.apply {
                launch {
                    viewModel.totalBooks.collectLatest { totalBooks ->
                        tvTotalBooks.text = totalBooks.toString()
                    }
                }
                launch {
                    viewModel.totalMembers.collectLatest { totalMembers ->
                        tvTotalMembers.text = totalMembers.toString()
                    }
                }
                launch {
                    viewModel.issueBooks.collectLatest { issueBooks ->
                        tvBorrowedBooks.text = issueBooks.toString()
                    }
                }
                launch {
                    viewModel.availableCopies.collectLatest { availableCopies ->
                        tvAvailableBooks.text = availableCopies.toString()
                    }
                }
            }
        }
    }
}