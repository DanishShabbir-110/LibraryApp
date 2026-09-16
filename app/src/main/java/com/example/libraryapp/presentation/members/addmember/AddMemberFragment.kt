package com.example.libraryapp.presentation.members.addmember

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
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentAddMemberBinding
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddMemberFragment : Fragment() {
    private val viewModel: AddMemberViewModel by viewModels()

    private var _binding: FragmentAddMemberBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMemberBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

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
            viewModel.uiState.collectLatest { state ->
                binding.apply {
                    if (etMemberName.text.toString() != state.name) {
                        etMemberName.setText(state.name)
                    }

                    if (etEmail.text.toString() != state.email) {
                        etEmail.setText(state.email)
                    }

                    if (etPhone.text.toString() != state.phoneNumber) {
                        etPhone.setText(state.phoneNumber)
                    }

                    loadingOverlay.isVisible = state.isLoading
                    btnSaveMember.isEnabled = !state.isLoading

                    if (state.isEditMode) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}