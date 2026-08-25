package com.example.libraryapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Book")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val name:String,
    val author:String,
    val category:String,
    val totalCopies:Int,
    val availableCopies :Int
)
