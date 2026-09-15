package com.example.libraryapp.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.databinding.FragmentBrowsingHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BorrowingHistoryFragment : Fragment() {
    private val viewModel: BorrowingHistoryViewModel by viewModels()

    private lateinit var borrowingHistoryAdapter: BorrowingHistoryAdapter
    private var _binding: FragmentBrowsingHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrowsingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.uiState.collect { state ->
                    borrowingHistoryAdapter.submitList(state.borrowList)
                    binding.tvNoData.isVisible = state.borrowList.isEmpty() && !state.isLoading
                    binding.rvBorrowingHistory.isVisible = state.borrowList.isNotEmpty()
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

        binding.etSearch.doAfterTextChanged { editable ->
            viewModel.searchBooks(
                editable?.toString().orEmpty()
            )
        }
    }

    private fun setupStatusDropdown() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        state.statusList
                    )

                    binding.actStatus.apply {
                        setAdapter(adapter)
                        setText(
                            state.selectedStatus.name.lowercase()
                                .replaceFirstChar { it.uppercase() }, false
                        )
                        keyListener = null
                        setOnItemClickListener { _, _, position, _ ->
                            viewModel.selected(position)
                        }
                    }
                }
            }
        }
    }
}