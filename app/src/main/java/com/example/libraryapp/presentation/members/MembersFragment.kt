package com.example.libraryapp.presentation.members

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.libraryapp.R
import com.example.libraryapp.databinding.FragmentMembersBinding
import com.example.libraryapp.presentation.members.addmember.AddMemberFragment

class MembersFragment : Fragment() {
    private val binding: FragmentMembersBinding by lazy {
        FragmentMembersBinding.inflate(layoutInflater)
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

        binding.fabAddMember.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, AddMemberFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}