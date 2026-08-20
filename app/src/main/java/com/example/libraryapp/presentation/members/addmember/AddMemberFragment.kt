package com.example.libraryapp.presentation.members.addmember

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentAddMemberBinding
import com.example.libraryapp.presentation.books.addbooks.AddBookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.getValue
@AndroidEntryPoint
class AddMemberFragment : Fragment() {
    private val viewModel: AddMemberViewModel by viewModels()

    private var _binding: FragmentAddMemberBinding?=null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentAddMemberBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeUiState()
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

                    state.errorMessage?.let { message ->
                        Toast.makeText(
                            requireContext(),
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.clearError()

                    }
                    if (state.isMemberAdded) {

                        Toast.makeText(
                            requireContext(),
                            "Member added successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        viewModel.clearForm()
                        viewModel.resetSuccessState()

                        parentFragmentManager.popBackStack()
                    }

                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveMember.setOnClickListener {
            val name = binding.etMemberName.text
                .toString()
                .trim()

            val email = binding.etEmail.text
                .toString()
                .trim()

            val phone = binding.etMemberName.text
                .toString()
                .trim()

            viewModel.addMember(
                name = name,
                email = email,
                phone=phone
            )
        }
        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}