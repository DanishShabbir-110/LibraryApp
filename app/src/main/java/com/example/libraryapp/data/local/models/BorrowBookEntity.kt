package com.example.libraryapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity("BorrowBooks")
data class BorrowBookEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val bookId:Int,
    val memberId:Int,
    val issueDate:Long,
    val expectedReturnDate:Long,
    val actualReturnDate:Long,
    val status:String
): Serializable
