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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                    books,
                    members,
                    borrowings
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
                returnDate = null,
                errorMessage = null
            )
        }

        updateDropdownData()
    }

    fun selectMember(member: Member) {
        _uiState.update {
            it.copy(
                selectedMember = member,
                selectedBook = null,
                errorMessage = null
            )
        }

        if (_uiState.value.mode == BookActionMode.RETURN) {
            updateDropdownData()
        }
    }

    fun selectBook(book: Book) {
        _uiState.update {
            it.copy(
                selectedBook = book,
                errorMessage = null
            )
        }
    }

    fun selectIssueDate(date: Long) {
        _uiState.update {
            it.copy(
                issueDate = date,
                errorMessage = null
            )
        }
    }

    fun selectReturnDate(date: Long) {
        _uiState.update {
            it.copy(
                returnDate = date,
                errorMessage = null
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
                        members = allMembers,
                        books = availableBooks
                    )
                }
            }

            BookActionMode.RETURN -> {

                val activeMemberIds = activeBorrowings
                    .map {
                        it.memberId
                    }
                    .toSet()

                val returnMembers = allMembers.filter {
                    it.id in activeMemberIds
                }

                val selectedMemberId = state.selectedMember?.id

                val activeBookIds = if (selectedMemberId != null) {
                    activeBorrowings
                        .filter {
                            it.memberId == selectedMemberId
                        }
                        .map {
                            it.bookId
                        }
                        .toSet()
                } else {
                    emptySet()
                }

                val returnBooks = allBooks.filter {
                    it.id in activeBookIds
                }

                _uiState.update {
                    it.copy(
                        members = returnMembers,
                        books = returnBooks
                    )
                }
            }
        }
    }

    fun issueBook() {
        val state = _uiState.value

        val member = state.selectedMember
        val book = state.selectedBook
        val issueDate = state.issueDate

        if (member == null) {
            showError("Please Select a member.")
            return
        }

        if (book == null) {
            showError("Please Select a book.")
            return
        }

        if (issueDate == null) {
            showError("Please Select Issue Date.")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }

                val activeBorrowBook =
                    borrowBookRepository.getActiveBorrowBook(
                        book.id,
                        member.id
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

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Book Issued Successfully"
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                            ?: "Failed to issue a book."
                    )
                }
            }
        }
    }

    fun returnBook() {
        val state = _uiState.value
        val member = state.selectedMember
        val book = state.selectedBook
        val returnDate = state.returnDate

        if (member == null) {
            showError("Please Select a member.")
            return
        }

        if (book == null) {
            showError("Please Select a book.")
            return
        }

        if (returnDate == null) {
            showError("Please Select Return Date.")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }

                val activeBorrowBook = borrowBookRepository.getActiveBorrowBook(book.id, member.id)
                    ?: throw IllegalStateException("No active borrowing found for this book and member.")

                borrowBookRepository.returnBook(activeBorrowBook.id, returnDate)

                bookRepository.increaseAvailableCopies(book.id)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "Book Returned Successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to return a book."
                    )
                }
            }
        }
    }
    private fun showError(message: String) {
        _uiState.update {
            it.copy(
                errorMessage = message
            )
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }

    fun clearSuccess() {
        _uiState.update {
            it.copy(
                isSuccess = false,
                successMessage = null
            )
        }
    }

    fun clearForm() {
        _uiState.update {
            it.copy(
                selectedMember = null,
                selectedBook = null,
                issueDate = null,
                returnDate = null,
                isLoading = false,
                isSuccess = false,
                successMessage = null,
                errorMessage = null
            )
        }

        updateDropdownData()
    }
}

enum class BookActionMode {
    ISSUE,
    RETURN
}

data class IssueReturnUiState(
    val mode: BookActionMode = BookActionMode.ISSUE,
    val members: List<Member> = emptyList(),
    val books: List<Book> = emptyList(),
    val selectedMember: Member? = null,
    val selectedBook: Book? = null,
    val issueDate: Long? = null,
    val returnDate: Long? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
