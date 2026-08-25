package com.example.libraryapp.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentDashboardBinding
import com.example.libraryapp.presentation.AppMenuFragment
import com.example.libraryapp.presentation.issue_return.IssueReturnBookFragment
import com.example.libraryapp.presentation.members.MembersFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private val viewModel: DashboardViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupActionListeners()
        observeUiState()
    }

    private fun setupActionListeners() {
        binding.clBooks.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("showBackArrow", true) }
            findNavController().navigate(R.id.nav_books,bundle)
        }
        binding.clMembers.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("showBackArrow", true) }
            findNavController().navigate(R.id.nav_members,bundle)
        }
        binding.clIssueReturn.setOnClickListener {
            findNavController().navigate(R.id.issueReturnBookFragment)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.uiState.collect { state ->
                    binding.tvTotalBooks.text = state.totalBooks.toString()
                    binding.tvTotalMembers.text = state.totalMembers.toString()
                    binding.tvAvailableBooks.text = state.availableBooks.toString()
                    binding.tvBorrowedBooks.text = state.issuedBooks.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}