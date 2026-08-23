package com.example.libraryapp.presentation.members.addmember

data class AddMemberUiState(
    val id:Int=0,
    val name:String="",
    val email:String="",
    val phoneNumber:String="",
    val isEditMode: Boolean=false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isMemberAdded: Boolean = false
)
