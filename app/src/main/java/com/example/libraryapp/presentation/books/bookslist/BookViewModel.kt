package com.example.libraryapp.presentation.books.bookslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    private var allBooks: List<Book> = emptyList()
    private var _uiState = MutableStateFlow(BookUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getBooks()
    }

    fun getBooks() {
        viewModelScope.launch {
            repository.getAllBooks().collectLatest { books ->

                allBooks = books
                _uiState.update {
                    it.copy(
                        books = books,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            try {
                repository.deleteBook(book)
            } catch (ex: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = ex.message ?: "Failed to delete a book"
                    )
                }
            }
        }
    }

    fun searchBooks(query: String) {

        val filteredBooks = if (query.isBlank()) {
            allBooks
        } else {
            allBooks.filter { book ->
                book.name.contains(query, ignoreCase = true) ||
                        book.author.contains(query, ignoreCase = true) ||
                        book.category.name.contains(query, ignoreCase = true)
            }
        }

        _uiState.update {
            it.copy(
                books = filteredBooks
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}

data class BookUiState(
    val books: List<Book> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)