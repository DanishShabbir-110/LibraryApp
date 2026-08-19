package com.example.libraryapp.presentation.issue_return

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentIssueReturnBookBinding


class IssueReturnBookFragment : Fragment() {
    private val binding: FragmentIssueReturnBookBinding by lazy {
        FragmentIssueReturnBookBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_issue_return_book, container, false)
    }

}