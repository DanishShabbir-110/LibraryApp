package com.example.libraryapp.presentation.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libraryapp.databinding.ItemMemberBinding
import com.example.libraryapp.domain.model.Member

class MemberAdapter(
    private val onDeleteClick:(Member)-> Unit
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
        holder.binding(members[position],onDeleteClick=onDeleteClick)
    }

    override fun getItemCount(): Int = members.size

    fun submitList(newList:List<Member>){
        members.clear()
        members.addAll(newList)
        notifyDataSetChanged()
    }

    class VH(val bind: ItemMemberBinding) : RecyclerView.ViewHolder(bind.root) {
        fun binding(member: Member,onDeleteClick: (Member) -> Unit) {
            bind.tvMemberName.text = member.name
            bind.tvMemberEmail.text = member.email
            bind.tvPhone.text = member.phone
            bind.ivDeleteMember.setOnClickListener {
                onDeleteClick(member)
            }
        }
    }
}