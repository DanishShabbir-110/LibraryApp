package com.example.libraryapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("BorrowBooks")
data class BorrowBookEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val bookId:Int,
    val memberId:Int,
    val bookName: String,
    val memberName: String,
    val issueDate:Long,
    val returnDate:Long?=null,
    val status:String
)
