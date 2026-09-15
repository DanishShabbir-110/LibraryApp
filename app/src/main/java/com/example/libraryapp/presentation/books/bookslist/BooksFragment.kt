package com.example.libraryapp.presentation.books.bookslist

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentBooksBinding
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BooksFragment : Fragment() {
    private val viewModel: BookViewModel by viewModels()
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BooksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(
            inflater,
            container,
            false
        )


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeUiState()
        setupSearch()

    }

    private fun observeUiState() {
        loadAndRepeatOnStarted {
            viewModel.uiState.collect { state ->
                bookAdapter.submitList(state.books)
                binding.apply {
                    loadingOverlay.isVisible = state.isLoading
                    tvNoData.isVisible = state.books.isEmpty() && !state.isLoading
                    rvBooks.isVisible = state.books.isNotEmpty()
                }
                state.errorMessage?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
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
                val bundle = Bundle().apply {
                    putInt("bookId", book.id)
                }
                findNavController().navigate(R.id.addBookFragment, bundle)
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

        binding.etSearchBooks.doAfterTextChanged { editable ->

            viewModel.searchBooks(
                editable?.toString().orEmpty()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


