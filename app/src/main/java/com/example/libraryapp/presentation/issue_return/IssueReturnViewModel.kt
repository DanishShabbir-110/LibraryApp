package com.example.libraryapp.presentation.issue_return

import androidx.lifecycle.ViewModel
import com.example.libraryapp.domain.cases.borrowbooks.IssueBookUseCase
import com.example.libraryapp.domain.cases.borrowbooks.ReturnBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class IssueReturnViewModel @Inject constructor(
    private val issueBookUseCase: IssueBookUseCase,
    private val returnBookUseCase: ReturnBookUseCase
) : ViewModel() {
    private var _uiState = MutableStateFlow(IssueReturnUiState())
    val uiState: StateFlow<IssueReturnUiState> = _uiState.asStateFlow()


}