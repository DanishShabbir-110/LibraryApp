package com.example.libraryapp.presentation.issuereturn

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentIssueReturnBookBinding
import com.example.libraryapp.presentation.main.MainActivity
import com.example.libraryapp.utils.hide
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import com.example.libraryapp.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class IssueReturnBookFragment : Fragment() {

    private val viewModel: IssueReturnViewModel by viewModels()
    private var _binding: FragmentIssueReturnBookBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIssueReturnBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupToggleButton()
        setupClickListeners()
        observeUiState()
        observeUiEvents()
    }

    private fun observeUiEvents() {
        loadAndRepeatOnStarted {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is IssueReturnBookEvents.ErrorMessage -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    is IssueReturnBookEvents.SuccessMessage -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack(R.id.nav_home, false)
                        (requireActivity() as MainActivity).selectBottomTab(R.id.nav_history)
                    }
                }
            }
        }
    }

    private fun setupToggleButton() {
        binding.toggleAction.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) {
                return@addOnButtonCheckedListener
            }

            when (checkedId) {
                R.id.btnIssueMode -> viewModel.selectMode(BookActionMode.ISSUE)
                R.id.btnReturnMode -> viewModel.selectMode(BookActionMode.RETURN)
            }
        }
    }

    private fun observeUiState() {
        loadAndRepeatOnStarted {
            viewModel.uiState.collectLatest { state ->
                binding.apply {
                    loadingOverlay.isVisible = state.isLoading
                    btnSubmit.isEnabled = !state.isLoading

                    when (state.mode) {
                        BookActionMode.ISSUE -> {
                            if (toggleAction.checkedButtonId != R.id.btnIssueMode) {
                                toggleAction.check(R.id.btnIssueMode)
                            }

                            tvTitle.text = getString(R.string.issue_book)

                            btnSubmit.text = getString(R.string.issue_book)

                            tvIssueDateLabel.show()
                            tilIssueDate.show()

                            tvReturnDateLabel.hide()
                            tilReturnDate.hide()
                        }

                        BookActionMode.RETURN -> {
                            if (toggleAction.checkedButtonId != R.id.btnReturnMode) {
                                toggleAction.check(R.id.btnReturnMode)
                            }

                            tvTitle.text = getString(R.string.return_book)

                            btnSubmit.text = getString(R.string.return_book)

                            tvReturnDateLabel.show()
                            tilReturnDate.show()

                            tvIssueDateLabel.hide()
                            tilIssueDate.hide()
                        }
                    }

                    val memberNames = state.members.map { it.name }

                    val memberAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        memberNames
                    )
                    actMember.setAdapter(memberAdapter)
                    actMember.setOnItemClickListener { _, _, position, _ ->
                        val selectedMember = state.members[position]
                        viewModel.selectMember(selectedMember)
                    }


                    val bookNames = state.books.map { it.name }

                    val bookAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        bookNames
                    )

                    actBook.setAdapter(bookAdapter)
                    actBook.setOnItemClickListener { _, _, position, _ ->
                        val selectedBook = state.books[position]
                        viewModel.selectBook(selectedBook)
                    }


                    val selectedMemberName = state.selectedMember?.name.orEmpty()
                    if (actMember.text.toString() != selectedMemberName) {
                        actMember.setText(selectedMemberName, false)
                    }

                    val selectedBookName = state.selectedBook?.name.orEmpty()
                    if (actBook.text.toString() != selectedBookName) {
                        actBook.setText(selectedBookName, false)
                    }

                    val issueDateText = state.issueDate?.let { formatDate(it) }.orEmpty()
                    if (etIssueDate.text.toString() != issueDateText) {
                        etIssueDate.setText(issueDateText)
                    }

                    val returnDateText = state.returnDate?.let { formatDate(it) }.orEmpty()
                    if (etReturnDate.text.toString() != returnDateText) {
                        etReturnDate.setText(returnDateText)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            etIssueDate.setOnClickListener {
                showDatePicker(DateType.ISSUEDATE)
            }

            etReturnDate.setOnClickListener {
                showDatePicker(DateType.RETURNDATE)
            }

            btnSubmit.setOnClickListener {
                when (viewModel.uiState.value.mode) {
                    BookActionMode.ISSUE -> viewModel.issueBook()
                    BookActionMode.RETURN -> viewModel.returnBook()
                }
            }
        }
    }

    private fun showDatePicker(dateType: DateType) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                when (dateType) {
                    DateType.ISSUEDATE -> {
                        viewModel.selectIssueDate(calendar.timeInMillis)
                    }

                    DateType.RETURNDATE -> {
                        viewModel.selectReturnDate(calendar.timeInMillis)
                    }
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun formatDate(time: Long): String {
        return dateFormat.format(Date(time))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}