package com.example.libraryapp.presentation.books.bookslist

import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : BaseViewModel<BookEvents>() {
    private var allBooks: List<Book> = emptyList()
    private var _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books.asStateFlow()
    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        getBooks()
    }

    fun getBooks() {
        viewModelScope.launch {
            repository.getAllBooks().collectLatest { books ->
                allBooks = books
                _books.value = books
                _isLoading.value = false
            }
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            try {
                repository.deleteBook(book)
                sendEvent(BookEvents.BookDeleted)
            } catch (ex: Exception) {
                sendEvent(BookEvents.ShowError(ex.message ?: "Failed to delete a book"))
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
        _books.value = filteredBooks
    }
}

sealed class BookEvents {
    data class ShowError(val message: String) : BookEvents()
    data object BookDeleted : BookEvents()
}