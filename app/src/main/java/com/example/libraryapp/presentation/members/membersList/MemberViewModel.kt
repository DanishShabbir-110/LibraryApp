package com.example.libraryapp.presentation.members.membersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        loadMembers()
    }

    private fun loadMembers() {
        viewModelScope.launch {
            repository.getAllMembers().catch { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load members"
                    )
                }
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
            } catch (ex: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = ex.message ?: "Failed to delete member."
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                errorMessage = null
            )
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
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)