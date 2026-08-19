package com.example.libraryapp.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Book")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val name:String,
    val author:String,
    val category:String,
    val totalCopies:Int,
    val availableCopies :Int
): Serializable
