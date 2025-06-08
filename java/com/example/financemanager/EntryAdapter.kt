package com.example.financemanager

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class EntryAdapter(
    private val onDeleteClicked: (Entry) -> Unit // Callback for delete action
) : ListAdapter<Entry, EntryAdapter.EntryViewHolder>(EntryDiffCallback()) {

    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountTextView: TextView = itemView.findViewById(R.id.entryAmountTextView)
        val typeTextView: TextView = itemView.findViewById(R.id.entryTypeTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.entryDescriptionTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.entryCategoryTextView)
        // Add reference to the delete button from your entry_item.xml
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteEntry)

        fun bind(entry: Entry, onDeleteClickedCallback: (Entry) -> Unit) { // Pass the callback to bind
            amountTextView.text = String.format("%.2f", entry.amount)
            typeTextView.text = entry.type
            descriptionTextView.text = entry.description
            categoryTextView.text = entry.category

            if (entry.type.equals("income", ignoreCase = true)) {
                amountTextView.setTextColor(Color.GREEN)
            } else {
                amountTextView.setTextColor(Color.RED)
            }

            // Set the click listener for the delete button
            deleteButton.setOnClickListener {
                onDeleteClickedCallback(entry) // Invoke the callback with the current entry
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.entry_item, parent, false) // Ensure R.layout.entry_item is correct
        return EntryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val currentEntry = getItem(position)
        holder.bind(currentEntry, onDeleteClicked) // Pass the adapter's callback to the ViewHolder's bind method
    }

    class EntryDiffCallback : DiffUtil.ItemCallback<Entry>() {
        override fun areItemsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            // Assuming 'id' is the unique identifier from your Room Entity.
            // Make sure your Entry data class has a unique 'id' field.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem == newItem // Assumes Entry is a data class for content comparison
        }
    }
}