package com.example.libraryapp.presentation.members.addmember

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.usecases.members.AddMemberUseCase
import com.example.libraryapp.domain.usecases.members.GetMemberByIdUseCase
import com.example.libraryapp.domain.usecases.members.UpdateMemberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMemberViewModel @Inject constructor(
    private val addMemberUseCase: AddMemberUseCase,
    private val getMemberByIdUseCase: GetMemberByIdUseCase,
    private val updateMemberUseCase: UpdateMemberUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        AddMemberUiState()
    )
    val uiState: StateFlow<AddMemberUiState> = _uiState.asStateFlow()

    fun saveMember() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Name is required.")
            }
            return
        }
        if (state.email.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Email is required.")
            }
            return
        }
        if (state.phoneNumber.isBlank()) {
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
                if (state.isEditMode) {
                    val oldMember = getMemberByIdUseCase(state.id)
                    if (oldMember == null) {
                        showError("Not Member found.")
                    }

                    val updateMember = Member(
                        id = state.id,
                        name = state.name,
                        email = state.email,
                        phone = state.phoneNumber
                    )
                    updateMemberUseCase(updateMember)
                } else {
                    val member = Member(
                        name = state.name.trim(),
                        email = state.email.trim(),
                        phone = state.phoneNumber.trim()
                    )

                    addMemberUseCase(member)
                }
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
                        errorMessage = e.message ?: "Failed to save Member"
                    )
                }
            }
        }
    }

    fun loadMember(id: Int) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(isLoading = true)
                }
                val member = getMemberByIdUseCase(id)
                if (member == null) {
                    showError("Member not found.")
                    return@launch
                }
                _uiState.update {
                    it.copy(
                        id = member.id,
                        name = member.name,
                        email = member.email,
                        phoneNumber = member.phone,
                        isEditMode = true,
                        isLoading = false
                    )
                }
            } catch (ex: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = ex.message ?: "Failed to load Member")
                }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    fun updatePhone(phoneNumber: String) {
        _uiState.update {
            it.copy(phoneNumber = phoneNumber)
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

    private fun showError(message: String) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
    }
}