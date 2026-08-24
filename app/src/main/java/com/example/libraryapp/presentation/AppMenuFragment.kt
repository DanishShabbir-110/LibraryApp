package com.example.libraryapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentAppmenuBinding
import com.example.libraryapp.presentation.books.BooksFragment
import com.example.libraryapp.presentation.dashboard.DashboardFragment
import com.example.libraryapp.presentation.history.BorrowingHistoryFragment
import com.example.libraryapp.presentation.members.MembersFragment

class AppMenuFragment : Fragment() {
    private var _binding: FragmentAppmenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppmenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (item.itemId) {
                R.id.nav_home -> DashboardFragment().apply {
                    arguments = Bundle().apply { putBoolean("showBackArrow", false) }
                }

                R.id.nav_books -> BooksFragment().apply {
                    arguments = Bundle().apply { putBoolean("showBackArrow", false) }
                }

                R.id.nav_members -> MembersFragment().apply {
                    arguments = Bundle().apply { putBoolean("showBackArrow", false) }
                }

                R.id.nav_history -> BorrowingHistoryFragment().apply {
                    arguments = Bundle().apply { putBoolean("showBackArrow", false) }
                }

                else -> null
            }

            selectedFragment?.let {
                replaceFragment(it)
                return@setOnItemSelectedListener true
            }
            false
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }

    fun setSelectedBottomTab(itemId: Int) {
        binding.bottomNav.menu
            .findItem(itemId)
            .isChecked = true
    }
}