package com.example.libraryapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentAppmenuBinding
import com.example.libraryapp.presentation.books.BooksFragment
import com.example.libraryapp.presentation.dashboard.DashboardFragment
import com.example.libraryapp.presentation.history.BorrowingHistoryFragment
import com.example.libraryapp.presentation.members.MembersFragment

class AppMenuFragment : Fragment() {
    private val binding: FragmentAppmenuBinding by lazy {
        FragmentAppmenuBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            replaceFragment(DashboardFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment? = when (item.itemId) {
                R.id.nav_home -> DashboardFragment()
                R.id.nav_books -> BooksFragment()
                R.id.nav_members -> MembersFragment()
                R.id.nav_history -> BorrowingHistoryFragment()
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

}