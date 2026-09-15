package com.example.libraryapp.utils

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun View.show(){
    isVisible=true
}

fun View.hide(){
    isVisible=false
}

fun Fragment.loadAndRepeatOnStarted(block:suspend CoroutineScope.()->Unit){
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
            block()
        }
    }
}