package id.nphew.binar.challenge4

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.nphew.binar.challenge4.adapter.NoteActionListener
import id.nphew.binar.challenge4.adapter.NoteAdapter
import id.nphew.binar.challenge4.database.AccountDatabase
import id.nphew.binar.challenge4.database.Note
import id.nphew.binar.challenge4.databinding.FragmentHomeBinding
import id.nphew.binar.challenge4.sharedPrefs.SharedPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteAdapter
    private var accountDb: AccountDatabase? = null
//    private val authenticator: SharedPrefs by lazy { SharedPrefs(requireContext()) }
    private lateinit var authenticator: SharedPrefs
    private var accEmail: String? = null
//    = authenticator.loggedInEmail()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountDb = AccountDatabase.getInstance(requireContext())
        authenticator = SharedPrefs(requireContext())
        accEmail = authenticator.loggedInEmail()

        setUsername(accEmail)
        logout()

        initRecyclerView()
        getDataFromDb()
        addNote()
    }

    private fun setUsername(email: String?){
        CoroutineScope(Dispatchers.Main).launch {
            val welcome = "Welcome, "
            val caution = "!"
            val username = getUsername(email)
            val value = welcome + username + caution
            binding.tvHomeWelcome.text = value
        }
    }

    private fun logout(){
        binding.tvHomeLogout.setOnClickListener {
            authenticator.clearLoggedInUser()
            it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
        }
    }

    private suspend fun getUsername(email: String?): String? = withContext(Dispatchers.IO) {
        return@withContext accountDb?.accountDao()?.getUsername(email)
    }

    private suspend fun getAccountId(email: String?): Int? = withContext(Dispatchers.IO) {
        return@withContext accountDb?.accountDao()?.getId(email)
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

        val etTitle = customLayout.findViewById<EditText>(R.id.et_title)
        val etNote = customLayout.findViewById<EditText>(R.id.et_note)
        val btnSave = customLayout.findViewById<Button>(R.id.btn_save)

        if (note != null) {
            etTitle.setText(note.title)
            etNote.setText(note.note)
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(customLayout)
        val dialog = builder.create()

        btnSave.setOnClickListener {
            val judul = etTitle.text.toString()
            val isi = etNote.text.toString()
            if (note != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val accId = getAccountId(accEmail)
                    val newNote = Note(note.id, accId, judul, isi)
                    updateToDb(newNote)
                }
            } else {
                saveToDb(judul, isi)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun saveToDb(judul: String, isi: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val accId = getAccountId(accEmail)
            val note = Note(null,accId, judul, isi)

            CoroutineScope(Dispatchers.IO).launch {
                val result = accountDb?.noteDao()?.insertNote(note)
                if (result != 0L) {
                    getDataFromDb()
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Berhasil Ditambahkan", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(), "Gagal Ditambahkan", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun getDataFromDb() {
        CoroutineScope(Dispatchers.Main).launch {
            val accId = getAccountId(accEmail)
            CoroutineScope(Dispatchers.IO).launch {
                val result = accountDb?.noteDao()?.getAllNote(accId)
                if (result != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        noteAdapter.updateData(result)
                    }
                }
            }
        }
    }

    private fun updateToDb(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = accountDb?.noteDao()?.updateNote(note)
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
        override fun onDelete(Note: Note) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = accountDb?.noteDao()?.deleteNote(Note)
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
        override fun onEdit(Note: Note) {
            showAlertDialog(Note)
        }

    }

}