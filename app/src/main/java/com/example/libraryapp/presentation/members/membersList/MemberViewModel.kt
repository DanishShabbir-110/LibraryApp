package com.example.libraryapp.presentation.members.membersList

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor( private val repository: MemberRepository) : ViewModel() {
    private var allMembers: List<Member> = emptyList()
    private var _uiState = MutableStateFlow(MemberUiState())
    val uiState: StateFlow<MemberUiState> = _uiState.asStateFlow()

    private val _events= MutableSharedFlow<MemberEvents>()
    val events= _events.asSharedFlow()


    init {
        loadMembers()
    }

    private fun loadMembers() {
        viewModelScope.launch {
            repository.getAllMembers().catch { exception ->
                _uiState.update {
                    it.copy(isLoading = false)
                }
                _events.emit(MemberEvents.ShowError(exception.message ?: "Failed to load members"))
            }.collectLatest { members ->
                allMembers = members
                _uiState.update {
                    it.copy(
                        members = members,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch {
            try {
                repository.deleteMember(member)
                _events.emit(MemberEvents.MemberDeleted)
            } catch (ex: Exception) {
                _events.emit(MemberEvents.ShowError(ex.message ?: "Failed to delete member"))
            }finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun searchMembers(query: String) {

        val filteredBooks = if (query.isBlank()) {
            allMembers
        } else {
            allMembers.filter { member ->
                member.name.contains(query, ignoreCase = true)
            }
        }
        _uiState.update {
            it.copy(
                members = filteredBooks
            )
        }
    }
}

data class MemberUiState(
    val members: List<Member> = emptyList(),
    val isLoading: Boolean = true
)

sealed class MemberEvents{
    data class ShowError(val message:String): MemberEvents()
    data object MemberDeleted: MemberEvents()
}