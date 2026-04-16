package com.example.personaleventplanner.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personaleventplanner.data.Event
import com.example.personaleventplanner.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(
    private val onItemClick: (Event) -> Unit,
    private val onDeleteClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var events = listOf<Event>()

    fun submitList(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

            binding.tvTitle.text = event.title
            binding.tvCategory.text = "Category: ${event.category}"
            binding.tvLocation.text = "Location: ${event.location}"
            binding.tvDateTime.text = "Date: ${formatter.format(Date(event.dateTime))}"

            binding.root.setOnClickListener {
                onItemClick(event)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size
}