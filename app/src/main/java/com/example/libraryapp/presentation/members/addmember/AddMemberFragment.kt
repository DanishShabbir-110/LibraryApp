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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentAddMemberBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
    }

    private fun setupTextListeners() {
        binding.etMemberName.doAfterTextChanged {
            viewModel.updateName(
                it?.toString().orEmpty()
            )
        }

        binding.etEmail.doAfterTextChanged {
            viewModel.updateEmail(
                it?.toString().orEmpty()
            )
        }

        binding.etPhone.doAfterTextChanged {
            viewModel.updatePhone(
                it?.toString().orEmpty()
            )
        }

    }

    private fun setupUpdateScreen() {
        val memberId = arguments?.getInt("memberId") ?: -1
        if (memberId != -1) {
            viewModel.loadMember(memberId)
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.uiState.collect { state ->
                    if (binding.etMemberName.text.toString() != state.name) {
                        binding.etMemberName.setText(state.name)
                    }

                    if (binding.etEmail.text.toString() != state.email) {
                        binding.etEmail.setText(state.email)
                    }

                    if (binding.etPhone.text.toString() != state.phoneNumber) {
                        binding.etPhone.setText(state.phoneNumber)
                    }

                    binding.loadingOverlay.isVisible = state.isLoading
                    binding.btnSaveMember.isEnabled = !state.isLoading

                    if (state.isEditMode) {
                        binding.tvTitle.text = getString(R.string.update_member)
                        binding.btnSaveMember.text = getString(R.string.update_member)
                    } else {
                        binding.tvTitle.text = getString(R.string.save_member)
                        binding.btnSaveMember.text = getString(R.string.save_member)
                    }

                    state.errorMessage?.let { message ->
                        Toast.makeText(
                            requireContext(),
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.clearError()

                    }
                    if (state.isMemberAdded) {
                        val message = if (state.isEditMode) {
                            "Member Updated Successfully"
                        } else {
                            "Member added successfully"
                        }

                        Toast.makeText(
                            requireContext(),
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                        viewModel.clearForm()
                        viewModel.resetSuccessState()

                        findNavController().popBackStack()
                    }

                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveMember.setOnClickListener {
            viewModel.saveMember()
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}