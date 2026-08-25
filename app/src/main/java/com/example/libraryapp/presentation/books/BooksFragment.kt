package com.example.libraryapp.presentation.books

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentBooksBinding
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.presentation.AppMenuFragment
import com.example.libraryapp.presentation.books.addbooks.AddBookFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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
        setupBackArrow()
        observeUiState()
        setupSearch()

    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    bookAdapter.submitList(state.books)
                    binding.loadingOverlay.isVisible = state.isLoading
                    binding.tvNoData.isVisible = state.books.isEmpty() && !state.isLoading
                    binding.rvBooks.isVisible = state.books.isNotEmpty()
                    state.errorMessage?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
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
                val bundle = Bundle().apply {
                    putInt("bookId", book.id)
                }
                findNavController().navigate(R.id.addBookFragment,bundle)
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
            .setTitle("Delete Book")
            .setMessage("Are you sure want to delete this book.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteBook(book)
            }.show()
    }

    fun setupClickListeners() {

        binding.fabAddBook.setOnClickListener {
            findNavController().navigate(R.id.addBookFragment)
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSearch() {

        binding.etSearchBooks.doAfterTextChanged { editable ->

            viewModel.searchBooks(
                editable?.toString().orEmpty()
            )
        }
    }

    private fun setupBackArrow() {

        val showBackArrow =
            arguments?.getBoolean("showBackArrow", false) ?: false

        binding.ivBack.isVisible = showBackArrow
    }
}