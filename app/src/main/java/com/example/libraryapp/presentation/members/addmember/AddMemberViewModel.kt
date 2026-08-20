package com.example.libraryapp.presentation.members.addmember

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.usecases.members.AddMemberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMemberViewModel @Inject constructor(
    private val addMemberUseCase: AddMemberUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AddMemberUiState()
    )
    val uiState: StateFlow<AddMemberUiState> = _uiState.asStateFlow()

    fun addMember(
        name: String,
        email: String,
        phone: String
    ) {
        if (name.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Name is required.")
            }
            return
        }
        if (email.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Email is required.")
            }
            return
        }
        if (phone.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Phone Number is Required")
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
                val member = Member(
                    name = name.trim(),
                    email = email.trim(),
                    phone = phone.trim()
                )

                addMemberUseCase(member)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isMemberAdded = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to add Member"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun resetSuccessState() {
        _uiState.update {
            it.copy(isMemberAdded = false)
        }
    }

    fun clearForm() {
        _uiState.value = AddMemberUiState()
    }
}