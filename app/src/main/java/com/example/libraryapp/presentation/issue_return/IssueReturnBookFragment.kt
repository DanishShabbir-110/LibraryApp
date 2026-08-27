package com.example.libraryapp.presentation.issue_return

//noinspection SuspiciousImport
import android.R
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.databinding.FragmentIssueReturnBookBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIssueReturnBookBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {
                viewModel.uiState.collect { state ->
                    binding.loadingOverlay.isVisible = state.isLoading
                    binding.btnIssueBook.isEnabled = !state.isLoading
                    binding.btnReturnBook.isEnabled = !state.isLoading

                    val memberNames = state.members.map { it.name }

                    val memberAdapter = ArrayAdapter(
                        requireContext(), R.layout.simple_dropdown_item_1line, memberNames
                    )

                    binding.actMember.setAdapter(memberAdapter)

                    binding.actMember.setOnItemClickListener { _, _, position, _ ->

                        val selectedMember = state.members[position]

                        viewModel.selectMember(selectedMember)
                    }

                    val bookNames = state.books.map {
                        it.name
                    }

                    val bookAdapter = ArrayAdapter(
                        requireContext(), R.layout.simple_dropdown_item_1line, bookNames
                    )

                    binding.actBook.setAdapter(bookAdapter)

                    binding.actBook.setOnItemClickListener { _, _, position, _ ->
                        val selectedBook = state.books[position]
                        viewModel.selectBook(selectedBook)
                    }

                    val selectedBookName = state.selectedBook?.name.orEmpty()
                    if (binding.actBook.text.toString() != selectedBookName) {
                        binding.actBook.setText(selectedBookName, false)
                    }

                    val selectedMemberName = state.selectedMember?.name.orEmpty()
                    if (binding.actMember.text.toString() != selectedMemberName) {
                        binding.actMember.setText(selectedMemberName, false)
                    }

                    val issueDateText = state.issueDate?.let {
                        formatDate(it)
                    }.orEmpty()

                    if (binding.etIssueDate.text.toString() != issueDateText) {
                        binding.etIssueDate.setText(issueDateText)
                    }

                    val returnDateText = state.returnDate?.let {
                        formatDate(it)
                    }.orEmpty()

                    if (binding.etReturnDate.text.toString() != returnDateText) {
                        binding.etReturnDate.setText(returnDateText)
                    }

                    state.errorMessage?.let { message ->

                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                        viewModel.clearError()
                    }

                    if (state.isSuccess) {

                        Toast.makeText(
                            requireContext(), state.successMessage, Toast.LENGTH_SHORT
                        ).show()
                        viewModel.clearForm()
                        val bundle = Bundle().apply { putBoolean("showBackArrow", true) }
                        findNavController().navigate(
                            com.example.libraryapp.R.id.nav_history,
                            bundle
                        )
                        viewModel.clearForm()
                        viewModel.clearSuccess()
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.etIssueDate.setOnClickListener {
            showDatePicker(DateType.ISSUEDATE)
        }

        binding.etReturnDate.setOnClickListener {
            showDatePicker(DateType.RETURNDATE)
        }

        binding.btnIssueBook.setOnClickListener {
            viewModel.issueBook()
        }

        binding.btnReturnBook.setOnClickListener {
            viewModel.returnBook()
        }

    }

    private fun showDatePicker(dateType: DateType) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->

                calendar.set(
                    year, month, dayOfMonth
                )
                if (dateType == DateType.ISSUEDATE) {
                    viewModel.selectIssueDate(
                        calendar.timeInMillis
                    )
                } else {
                    viewModel.selectReturnDate(
                        calendar.timeInMillis
                    )
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun formatDate(
        time: Long
    ): String {

        return dateFormat.format(
            Date(time)
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}