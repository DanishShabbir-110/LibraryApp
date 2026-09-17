package com.example.libraryapp.presentation.members.membersList

import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.MemberRepository
import com.example.libraryapp.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val repository: MemberRepository
) : BaseViewModel<MemberEvents>() {
    private var allMembers: List<Member> = emptyList()

    private var _members = MutableStateFlow<List<Member>>(emptyList())
    val members = _members.asStateFlow()

    private var _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadMembers()
    }

    private fun loadMembers() {
        viewModelScope.launch {
            repository.getAllMembers().catch { exception ->
                _isLoading.value = false
                sendEvent(MemberEvents.ShowError(exception.message ?: "Failed to load members"))
            }.collectLatest { members ->
                allMembers = members
                _members.value = members
                _isLoading.value = false
            }
        }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch {
            try {
                repository.deleteMember(member)
                sendEvent(MemberEvents.MemberDeleted)
            } catch (ex: Exception) {
                sendEvent(MemberEvents.ShowError(ex.message ?: "Failed to delete member"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMembers(query: String) {

        val filteredMembers = if (query.isBlank()) {
            allMembers
        } else {
            allMembers.filter { member ->
                member.name.contains(query, ignoreCase = true)
            }
        }
        _members.value = filteredMembers
    }
}


sealed class MemberEvents {
    data class ShowError(val message: String) : MemberEvents()
    data object MemberDeleted : MemberEvents()
}