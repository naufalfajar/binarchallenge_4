package id.nphew.binar.challenge4.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.nphew.binar.challenge4.R
import id.nphew.binar.challenge4.database.Note

class NoteAdapter(private val onDelete : (Note) -> Unit,
                     private val onEdit : (Note) -> Unit,
                     private val listener: NoteActionListener)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){

    private val difCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, difCallback)

    fun updateData(Notes: List<Note>) = differ.submitList(Notes)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tv_title_value)
        private val tvNote: TextView = view.findViewById(R.id.tv_note_value)
        private val btnDelete: ImageView = view.findViewById(R.id.btn_delete)
        private val btnEdit: ImageView = view.findViewById(R.id.btn_edit)

        fun bind(note: Note) {
            tvTitle.text = note.title
            tvNote.text = note.note

            btnDelete.setOnClickListener {
                onDelete.invoke(note)
                listener.onDelete(note)
            }

            btnEdit.setOnClickListener {
                onEdit.invoke(note)
                listener.onEdit(note)
            }
        }
    }
}
interface NoteActionListener {
    fun onDelete(Note: Note)
    fun onEdit(Note: Note)
}