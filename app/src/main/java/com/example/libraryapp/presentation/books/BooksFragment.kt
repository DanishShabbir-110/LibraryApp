package com.example.libraryapp.presentation.books

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentBooksBinding
import com.example.libraryapp.presentation.books.addbooks.AddBookFragment
import com.example.libraryapp.presentation.members.addmember.AddMemberFragment


class BooksFragment : Fragment() {
    private val binding: FragmentBooksBinding by lazy {
        FragmentBooksBinding.inflate(layoutInflater)
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

        setupClickListeners()
    }
    fun setupClickListeners(){

        binding.fabAddBook.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, AddBookFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}