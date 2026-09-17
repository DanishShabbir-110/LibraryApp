package com.example.libraryapp.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding,VM: ViewModel>(
    private val bindingInflater:(inflater: LayoutInflater,container: ViewGroup?,attachedToParent: Boolean)->VB,
    private val viewModelClass:Class<VM>
): Fragment() {
    private var _binding:VB?=null
    protected val binding get() = _binding!!

    protected val viewModel:VM by lazy {
        ViewModelProvider(this)[viewModelClass]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=bindingInflater(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}