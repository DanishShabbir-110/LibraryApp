package com.example.libraryapp.presentation.books.addbooks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentAddBookBinding


class AddBookFragment : Fragment() {
    private val binding: FragmentAddBookBinding by lazy{
        FragmentAddBookBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }


}