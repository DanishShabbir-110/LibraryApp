package com.example.libraryapp.presentation.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.databinding.ItemMemberBinding
import com.example.libraryapp.domain.model.Member

class MemberAdapter(
    private val onDeleteClick: (Member) -> Unit,
    private val onUpdateClick: (Member) -> Unit
) : RecyclerView.Adapter<MemberAdapter.VH>() {
    private var members = mutableListOf<Member>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VH {
        return VH(
            ItemMemberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int
    ) {
        holder.binding(
            members[position],
            onDeleteClick = onDeleteClick,
            onUpdateClick = onUpdateClick
        )
    }

    override fun getItemCount(): Int = members.size

    fun submitList(newList: List<Member>) {
        members.clear()
        members.addAll(newList)
        notifyDataSetChanged()
    }

    class VH(val bind: ItemMemberBinding) : RecyclerView.ViewHolder(bind.root) {
        fun binding(
            member: Member,
            onDeleteClick: (Member) -> Unit,
            onUpdateClick: (Member) -> Unit
        ) {
            bind.apply {

                tvMemberName.text = member.name
                tvMemberEmail.text = member.email
                tvPhone.text = member.phone
                ivDeleteMember.setOnClickListener {
                    onDeleteClick(member)
                }
                root.setOnClickListener {
                    onUpdateClick(member)
                }
            }
        }
    }
}