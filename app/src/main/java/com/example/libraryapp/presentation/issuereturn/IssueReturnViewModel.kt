package com.example.libraryapp.presentation.issuereturn

import androidx.lifecycle.viewModelScope
import com.example.libraryapp.domain.model.Book
import com.example.libraryapp.domain.model.BorrowBook
import com.example.libraryapp.domain.model.BorrowingStatus
import com.example.libraryapp.domain.model.Member
import com.example.libraryapp.domain.repository.BookRepository
import com.example.libraryapp.domain.repository.BorrowBookRepository
import com.example.libraryapp.domain.repository.MemberRepository
import com.example.libraryapp.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class IssueReturnViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val memberRepository: MemberRepository,
    private val borrowBookRepository: BorrowBookRepository
) : BaseViewModel<IssueReturnBookEvents>() {
    private var allBooks: List<Book> = emptyList()
    private var allMembers: List<Member> = emptyList()
    private var activeBorrowings: List<BorrowBook> = emptyList()

    private var _mode = MutableStateFlow(BookActionMode.ISSUE)
    val mode = _mode.asStateFlow()
    private var _members = MutableStateFlow<List<Member>>(emptyList())
    val members = _members.asStateFlow()
    private var _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books.asStateFlow()
    private var _selectedMember = MutableStateFlow<Member?>(null)
    val selectedMember = _selectedMember.asStateFlow()
    private var _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook = _selectedBook.asStateFlow()
    private var _issueDate = MutableStateFlow<Long?>(null)
    val issueDate = _issueDate.asStateFlow()
    private var _returnDate = MutableStateFlow<Long?>(null)
    val returnDate = _returnDate.asStateFlow()
    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

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
        _mode.value = mode
        _selectedMember.value = null
        _selectedBook.value = null
        _issueDate.value = null
        _returnDate.value = null
        updateDropdownData()
    }

    fun selectMember(member: Member) {
        _selectedMember.value = member
        _selectedBook.value = null
        if (_mode.value == BookActionMode.RETURN) {
            updateDropdownData()
        }
    }

    fun selectBook(book: Book) {
        _selectedBook.value = book
    }

    fun selectIssueDate(date: Long) {
        _issueDate.value = date
    }

    fun selectReturnDate(date: Long) {
        _returnDate.value = date
    }

    private fun updateDropdownData() {
        when (_mode.value) {

            BookActionMode.ISSUE -> {

                val availableBooks = allBooks.filter {
                    it.availableCopies > 0
                }
                _members.value = allMembers
                _books.value = availableBooks
            }

            BookActionMode.RETURN -> {

                val activeMemberIds = activeBorrowings.map {
                    it.memberId
                }.toSet()

                val returnMembers = allMembers.filter {
                    it.id in activeMemberIds
                }

                val selectedMemberId = _selectedMember.value?.id

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
                _members.value = returnMembers
                _books.value = returnBooks
            }
        }
    }

    fun issueBook() {
        viewModelScope.launch {
            val (member, book) = validateTheSelection() ?: return@launch
            val issueDate = _issueDate.value

            if (issueDate == null) {
                sendEvent(IssueReturnBookEvents.ErrorMessage("Please Select Issue Date."))
                return@launch
            }

            try {
                _isLoading.value = true

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

                sendEvent(IssueReturnBookEvents.SuccessMessage("Book Issued Successfully"))

            } catch (e: Exception) {
                sendEvent(
                    IssueReturnBookEvents.ErrorMessage(
                        e.message ?: "Failed to issue a book."
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun returnBook() {
        viewModelScope.launch {
            val (member, book) = validateTheSelection() ?: return@launch
            val returnDate = _returnDate.value

            if (returnDate == null) {
                sendEvent(IssueReturnBookEvents.ErrorMessage("Please Select Return Date."))
                return@launch
            }

            try {
                _isLoading.value = true

                val activeBorrowBook = borrowBookRepository.getActiveBorrowBook(book.id, member.id)
                    ?: throw IllegalStateException("No active borrowing found for this book and member.")

                borrowBookRepository.returnBook(activeBorrowBook.id, returnDate)

                bookRepository.increaseAvailableCopies(book.id)

                sendEvent(IssueReturnBookEvents.SuccessMessage("Book Returned Successfully"))
            } catch (e: Exception) {
                sendEvent(
                    IssueReturnBookEvents.ErrorMessage(
                        e.message ?: "Failed to return a book."
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun validateTheSelection(): Pair<Member, Book>? {
        val member = _selectedMember.value
        val book = _selectedBook.value

        if (member == null) {
            sendEvent(IssueReturnBookEvents.ErrorMessage("Please Select a member."))
            return null
        }

        if (book == null) {
            sendEvent(IssueReturnBookEvents.ErrorMessage("Please Select a book."))
            return null
        }

        return member to book
    }
}

enum class BookActionMode {
    ISSUE, RETURN
}

sealed class IssueReturnBookEvents {
    data class SuccessMessage(val message: String) : IssueReturnBookEvents()
    data class ErrorMessage(val message: String) : IssueReturnBookEvents()
}
