package id.nphew.binar.challenge4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import id.nphew.binar.challenge4.adapter.NoteActionListener
import id.nphew.binar.challenge4.adapter.NoteAdapter
import id.nphew.binar.challenge4.database.Note
import id.nphew.binar.challenge4.database.NoteDatabase
import id.nphew.binar.challenge4.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteAdapter
    private var noteDb: NoteDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteDb = NoteDatabase.getInstance(requireContext())

        initRecyclerView()
        getDataFromDb()
        addNote()
    }
    private fun initRecyclerView() {
        binding.apply {
            noteAdapter = NoteAdapter({}, {}, action)
            rvData.adapter = noteAdapter
            rvData.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun addNote() {
        binding.fabAdd.setOnClickListener {
            showAlertDialog(null)
        }
    }

    private fun showAlertDialog(note: Note?) {
        val customLayout =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_dialog_add, null, false)

        val etName = customLayout.findViewById<EditText>(R.id.et_name)
        val etEmail = customLayout.findViewById<EditText>(R.id.et_email)
        val btnSave = customLayout.findViewById<Button>(R.id.btn_save)

        if (note != null) {
            etName.setText(note.title)
            etEmail.setText(note.note)
        }

        val builder = AlertDialog.Builder(requireContext())

        builder.setView(customLayout)

        val dialog = builder.create()

        btnSave.setOnClickListener {
            val nama = etName.text.toString()
            val email = etEmail.text.toString()
            if (note != null) {
                val newNote = Note(note.id, nama, email)
                updateToDb(newNote)
            } else {
                saveToDb(nama, email)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveToDb(name: String, email: String) {
        val note = Note(null, name, email)
        CoroutineScope(Dispatchers.IO).launch {
            val result = noteDb?.noteDao()?.insertNote(note)
            if (result != 0L) {
                getDataFromDb()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Berhasil Ditambahkan", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Gagal Ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getDataFromDb() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = noteDb?.noteDao()?.getAllNote()
            if (result != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    noteAdapter.updateData(result)
                }
            }
        }
    }

    private fun updateToDb(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = noteDb?.noteDao()?.updateNote(note)
            if (result != 0) {
                getDataFromDb()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Berhasil Diupdate", Toast.LENGTH_SHORT).show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Gagal Diupdate", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val action = object : NoteActionListener {
        override fun onDelete(note: Note) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = noteDb?.noteDao()?.deleteNote(note)
                if (result != 0) {
                    getDataFromDb()
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Berhasil Dihapus", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Gagal Dihapus", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        override fun onEdit(note: Note) {
            showAlertDialog(note)
        }

    }

}