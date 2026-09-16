package com.example.libraryapp.presentation.books.addbook

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
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.databinding.FragmentAddBookBinding
import com.example.libraryapp.domain.model.BookCategory
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class AddBookFragment : Fragment() {
    private val viewModel: AddBookViewModel by viewModels()
    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropDown()
        setupClickListeners()
        setupTextListeners()
        setupUpdateScreen()
        observeUiState()
        observeEvents()
    }

    private fun observeEvents() {
        loadAndRepeatOnStarted {
            viewModel.events.collectLatest { event ->
                when (event) {
                    AddBookEvents.BookAdded -> {
                        Toast.makeText(
                            requireContext(),
                            "Book added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }

                    AddBookEvents.BookUpdated -> {
                        Toast.makeText(
                            requireContext(),
                            "Book updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController().popBackStack()
                    }
                    is AddBookEvents.ShowError ->
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupTextListeners() {
        binding.apply {

            etBookTitle.doAfterTextChanged {
                viewModel.updateName(it?.toString().orEmpty())
            }

            etAuthor.doAfterTextChanged {
                viewModel.updateAuthor(it?.toString().orEmpty())
            }

            actCategory.doAfterTextChanged {
                viewModel.updateCategory(it?.toString().orEmpty())
            }

            etTotalCopies.doAfterTextChanged {
                viewModel.updateTotalCopies(it?.toString().orEmpty())
            }
        }
    }

    private fun setupUpdateScreen() {
        val bookId = arguments?.getInt("bookId") ?: -1
        viewModel.loadBook(bookId)
    }

    private fun setupDropDown() {
        val categoryList = BookCategory.entries.map {
            it.displayName()
        }
        val adapter =
            ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, categoryList)
        binding.actCategory.setAdapter(adapter)
    }

    private fun observeUiState() {
        loadAndRepeatOnStarted {
            viewModel.uiState.collectLatest { state ->
                binding.apply {
                    if (etBookTitle.text.toString() != state.name) {
                        etBookTitle.setText(state.name)
                    }

                    if (etAuthor.text.toString() != state.author) {
                        etAuthor.setText(state.author)
                    }

                    if (actCategory.text.toString() != state.category) {
                        actCategory.setText(state.category,false)
                    }

                    if (etTotalCopies.text.toString() != state.totalCopies) {
                        etTotalCopies.setText(state.totalCopies)
                    }

                    loadingOverlay.isVisible = state.isLoading
                    btnSaveBook.isEnabled = !state.isLoading

                    if (state.isEditMode) {
                        tvTitle.text = getString(com.example.libraryapp.R.string.update_book)
                        btnSaveBook.text = getString(com.example.libraryapp.R.string.update_book)
                    } else {
                        btnSaveBook.text = getString(com.example.libraryapp.R.string.add_book)
                        tvTitle.text = getString(com.example.libraryapp.R.string.add_book)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSaveBook.setOnClickListener {
                viewModel.saveBook()
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}