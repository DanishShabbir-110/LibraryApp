package com.example.libraryapp.presentation.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentDashboardBinding
import com.example.libraryapp.presentation.books.BooksFragment
import com.example.libraryapp.presentation.issue_return.IssueReturnBookFragment
import com.example.libraryapp.presentation.members.MembersFragment
import com.example.libraryapp.presentation.members.addmember.AddMemberFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private val viewModel: DashboardViewModel by viewModels()
    private  var _binding: FragmentDashboardBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentDashboardBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clBooks.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, BooksFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.clMembers.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, MembersFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.clIssueReturn.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, IssueReturnBookFragment())
                .addToBackStack(null)
                .commit()
        }

        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ){
                viewModel.uiState.collect {state->
                    binding.tvTotalBooks.text=state.totalBooks.toString()
                    binding.tvTotalMembers.text=state.totalMembers.toString()
                    binding.tvAvailableBooks.text=state.availableBooks.toString()
                    binding.tvBorrowedBooks.text=state.issuedBooks.toString()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}