package com.example.libraryapp.presentation.history

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
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
        setupBackArrow()
        setupStatusDropdown()
        setupListeners()
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupBackArrow() {

        val showBackArrow =
            arguments?.getBoolean("showBackArrow", false) ?: false

        binding.ivBack.isVisible = showBackArrow
    }

    private fun observeUiState() {
        loadAndRepeatOnStarted {
            binding.apply {
                launch {
                    viewModel.borrowBooks.collectLatest { borrowBooks ->
                        borrowingHistoryAdapter.submitList(borrowBooks)
                        rvBorrowingHistory.isVisible = borrowBooks.isNotEmpty()
                    }
                }
                launch {
                    combine(viewModel.borrowBooks,viewModel.isLoading){books, isLoading ->
                        books.isEmpty()&&!isLoading
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
