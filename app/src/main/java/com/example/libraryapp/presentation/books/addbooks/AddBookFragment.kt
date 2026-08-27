package com.example.libraryapp.presentation.books.addbooks

//noinspection SuspiciousImport
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
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.databinding.FragmentAddBookBinding
import com.example.libraryapp.domain.model.BookCategory
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
        setupTextListeners()
        setupUpdateScreen()
        observeUiState()
    }

    private fun setupTextListeners() {
        binding.etBookTitle.doAfterTextChanged {
            viewModel.updateName(
                it?.toString().orEmpty()
            )
        }

        binding.etAuthor.doAfterTextChanged {
            viewModel.updateAuthor(
                it?.toString().orEmpty()
            )
        }

        binding.actCategory.doAfterTextChanged {
            viewModel.updateCategory(
                it?.toString().orEmpty()
            )
        }

        binding.etTotalCopies.doAfterTextChanged {
            viewModel.updateTotalCopies(
                it?.toString().orEmpty()
            )
        }
    }

    private fun setupUpdateScreen() {
        val bookId = arguments?.getInt("bookId") ?: -1
        if (bookId != -1) {
            viewModel.loadBook(bookId)
        }
    }

    private fun setupDropDown() {
        val categoryList = BookCategory.entries.map {
            it.name.lowercase().replaceFirstChar { char -> char.uppercase() }
        }
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

                    if (binding.etTotalCopies.text.toString() != state.totalCopies) {
                        binding.etTotalCopies.setText(state.totalCopies)
                    }

                    binding.loadingOverlay.isVisible = state.isLoading
                    binding.btnSaveBook.isEnabled = !state.isLoading

                    if (state.isEditMode) {
                        binding.tvTitle.text =
                            getString(com.example.libraryapp.R.string.update_book)
                        binding.btnSaveBook.text =
                            getString(com.example.libraryapp.R.string.update_book)
                    } else {
                        binding.btnSaveBook.text =
                            getString(com.example.libraryapp.R.string.add_book)
                        binding.tvTitle.text = getString(com.example.libraryapp.R.string.add_book)
                    }

                    state.errorMessage?.let { message ->
                        Toast.makeText(
                            requireContext(),
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.clearError()

                    }
                    if (state.isBookAdded) {
                        val message = if (state.isEditMode) {
                            "Book Updated Successfully"
                        } else {
                            "Book added successfully"
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
        binding.btnSaveBook.setOnClickListener {
            viewModel.saveBook()
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}