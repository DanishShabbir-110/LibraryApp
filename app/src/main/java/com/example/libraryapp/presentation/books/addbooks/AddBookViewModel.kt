package com.example.libraryapp.presentation.books.addbooks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.usecases.books.AddBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddBookUiState()
    )
    val uiState: StateFlow<AddBookUiState> = _uiState.asStateFlow()

    fun addBook(
        name: String,
        author: String,
        category: String,
        totalCopies: Int
    ) {
        val state = _uiState.value
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
            _uiState.update {
                it.copy(errorMessage = "Total copies must be greater than 0")
            }
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
                val book = Book(
                    name = name.trim(),
                    author = author.trim(),
                    category = category.trim(),
                    totalCopies = totalCopies,
                    availableCopies = totalCopies
                )

                addBookUseCase(book)
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
                        errorMessage = e.message ?: "Failed to add book"
                    )
                }
            }

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