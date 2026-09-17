package com.example.libraryapp.presentation.members.addmember

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentAddMemberBinding
import com.example.libraryapp.presentation.base.BaseFragment
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddMemberFragment : BaseFragment<FragmentAddMemberBinding, AddMemberViewModel>(
    FragmentAddMemberBinding::inflate,
    AddMemberViewModel::class.java
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUpdateScreen()
        setupTextListeners()
        setupClickListeners()
        observeUiState()
        observeUiEvents()
    }

    private fun observeUiEvents() {
        loadAndRepeatOnStarted {
            viewModel.events.collectLatest { event ->
                when (event) {
                    AddMemberEvents.MemberAdded -> {
                        Toast.makeText(
                            requireContext(),
                            "Member added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }

                    AddMemberEvents.MemberUpdated -> {
                        Toast.makeText(
                            requireContext(),
                            "Member updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }

                    is AddMemberEvents.ShowError -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupTextListeners() {
        binding.apply {
            etMemberName.doAfterTextChanged {
                viewModel.updateName(it?.toString().orEmpty())
            }

            etEmail.doAfterTextChanged {
                viewModel.updateEmail(it?.toString().orEmpty())
            }

            etPhone.doAfterTextChanged {
                viewModel.updatePhone(it?.toString().orEmpty())
            }
        }
    }

    private fun setupUpdateScreen() {
        val memberId = arguments?.getInt("memberId") ?: -1
        viewModel.loadMember(memberId)
    }

    private fun observeUiState() {
        loadAndRepeatOnStarted {
            binding.apply {
                launch {
                    viewModel.name.collectLatest { name ->
                        if (etMemberName.text.toString() != name) {
                            etMemberName.setText(name)
                        }
                    }
                }
                launch {
                    viewModel.phoneNumber.collectLatest { phoneNumber ->
                        if (etPhone.text.toString() != phoneNumber) {
                            etPhone.setText(phoneNumber)
                        }
                    }
                }
                launch {
                    viewModel.email.collectLatest { email ->
                        if (etEmail.text.toString() != email) {
                            etEmail.setText(email)
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        loadingOverlay.isVisible = isLoading
                        btnSaveMember.isEnabled = !isLoading
                    }
                }
                launch {
                    viewModel.isEditMode.collectLatest { isEditMode ->
                        if (isEditMode) {
                            tvTitle.text = getString(R.string.update_member)
                            btnSaveMember.text = getString(R.string.update_member)
                        } else {
                            tvTitle.text = getString(R.string.save_member)
                            btnSaveMember.text = getString(R.string.save_member)
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSaveMember.setOnClickListener {
                viewModel.saveMember()
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}