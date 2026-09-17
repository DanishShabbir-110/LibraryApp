package com.example.libraryapp.presentation.books.addbook

import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.BookCategory
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val repository: BookRepository
) : BaseViewModel<AddBookEvents>() {
    private var bookId: Int = 0
    private var _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    private var _author = MutableStateFlow("")
    val author = _author.asStateFlow()

    private var _category = MutableStateFlow("")
    val category = _category.asStateFlow()
    private var _totalCopies = MutableStateFlow("")
    val totalCopies = _totalCopies.asStateFlow()

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    fun saveBook() {
        viewModelScope.launch {
            val totalCopies = validateBook() ?: return@launch
            _isLoading.value = true
            try {
                if (_isEditMode.value)
                    updateBook(totalCopies)
                else
                    addBook(totalCopies)
            } catch (e: Exception) {
                sendEvent(AddBookEvents.ShowError(e.message ?: "Failed to save book"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadBook(id: Int) {
        if (id < 0) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val book = repository.getBookById(id)
                if (book == null) {
                    sendEvent(AddBookEvents.ShowError("Book Not Found."))
                    return@launch
                }
                bookId = book.id
                _name.value = book.name
                _author.value = book.author
                _category.value = book.category.displayName()
                _totalCopies.value = book.totalCopies.toString()
                _isEditMode.value = true
            } catch (ex: Exception) {
                sendEvent(AddBookEvents.ShowError(ex.message ?: "Failed to load a book."))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun updateAuthor(author: String) {
        _author.value = author
    }

    fun updateCategory(category: String) {
        _category.value = category
    }

    fun updateTotalCopies(totalCopies: String) {
        _totalCopies.value = totalCopies
    }

    private suspend fun updateBook(totalCopies: Int) {
        val oldBook = repository.getBookById(bookId)
            ?: throw IllegalStateException("Book Not found.")
        val borrowCopies = oldBook.totalCopies - oldBook.availableCopies
        val selectedCategory = findCategory(_category.value)
            ?: throw IllegalStateException("Please select a valid category.")
        if (totalCopies < borrowCopies) {
            throw IllegalStateException("Total copies cannot be less than borrowed copies")
        }
        val updateBook = Book(
            id = bookId,
            name = _name.value.trim(),
            author = _author.value.trim(),
            category = selectedCategory,
            totalCopies = totalCopies,
            availableCopies = totalCopies - borrowCopies
        )
        repository.updateBook(updateBook)
        sendEvent(AddBookEvents.BookUpdated)
    }

    private suspend fun addBook(totalCopies: Int) {
        val selectedCategory = findCategory(_category.value)
            ?: throw IllegalStateException("Please select a valid category.")
        val book = Book(
            name = _name.value.trim(),
            author = _author.value.trim(),
            category = selectedCategory,
            totalCopies = totalCopies,
            availableCopies = totalCopies
        )
        repository.addBook(book)
        sendEvent(AddBookEvents.BookAdded)
    }

    private suspend fun validateBook(): Int? {
        if (_name.value.isBlank()) {
            sendEvent(AddBookEvents.ShowError("Book Name is Required."))
            return null
        }
        if (_author.value.isBlank()) {
            sendEvent(AddBookEvents.ShowError("Author name is required."))
            return null
        }
        if (_category.value.isBlank()) {
            sendEvent(AddBookEvents.ShowError("Select Category"))
            return null
        }
        val copies = _totalCopies.value.toIntOrNull()
        if (copies == null || copies <= 0) {
            sendEvent(AddBookEvents.ShowError("Total copies must be greater than 0"))
            return null
        }
        return copies
    }

    private fun findCategory(value: String): BookCategory? {
        return BookCategory.entries.find { category ->
            category.displayName().equals(
                value.trim(),
                ignoreCase = true
            ) || category.name.equals(
                value.trim(),
                ignoreCase = true
            )
        }
    }
}

sealed class AddBookEvents {
    data class ShowError(val message: String) : AddBookEvents()
    data object BookAdded : AddBookEvents()
    data object BookUpdated : AddBookEvents()
}