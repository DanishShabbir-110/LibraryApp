package com.example.libraryapp.presentation.books.addbooks

import android.R
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
import com.example.libraryapp.databinding.FragmentAddBookBinding
import com.example.libraryapp.domain.model.Book
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddBookFragment : Fragment() {
    private val viewModel: AddBookViewModel by viewModels()
    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropDown()
        setupClickListeners()
        observeUiState()
    }

    private fun setupDropDown() {
        val categoryList = listOf("Programing", "Database", "Research")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, categoryList)
        binding.actCategory.setAdapter(adapter)
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.uiState.collect { state ->
                    if (binding.etBookTitle.text.toString() != state.name) {
                        binding.etBookTitle.setText(state.name)
                    }

                    if (binding.etAuthor.text.toString() != state.author) {
                        binding.etAuthor.setText(state.author)
                    }

                    if (binding.actCategory.text.toString() != state.category) {
                        binding.actCategory.setText(state.category)
                    }

                    if (binding.etTotalCopies.text.toString() != state.totalCopies.toString()) {
                        binding.etTotalCopies.setText(state.totalCopies)
                    }

                    binding.loadingOverlay.isVisible = state.isLoading
                    binding.btnSaveBook.isEnabled = !state.isLoading

                    state.errorMessage?.let { message ->
                        Toast.makeText(
                            requireContext(),
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.clearError()

                    }
                    if (state.isBookAdded) {

                        Toast.makeText(
                            requireContext(),
                            "Book added successfully",
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
        binding.btnSaveBook.setOnClickListener {
            val name = binding.etBookTitle.text
                .toString()
                .trim()

            val author = binding.etAuthor.text
                .toString()
                .trim()

            val category = binding.actCategory.text
                .toString()
                .trim()

            val totalCopies = binding.etTotalCopies.text
                .toString()
                .toIntOrNull() ?: 0

            viewModel.addBook(
                name = name,
                author = author,
                category = category,
                totalCopies = totalCopies
            )
        }
        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

}