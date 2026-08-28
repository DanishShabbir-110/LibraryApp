package com.example.libraryapp.presentation.books.bookslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.databinding.ItemBookBinding
import com.example.libraryapp.domain.model.Book

class BooksAdapter(
    private val onDeleteClick: (Book) -> Unit,
    private val onUpdateClick: (Book) -> Unit
) : RecyclerView.Adapter<BooksAdapter.VH>() {
    private var books = mutableListOf<Book>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemBookBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding(
            books[position],
            onDeleteClick = onDeleteClick,
            onUpdateClick = onUpdateClick
        )
    }

    override fun getItemCount(): Int = books.size

    fun submitList(newList: List<Book>) {
        books.clear()
        books.addAll(newList)
        notifyDataSetChanged()
    }

    class VH(val bind: ItemBookBinding) : RecyclerView.ViewHolder(bind.root) {
        fun binding(book: Book, onDeleteClick: (Book) -> Unit, onUpdateClick: (Book) -> Unit) {
           bind.apply {
                tvBookTitle.text = book.name
                tvBookAuthor.text = book.author.let { "Author: ${book.author}" }
                tvBookCategory.text = book.category.let { "Category: ${book.category.displayName}" }
                tvBookAvailability.text = book.availableCopies.let { "Available: ${book.availableCopies} / ${book.totalCopies}" }
                ivDeleteBook.setOnClickListener {
                    onDeleteClick(book)
                }
                root.setOnClickListener {
                    onUpdateClick(book)
                }
            }
        }
    }

}