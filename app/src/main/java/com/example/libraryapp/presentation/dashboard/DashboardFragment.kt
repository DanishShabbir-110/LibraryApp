package com.example.libraryapp.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentDashboardBinding
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private val viewModel: DashboardViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

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
            viewModel.uiState.collect { state ->
                binding.apply {
                    tvTotalBooks.text = state.totalBooks.toString()
                    tvTotalMembers.text = state.totalMembers.toString()
                    tvAvailableBooks.text = state.availableBooks.toString()
                    tvBorrowedBooks.text = state.issuedBooks.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}