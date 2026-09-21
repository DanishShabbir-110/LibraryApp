package com.example.libraryapp.presentation.books.bookslist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentBooksBinding
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.presentation.base.BaseFragment
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BooksFragment : BaseFragment<FragmentBooksBinding, BookViewModel>(
    FragmentBooksBinding::inflate,
    BookViewModel::class.java
) {
    private lateinit var bookAdapter: BooksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeUiState()
        setupSearch()
        observeEvents()
    }

    private fun observeEvents() {
        loadAndRepeatOnStarted {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is BookEvents.ShowError -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }

                    BookEvents.BookDeleted -> {
                        Toast.makeText(
                            requireContext(),
                            "Book deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeUiState() {
        loadAndRepeatOnStarted {
            binding.apply {
                titleHeader.tvTitle.text="Books"
                searchBar.etSearch.hint = getString(R.string.search_books)
                launch {
                    viewModel.books.collectLatest { books ->
                        bookAdapter.submitList(books)
                        rvBooks.isVisible = books.isNotEmpty()
                    }
                }
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        loadingOverlay.root.isVisible = isLoading
                        loadingOverlay.tvLoadingText.text = "Loading Books..."
                    }
                }
                launch {
                    combine(viewModel.books, viewModel.isLoading) { books, isLoading ->
                        books.isEmpty() && !isLoading
                    }.collectLatest { showData ->
                        tvNoData.isVisible = showData
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        bookAdapter = BooksAdapter(
            onDeleteClick = { book ->
                showDeleteDialog(book)
            },
            onUpdateClick = { book ->
                val action = BooksFragmentDirections.bookFragmentToAddBookFragment(
                    bookId = book.id
                )
                findNavController().navigate(action)
            }
        )

        binding.rvBooks.apply {
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun showDeleteDialog(book: Book) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_book))
            .setMessage(getString(R.string.are_you_sure_want_to_delete_this_book))
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteBook(book)
            }.show()
    }

    fun setupClickListeners() {
        binding.fabAddBook.setOnClickListener {
            findNavController().navigate(R.id.addBookFragment)
        }
    }

    private fun setupSearch() {
        binding.searchBar.etSearch.doAfterTextChanged { editable ->
            viewModel.searchBooks(editable?.toString().orEmpty())
        }
    }
}


