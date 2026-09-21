package com.example.libraryapp.presentation.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.databinding.FragmentBrowsingHistoryBinding
import com.example.libraryapp.presentation.base.BaseFragment
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BorrowingHistoryFragment :
    BaseFragment<FragmentBrowsingHistoryBinding, BorrowingHistoryViewModel>(
        FragmentBrowsingHistoryBinding::inflate,
        BorrowingHistoryViewModel::class.java
    ) {
    private lateinit var borrowingHistoryAdapter: BorrowingHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeUiState()
        setupSearch()
        setupStatusDropdown()
    }


    @SuppressLint("SetTextI18n")
    private fun observeUiState() {
        loadAndRepeatOnStarted {
            binding.apply {
                titleHeader.tvTitle.text = "Borrowing History"
                launch {
                    viewModel.borrowBooks.collectLatest { borrowBooks ->
                        borrowingHistoryAdapter.submitList(borrowBooks)
                        rvBorrowingHistory.isVisible = borrowBooks.isNotEmpty()
                    }
                }
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        loadingOverlay.root.isVisible = isLoading
                        loadingOverlay.tvLoadingText.text = "Loading History..."
                    }
                }
                launch {
                    combine(viewModel.borrowBooks, viewModel.isLoading) { books, isLoading ->
                        books.isEmpty() && !isLoading
                    }.collectLatest {
                        tvNoData.isVisible = it

                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        borrowingHistoryAdapter = BorrowingHistoryAdapter()
        binding.rvBorrowingHistory.apply {
            adapter = borrowingHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearch() {

        binding.etSearch.doAfterTextChanged { editable ->
            viewModel.searchBooks(
                editable?.toString().orEmpty()
            )
        }
    }

    private fun setupStatusDropdown() {
        loadAndRepeatOnStarted {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                viewModel.statusList
            )

            binding.actStatus.apply {
                setAdapter(adapter)
                launch {
                    viewModel.selectedStatus.collectLatest { selectedStatus ->
                        setText(
                            selectedStatus.name.lowercase()
                                .replaceFirstChar { it.uppercase() }, false
                        )
                    }
                }
                keyListener = null
                setOnItemClickListener { _, _, position, _ ->
                    viewModel.selected(position)
                }
            }
        }
    }
}
