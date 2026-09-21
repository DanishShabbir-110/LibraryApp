package com.example.libraryapp.presentation.members.membersList

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentMembersBinding
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.presentation.base.BaseFragment
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MembersFragment : BaseFragment<FragmentMembersBinding, MemberViewModel>(
    FragmentMembersBinding::inflate,
    MemberViewModel::class.java
) {
    private lateinit var memberAdapter: MemberAdapter

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
            binding.apply {
                titleHeader.tvTitle.text="Members"
                searchBar.etSearch.hint = getString(R.string.search_members)
                launch {
                    viewModel.members.collectLatest { members ->
                        memberAdapter.submitList(members)
                        rvMembers.isVisible = members.isNotEmpty()
                    }
                }
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        loadingOverlay.root.isVisible = isLoading
                        loadingOverlay.tvLoadingText.text = "Loading Members....."
                    }
                }
                launch {
                    combine(
                        viewModel.members,
                        viewModel.isLoading
                    ) { members, isLoading ->
                        members.isEmpty() && !isLoading
                    }.collectLatest { showNoData ->
                        tvNoData.isVisible = showNoData
                    }
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
                val action =
                    MembersFragmentDirections.memberFragmentToAddMemberFragment(memberId = member.id)
                findNavController().navigate(action)
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
        binding.searchBar.etSearch.doAfterTextChanged { editable ->
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
}