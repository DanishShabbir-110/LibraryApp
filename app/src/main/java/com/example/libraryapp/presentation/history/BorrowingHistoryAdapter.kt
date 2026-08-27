package com.example.libraryapp.presentation.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.databinding.ItemBorrowingHistoryBinding
import com.example.libraryapp.domain.model.BorrowingStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BorrowingHistoryAdapter() : RecyclerView.Adapter<BorrowingHistoryAdapter.VH>() {
    private var borrowingHistory = mutableListOf<BorrowingHistoryItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemBorrowingHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding(borrowingHistory[position])
    }

    override fun getItemCount(): Int = borrowingHistory.size

    class VH(val bind: ItemBorrowingHistoryBinding) : RecyclerView.ViewHolder(bind.root) {
        fun binding(borrowBook: BorrowingHistoryItem) {
            bind.apply {
                tvMemberName.text = borrowBook.memberName
                tvBookTitle.text = borrowBook.bookName
                tvIssueDate.text = borrowBook.issueDate.let { "Issued: ${formatDate(it)}" }
                tvReturnDate.text = borrowBook.returnDate?.let {

                    "Returned: ${formatDate(it)}"

                } ?: "Not Returned"
                if (borrowBook.status == BorrowingStatus.ISSUED) {
                    tvStatus.text = borrowBook.status.toString()
                    tvStatus.setBackgroundColor("#FFF7E9".toColorInt())
                    tvStatus.setTextColor("#FF9800".toColorInt())
                } else {
                    tvStatus.text = borrowBook.status.toString()
                    tvStatus.setBackgroundColor("#EAF9F4".toColorInt())
                    tvStatus.setTextColor("#4CAF50".toColorInt())
                }
            }
        }

        private fun formatDate(
            time: Long
        ): String {

            val formatter = SimpleDateFormat(
                "dd MMM yyyy", Locale.getDefault()
            )

            return formatter.format(
                Date(time)
            )
        }
    }

    fun submitList(newList: List<BorrowingHistoryItem>) {
        borrowingHistory.clear()
        borrowingHistory.addAll(newList)
        notifyDataSetChanged()
    }
}