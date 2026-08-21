package com.example.libraryapp.presentation.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
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
            ItemBorrowingHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding(borrowingHistory[position])
    }

    override fun getItemCount(): Int = borrowingHistory.size

    class VH(val bind: ItemBorrowingHistoryBinding) : RecyclerView.ViewHolder(bind.root) {
        fun binding(borrowBook: BorrowingHistoryItem) {
            with(bind) {
                tvMemberName.text = borrowBook.memberName
                tvBookTitle.text = borrowBook.bookName
                tvIssueDate.text = "Issued: ${formatDate(borrowBook.issueDate)}"
                tvReturnDate.text =
                    borrowBook.returnDate?.let {

                        "Returned: ${formatDate(it)}"

                    } ?: "Not Returned"
                if (borrowBook.status == BorrowingStatus.ISSUED) {
                    tvStatus.text = borrowBook.status.toString()
                    tvStatus.setBackgroundColor(Color.parseColor("#FFF7E9"))
                    tvStatus.setTextColor(Color.parseColor("#FF9800"))
                } else {
                    tvStatus.text = borrowBook.status.toString()
                }
            }
        }

        private fun formatDate(
            time: Long
        ): String {

            val formatter =
                SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                )

            return formatter.format(
                Date(time)
            )
        }
    }

    fun submitList(newList:List<BorrowingHistoryItem>){
        borrowingHistory.clear()
        borrowingHistory.addAll(newList)
        notifyDataSetChanged()
    }


}