package com.example.libraryapp.domain.model

enum class BorrowingStatus {
    ALL,
    ISSUED,
    RETURNED;

    companion object{
        fun fromString(value: String): BorrowingStatus {
            return when (value) {
                RETURNED.name -> RETURNED
                else -> ISSUED
            }
        }
    }
}
