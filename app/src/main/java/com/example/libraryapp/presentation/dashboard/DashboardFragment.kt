package com.example.libraryapp.presentation.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentDashboardBinding
import com.example.libraryapp.presentation.books.BooksFragment
import com.example.libraryapp.presentation.issue_return.IssueReturnBookFragment
import com.example.libraryapp.presentation.members.MembersFragment
import com.example.libraryapp.presentation.members.addmember.AddMemberFragment

class DashboardFragment : Fragment() {
    private  val binding: FragmentDashboardBinding by lazy{
        FragmentDashboardBinding.inflate(layoutInflater)
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

        binding.clBooks.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, BooksFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.clMembers.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, MembersFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.clIssueReturn.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, IssueReturnBookFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}