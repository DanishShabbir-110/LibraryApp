package com.example.libraryapp.utils

import android.view.View
import androidx.core.view.isVisible

fun View.show(){
    isVisible=true
}

fun View.hide(){
    isVisible=false
}