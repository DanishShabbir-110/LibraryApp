package com.example.libraryapp.presentation.books.addbook

//noinspection SuspiciousImport
import android.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.databinding.FragmentAddBookBinding
import com.example.libraryapp.domain.model.BookCategory
import com.example.libraryapp.presentation.base.BaseFragment
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddBookFragment : BaseFragment<FragmentAddBookBinding, AddBookViewModel>(
    FragmentAddBookBinding::inflate,
    AddBookViewModel::class.java
) {
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
            binding.apply {
                launch {
                    viewModel.name.collectLatest { name ->
                        if (etBookTitle.text.toString() != name) {
                            etBookTitle.setText(name)
                        }
                    }
                }
                launch {
                    viewModel.author.collectLatest { author ->
                        if (etAuthor.text.toString() != author) {
                            etAuthor.setText(author)
                        }
                    }
                    launch {

                    }
                }
                launch {
                    viewModel.category.collectLatest { category ->
                        if (actCategory.text.toString() != category) {
                            actCategory.setText(category, false)
                        }
                    }
                }
                launch {
                    viewModel.totalCopies.collectLatest { totalCopies ->
                        if (etTotalCopies.text.toString() != totalCopies) {
                            etTotalCopies.setText(totalCopies)
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        loadingOverlay.isVisible = isLoading
                        btnSaveBook.isEnabled = !isLoading
                    }
                }
                launch {
                    viewModel.isEditMode.collectLatest { isEditMode ->
                        if (isEditMode) {
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