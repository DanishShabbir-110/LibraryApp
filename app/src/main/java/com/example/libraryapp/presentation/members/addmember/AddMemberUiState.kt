package com.example.libraryapp.presentation.members.addmember

data class AddMemberUiState(
    val name:String="",
    val email:String="",
    val phoneNumber:String="",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isMemberAdded: Boolean = false
)
