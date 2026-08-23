package com.example.libraryapp.presentation.books.addbooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.usecases.books.AddBookUseCase
import com.example.libraryapp.domain.usecases.books.GetBookByIdUseCase
import com.example.libraryapp.domain.usecases.books.UpdateBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddBookUiState()
    )
    val uiState: StateFlow<AddBookUiState> = _uiState.asStateFlow()

    fun saveBook() {
        val state=_uiState.value
        if (state.name.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Book name is required.")
            }
            return
        }
        if (state.author.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Author name is required.")
            }
            return
        }
        if (state.category.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Select Category")
            }
            return
        }
        val copies = state.totalCopies.toIntOrNull()
        if (copies == null || copies <= 0) {
            showError("Total copies must be greater than 0")
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            try {
                if(state.isEditMode){
                    val oldBook=getBookByIdUseCase(state.id)?:throw IllegalStateException("Book Not found.")

                    val borrowCopies=oldBook.totalCopies-oldBook.availableCopies

                    if(copies < borrowCopies){
                        throw IllegalStateException("Total copies cannot be less than borrowed copies")
                    }

                    val updateBook=Book(
                        id = state.id,
                        name = state.name.trim(),
                        author = state.author.trim(),
                        category = state.category.trim(),
                        totalCopies = copies,
                        availableCopies = copies - borrowCopies
                    )
                    updateBookUseCase(updateBook)
                }else{

                    val book = Book(
                        name = state.name.trim(),
                        author = state.author.trim(),
                        category = state.category.trim(),
                        totalCopies = copies,
                        availableCopies = copies
                    )

                    addBookUseCase(book)
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isBookAdded = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to save book"
                    )
                }
            }

        }
    }

    fun loadBook(bookId: Int) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(isLoading = true)
                }
                val book = getBookByIdUseCase(bookId)
                if (book == null) {
                    showError("Book Not Found.")
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        id = book.id,
                        name = book.name,
                        author = book.author,
                        category = book.category,
                        totalCopies = book.totalCopies.toString(),
                        availableCopies = book.availableCopies,
                        isEditMode = true,
                        isLoading = false
                    )
                }
            } catch (ex: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Failed to load a book."
                    )
                }
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
    private fun showError(message: String) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun resetSuccessState() {
        _uiState.update {
            it.copy(isBookAdded = false)
        }
    }

    fun clearForm() {
        _uiState.value = AddBookUiState()
    }
}