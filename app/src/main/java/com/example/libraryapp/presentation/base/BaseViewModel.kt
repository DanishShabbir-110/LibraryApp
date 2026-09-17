package com.example.libraryapp.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseViewModel<Event> : ViewModel() {
    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    protected suspend fun sendEvent(event: Event) {
        _events.emit(event)
    }
}