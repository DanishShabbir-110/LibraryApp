package com.example.libraryapp.presentation.dashboard

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.navigation.fragment.findNavController
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentDashboardBinding
import com.example.libraryapp.databinding.StatCardBinding
import com.example.libraryapp.presentation.base.BaseFragment
import com.example.libraryapp.utils.loadAndRepeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding, DashboardViewModel>(
    FragmentDashboardBinding::inflate,
    DashboardViewModel::class.java
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupStatCards()
        setupActionListeners()
        observeUiState()
    }

    private fun setupActionListeners() {
        binding.clIssueReturn.setOnClickListener {
            findNavController().navigate(R.id.issueReturnBookFragment)
        }
    }

    private fun observeUiState() {
        loadAndRepeatOnStarted {
            binding.apply {
                launch {
                    viewModel.totalBooks.collectLatest { totalBooks ->
                        cardTotalBooks.apply {
                            tvLabel.text = getString(R.string.total_books)
                            ivIcon.setImageResource(R.drawable.iv_books)
                            tvValue.text = totalBooks.toString()
                        }
                    }
                }
                launch {
                    viewModel.totalMembers.collectLatest { totalMembers ->
                        cardTotalMembers.apply {
                            tvLabel.text = getString(R.string.total_members)
                            ivIcon.setImageResource(R.drawable.iv_member)
                            tvValue.text = totalMembers.toString()
                        }
                    }
                }
                launch {
                    viewModel.issueBooks.collectLatest { issueBooks ->
                        cardBorrowedBooks.apply {
                            tvLabel.text = getString(R.string.borrowed_books)
                            ivIcon.setImageResource(R.drawable.iv_members)
                            tvValue.text = issueBooks.toString()
                        }
                    }
                }
                launch {
                    viewModel.availableCopies.collectLatest { availableCopies ->
                        cardAvailableBooks.apply {
                            tvLabel.text = getString(R.string.available_books)
                            ivIcon.setImageResource(R.drawable.iv_books)
                            tvValue.text = availableCopies.toString()
                        }
                    }
                }
            }
        }
    }
    private fun setupStatCard(
        card: StatCardBinding,
        label: String,
        icon: Int,
        backgroundColor: Int,
        tintColor: Int
    ) {
        card.apply {
            tvLabel.text = label
            ivIcon.setImageResource(icon)
            ivIcon.setBackgroundColor(
                ContextCompat.getColor(requireContext(), backgroundColor)
            )
            ImageViewCompat.setImageTintList(
                ivIcon,
                ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), tintColor)
                )
            )
        }
    }
    private fun setupStatCards() {
        setupStatCard(
            card = binding.cardTotalBooks,
            label = getString(R.string.total_books),
            icon = R.drawable.iv_books,
            backgroundColor = R.color.total_books_icon_background,
            tintColor = R.color.primary
        )

        setupStatCard(
            card = binding.cardAvailableBooks,
            label = getString(R.string.available_books),
            icon = R.drawable.iv_books,
            backgroundColor = R.color.available_books_icon_background,
            tintColor = R.color.success
        )

        setupStatCard(
            card = binding.cardBorrowedBooks,
            label = getString(R.string.borrowed_books),
            icon = R.drawable.iv_members,
            backgroundColor = R.color.borrowed_books_icon_background,
            tintColor = R.color.error
        )

        setupStatCard(
            card = binding.cardTotalMembers,
            label = getString(R.string.total_members),
            icon = R.drawable.iv_member,
            backgroundColor = R.color.total_members_icon_background,
            tintColor = R.color.primary
        )
    }
}