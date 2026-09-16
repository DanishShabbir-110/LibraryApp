package com.example.libraryapp.presentation.books.addbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.BookCategory
import com.example.libraryapp.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(private val repository: BookRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddBookEvents>()
    val events = _events.asSharedFlow()
    fun saveBook() {
        viewModelScope.launch {
            val state = _uiState.value
            val totalCopies = validateBook(state) ?: return@launch

            _uiState.update {
                it.copy(isLoading = true)
            }
            try {
                if (state.isEditMode)
                    updateBook(state, totalCopies)
                else
                    addBook(state, totalCopies)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false)
                }
                _events.emit(AddBookEvents.ShowError(e.message ?: "Failed to save book"))
            }
        }
    }

    fun loadBook(bookId: Int) {
        if(bookId<0) return
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(isLoading = true)
                }
                val book = repository.getBookById(bookId)
                if (book == null) {
                    _events.emit(AddBookEvents.ShowError("Book Not Found."))
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        id = book.id,
                        name = book.name,
                        author = book.author,
                        category = book.category.name,
                        totalCopies = book.totalCopies.toString(),
                        availableCopies = book.availableCopies,
                        isEditMode = true,
                        isLoading = false
                    )
                }
            } catch (ex: Exception) {
                _uiState.update {
                    it.copy(isLoading = false)
                }
                _events.emit(AddBookEvents.ShowError(ex.message ?: "Failed to load a book."))
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun updateAuthor(author: String) {
        _uiState.update {
            it.copy(author = author)
        }
    }

    fun updateCategory(category: String) {
        _uiState.update {
            it.copy(category = category)
        }
    }

    fun updateTotalCopies(totalCopies: String) {
        _uiState.update {
            it.copy(totalCopies = totalCopies)
        }
    }

    private suspend fun updateBook(state: AddBookUiState, totalCopies: Int) {
        val oldBook = repository.getBookById(state.id)
            ?: throw IllegalStateException("Book Not found.")
        val borrowCopies = oldBook.totalCopies - oldBook.availableCopies
        val selectedCategory = findCategory(state.category)
            ?: throw IllegalStateException("Please select a valid category.")
        if (totalCopies < borrowCopies) {
            throw IllegalStateException("Total copies cannot be less than borrowed copies")
        }
        val updateBook = Book(
            id = state.id,
            name = state.name.trim(),
            author = state.author.trim(),
            category = selectedCategory,
            totalCopies = totalCopies,
            availableCopies = totalCopies - borrowCopies
        )
        repository.updateBook(updateBook)
        _uiState.update {
            it.copy(isLoading = false)
        }
        _events.emit(AddBookEvents.BookUpdated)

    }

    private suspend fun addBook(state: AddBookUiState, totalCopies: Int) {
        val selectedCategory = findCategory(state.category)
            ?: throw IllegalStateException("Please select a valid category.")
        val book = Book(
            name = state.name.trim(),
            author = state.author.trim(),
            category = selectedCategory,
            totalCopies = totalCopies,
            availableCopies = totalCopies
        )
        repository.addBook(book)
        _uiState.update {
            it.copy(isLoading = false)
        }
        _events.emit(AddBookEvents.BookAdded)
    }

    private suspend fun validateBook(state: AddBookUiState): Int? {
        if (state.name.isBlank()) {
            _events.emit(AddBookEvents.ShowError("Book Name is Required."))
            return null
        }
        if (state.author.isBlank()) {
            _events.emit(AddBookEvents.ShowError("Author name is required."))
            return null
        }
        if (state.category.isBlank()) {
            _events.emit(AddBookEvents.ShowError("Select Category"))
            return null
        }
        val copies = state.totalCopies.toIntOrNull()
        if (copies == null || copies <= 0) {
            _events.emit(AddBookEvents.ShowError("Total copies must be greater than 0"))
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


data class AddBookUiState(
    val id: Int = 0,
    val name: String = "",
    val author: String = "",
    val category: String = "",
    val totalCopies: String = "",
    val availableCopies: Int = 0,
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false
)

sealed class AddBookEvents {
    data class ShowError(val message: String) : AddBookEvents()
    data object BookAdded : AddBookEvents()
    data object BookUpdated : AddBookEvents()
}