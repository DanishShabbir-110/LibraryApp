package com.example.libraryapp.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.databinding.FragmentBrowsingHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrowsingHistoryFragment : Fragment() {
    private val viewModel: BorrowingHistoryViewModel by viewModels()

    private lateinit var borrowingHistoryAdapter: BorrowingHistoryAdapter
    private var _binding: FragmentBrowsingHistoryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowsingHistoryBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeUiState()
        setupSearch()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.uiState.collect { state ->
                    borrowingHistoryAdapter.submitList(state.borrowList)
                    state.errorMessage?.let { message ->
                        Toast.makeText(
                            requireContext(),
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                        viewModel.clearError()
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

        binding.etSearch
            .doAfterTextChanged { editable ->

                viewModel.searchBooks(
                    editable?.toString().orEmpty()
                )
            }
    }
}