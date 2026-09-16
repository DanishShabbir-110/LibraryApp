package com.example.libraryapp.presentation.members.membersList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentMembersBinding
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MembersFragment : Fragment() {

    private val viewModel: MemberViewModel by viewModels()
    private var _binding: FragmentMembersBinding? = null
    private val binding get() = _binding!!
    private lateinit var memberAdapter: MemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembersBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearch()
        setupRecyclerView()
        setupClickListeners()
        observeUiState()
        observeUiEvents()
    }

    private fun observeUiEvents() {
        loadAndRepeatOnStarted {
            viewModel.events.collectLatest { event ->
                when (event) {
                    MemberEvents.MemberDeleted -> {
                        Toast.makeText(
                            requireContext(),
                            "Member deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is MemberEvents.ShowError -> {
                        Toast.makeText(
                            requireContext(),
                            event.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun observeUiState() {
        loadAndRepeatOnStarted {
            viewModel.uiState.collectLatest { state ->
                memberAdapter.submitList(state.members)
                binding.apply {
                    tvNoData.isVisible = state.members.isEmpty() && !state.isLoading
                    rvMembers.isVisible = state.members.isNotEmpty()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        memberAdapter = MemberAdapter(
            onDeleteClick = { member ->
                showDeleteDialog(member)
            },
            onUpdateClick = { member ->
                val bundle = Bundle().apply {
                    putInt("memberId", member.id)
                }
                findNavController().navigate(R.id.addMemberFragment, bundle)
            }
        )
        binding.rvMembers.apply {
            adapter = memberAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun setupClickListeners() {
        binding.fabAddMember.setOnClickListener {
            findNavController().navigate(R.id.addMemberFragment)
        }
    }

    private fun setupSearch() {
        binding.etSearchMembers.doAfterTextChanged { editable ->
            viewModel.searchMembers(editable?.toString().orEmpty())
        }
    }


    private fun showDeleteDialog(member: Member) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Member")
            .setMessage("Are you sure want to delete this member.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteMember(member)
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}