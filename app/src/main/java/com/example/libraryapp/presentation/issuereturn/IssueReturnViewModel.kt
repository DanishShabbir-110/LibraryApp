package com.example.libraryapp.presentation.issuereturn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.BorrowBook
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import com.example.libraryapp.domain.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class IssueReturnViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val memberRepository: MemberRepository,
    private val borrowBookRepository: BorrowBookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IssueReturnUiState())
    val uiState: StateFlow<IssueReturnUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<IssueReturnBookEvents>()
    val events = _events.asSharedFlow()

    private var allBooks: List<Book> = emptyList()
    private var allMembers: List<Member> = emptyList()
    private var activeBorrowings: List<BorrowBook> = emptyList()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                bookRepository.getAllBooks(),
                memberRepository.getAllMembers(),
                borrowBookRepository.getAllBorrowBooksByStatus(BorrowingStatus.ISSUED)
            ) { books, members, borrowings ->
                Triple(
                    books, members, borrowings
                )
            }.collectLatest { (books, members, borrowings) ->

                allBooks = books
                allMembers = members
                activeBorrowings = borrowings

                updateDropdownData()
            }
        }
    }

    fun selectMode(mode: BookActionMode) {
        _uiState.update {
            it.copy(
                mode = mode,
                selectedMember = null,
                selectedBook = null,
                issueDate = null,
                returnDate = null
            )
        }

        updateDropdownData()
    }

    fun selectMember(member: Member) {
        _uiState.update {
            it.copy(
                selectedMember = member, selectedBook = null
            )
        }

        if (_uiState.value.mode == BookActionMode.RETURN) {
            updateDropdownData()
        }
    }

    fun selectBook(book: Book) {
        _uiState.update {
            it.copy(
                selectedBook = book
            )
        }
    }

    fun selectIssueDate(date: Long) {
        _uiState.update {
            it.copy(
                issueDate = date
            )
        }
    }

    fun selectReturnDate(date: Long) {
        _uiState.update {
            it.copy(
                returnDate = date
            )
        }
    }

    private fun updateDropdownData() {
        val state = _uiState.value

        when (state.mode) {

            BookActionMode.ISSUE -> {

                val availableBooks = allBooks.filter {
                    it.availableCopies > 0
                }

                _uiState.update {
                    it.copy(
                        members = allMembers, books = availableBooks
                    )
                }
            }

            BookActionMode.RETURN -> {

                val activeMemberIds = activeBorrowings.map {
                    it.memberId
                }.toSet()

                val returnMembers = allMembers.filter {
                    it.id in activeMemberIds
                }

                val selectedMemberId = state.selectedMember?.id

                val activeBookIds = if (selectedMemberId != null) {
                    activeBorrowings.filter {
                        it.memberId == selectedMemberId
                    }.map {
                        it.bookId
                    }.toSet()
                } else {
                    emptySet()
                }
                val returnBooks = allBooks.filter {
                    it.id in activeBookIds
                }
                _uiState.update {
                    it.copy(
                        members = returnMembers, books = returnBooks
                    )
                }
            }
        }
    }

    fun issueBook() {
        viewModelScope.launch {
            val state = _uiState.value

            val (member, book) = validateTheSelection(state) ?: return@launch
            val issueDate = state.issueDate

            if (issueDate == null) {
                _events.emit(IssueReturnBookEvents.ErrorMessage("Please Select Issue Date."))
                return@launch
            }

            try {
                _uiState.update {
                    it.copy(isLoading = true)
                }

                val activeBorrowBook = borrowBookRepository.getActiveBorrowBook(
                    book.id, member.id
                )

                if (activeBorrowBook != null) {
                    throw IllegalStateException("Book Already issued to that person.")
                }

                if (book.availableCopies <= 0) {
                    throw IllegalStateException("No copies available.")
                }

                val borrowBook = BorrowBook(
                    bookId = book.id,
                    memberId = member.id,
                    issueDate = issueDate,
                    memberName = member.name,
                    bookName = book.name,
                    returnDate = null,
                    status = BorrowingStatus.ISSUED
                )

                borrowBookRepository.issueBook(borrowBook)

                bookRepository.decreaseAvailableCopies(book.id)

                _events.emit(IssueReturnBookEvents.SuccessMessage("Book Issued Successfully"))

            } catch (e: Exception) {
                _events.emit(
                    IssueReturnBookEvents.ErrorMessage(
                        e.message ?: "Failed to issue a book."
                    )
                )
            } finally {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun returnBook() {
        viewModelScope.launch {
            val state = _uiState.value
            val (member, book) = validateTheSelection(state) ?: return@launch
            val returnDate = state.returnDate

            if (returnDate == null) {
                _events.emit(IssueReturnBookEvents.ErrorMessage("Please Select Return Date."))
                return@launch
            }

            try {
                _uiState.update {
                    it.copy(isLoading = true)
                }

                val activeBorrowBook = borrowBookRepository.getActiveBorrowBook(book.id, member.id)
                    ?: throw IllegalStateException("No active borrowing found for this book and member.")

                borrowBookRepository.returnBook(activeBorrowBook.id, returnDate)

                bookRepository.increaseAvailableCopies(book.id)

                _events.emit(IssueReturnBookEvents.SuccessMessage("Book Returned Successfully"))
            } catch (e: Exception) {
                _events.emit(
                    IssueReturnBookEvents.ErrorMessage(
                        e.message ?: "Failed to return a book."
                    )
                )
            }finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun validateTheSelection(state: IssueReturnUiState): Pair<Member, Book>? {
        val member = state.selectedMember
        val book = state.selectedBook

        if (member == null) {
            _events.emit(IssueReturnBookEvents.ErrorMessage("Please Select a member."))
            return null
        }

        if (book == null) {
            _events.emit(IssueReturnBookEvents.ErrorMessage("Please Select a book."))
            return null
        }

        return member to book
    }
}

enum class BookActionMode {
    ISSUE, RETURN
}

data class IssueReturnUiState(
    val mode: BookActionMode = BookActionMode.ISSUE,
    val members: List<Member> = emptyList(),
    val books: List<Book> = emptyList(),
    val selectedMember: Member? = null,
    val selectedBook: Book? = null,
    val issueDate: Long? = null,
    val returnDate: Long? = null,
    val isLoading: Boolean = false
)

sealed class IssueReturnBookEvents {
    data class SuccessMessage(val message: String) : IssueReturnBookEvents()
    data class ErrorMessage(val message: String) : IssueReturnBookEvents()
}
