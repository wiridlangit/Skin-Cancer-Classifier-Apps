package com.dicoding.asclepius.view

import android.app.AlertDialog
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.database.Note
import com.dicoding.asclepius.databinding.ItemNoteBinding
import com.dicoding.asclepius.helper.NoteDiffCallback

class HistoryAdapter(private val historyViewModel: HistoryViewModel) : ListAdapter<Note, HistoryAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding, historyViewModel)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = getItem(position)
        holder.bind(currentNote)
    }

    class NoteViewHolder(private val binding: ItemNoteBinding, private val historyViewModel: HistoryViewModel) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.textViewNotePrediction.text = note.prediction
            binding.tvTimestamp.text = note.timestamp
            binding.tvResult.text = "Confidence: ${String.format("%.2f%%", note.confidenceScore * 100)}"
            Glide.with(binding.imageViewNoteImage.context)
                .load(Uri.parse(note.imageUri))
                .into(binding.imageViewNoteImage)
            binding.buttonDeleteNote.setOnClickListener {
                AlertDialog.Builder(it.context)
                    .setTitle("Delete History")
                    .setMessage("Are you sure you want to delete this history?")
                    .setPositiveButton("Yes") { _, _ ->
                        historyViewModel.delete(note.id)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }

        }
    }
    fun setNotes(notes: List<Note>) {
        submitList(notes)
    }
}

