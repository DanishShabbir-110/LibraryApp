package com.example.libraryapp.presentation.members.addmember

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMemberViewModel @Inject constructor(private val repository: MemberRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(AddMemberUiState())
    val uiState: StateFlow<AddMemberUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddMemberEvents>()
    val events = _events.asSharedFlow()


    fun saveMember() {
        val state = _uiState.value
        viewModelScope.launch {
            if (!validateMember(state)) return@launch
            _uiState.update {
                it.copy(isLoading = true)
            }
            try {
                if (state.isEditMode) {
                    updateMember(state)
                } else {
                    addMember(state)
                }
            } catch (e: Exception) {
                _events.emit(AddMemberEvents.ShowError(e.message ?: "Failed to save Member"))
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun addMember(state: AddMemberUiState) {
        val member = Member(
            name = state.name.trim(),
            email = state.email.trim(),
            phone = state.phoneNumber.trim()
        )
        repository.addMember(member)
        _events.emit(AddMemberEvents.MemberAdded)
    }

    private suspend fun updateMember(state: AddMemberUiState) {
        val oldMember =
            repository.getMemberById(state.id) ?: throw IllegalStateException("Member not found.")

        val updateMember = Member(
            id = state.id,
            name = state.name,
            email = state.email,
            phone = state.phoneNumber
        )
        repository.updateMember(updateMember)
        _events.emit(AddMemberEvents.MemberUpdated)
    }

    private suspend fun validateMember(state: AddMemberUiState): Boolean {
        if (state.name.isBlank()) {
            _events.emit(AddMemberEvents.ShowError("Name is required."))
            return false
        }
        if (state.email.isBlank()) {
            _events.emit(AddMemberEvents.ShowError("Email is required."))
            return false
        }
        if (state.phoneNumber.isBlank()) {
            _events.emit(AddMemberEvents.ShowError("Phone Number is Required"))
            return false
        }
        return true
    }

    fun loadMember(id: Int) {
        if (id < 0) return
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val member = repository.getMemberById(id)
                if (member == null) {
                    _events.emit(AddMemberEvents.ShowError("Member not found."))
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
                _events.emit(AddMemberEvents.ShowError(ex.message ?: "Failed to load Member"))
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
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
}

data class AddMemberUiState(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false
)

sealed class AddMemberEvents {
    data class ShowError(val message: String) : AddMemberEvents()
    data object MemberAdded : AddMemberEvents()
    data object MemberUpdated : AddMemberEvents()
}