package com.example.libraryapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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

       setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHostFragment=childFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment

        val navController=navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}