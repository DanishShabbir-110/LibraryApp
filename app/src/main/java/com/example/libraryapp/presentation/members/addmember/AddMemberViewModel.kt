package com.example.libraryapp.presentation.members.addmember

import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.MemberRepository
import com.example.libraryapp.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMemberViewModel @Inject constructor(
    private val repository: MemberRepository
) : BaseViewModel<AddMemberEvents>() {
    private var memberId = 0

    private var _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    private var _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    private var _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()
    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private var _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    fun saveMember() {
        viewModelScope.launch {
            if (!validateMember()) return@launch
            _isLoading.value = true
            try {
                if (_isEditMode.value) {
                    updateMember()
                } else {
                    addMember()
                }
            } catch (e: Exception) {
                sendEvent(AddMemberEvents.ShowError(e.message ?: "Failed to save Member"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun addMember() {
        val member = Member(
            name = _name.value.trim(),
            email = _email.value.trim(),
            phone = _phoneNumber.value.trim()
        )
        repository.addMember(member)
        sendEvent(AddMemberEvents.MemberAdded)
    }

    private suspend fun updateMember() {
        val oldMember =
            repository.getMemberById(memberId) ?: throw IllegalStateException("Member not found.")

        val updateMember = Member(
            id = memberId,
            name = _name.value,
            email = _email.value,
            phone = _phoneNumber.value
        )
        repository.updateMember(updateMember)
        sendEvent(AddMemberEvents.MemberUpdated)
    }

    private suspend fun validateMember(): Boolean {
        if (_name.value.isBlank()) {
            sendEvent(AddMemberEvents.ShowError("Name is required."))
            return false
        }
        if (_email.value.isBlank()) {
            sendEvent(AddMemberEvents.ShowError("Email is required."))
            return false
        }
        if (_phoneNumber.value.isBlank()) {
            sendEvent(AddMemberEvents.ShowError("Phone Number is Required"))
            return false
        }
        return true
    }

    fun loadMember(id: Int) {
        if (id < 0) return
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val member = repository.getMemberById(id)
                if (member == null) {
                    sendEvent(AddMemberEvents.ShowError("Member not found."))
                    return@launch
                }
                memberId = member.id
                _name.value = member.name
                _email.value = member.email
                _phoneNumber.value = member.phone
                _isEditMode.value = true
                _isLoading.value = false
            } catch (ex: Exception) {
                sendEvent(AddMemberEvents.ShowError(ex.message ?: "Failed to load Member"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updatePhone(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }
}

sealed class AddMemberEvents {
    data class ShowError(val message: String) : AddMemberEvents()
    data object MemberAdded : AddMemberEvents()
    data object MemberUpdated : AddMemberEvents()
}