package com.anibalventura.anothernote.ui.note

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.fragment_note_add.*

class NoteAddFragment : Fragment() {

    private val noteViewModel: NoteViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_note_add, container, false)

        // Set menu.
        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note, menu)
        // Enable required options.
        menu.findItem(R.id.menu_note_save).setEnabled(true).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_note_save -> insertNewNote()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertNewNote() {
        val title = tvAddTitle.text.toString()
        val description = etAddNote.text.toString()

        // Insert data to database.
        val newNote =
            NoteData(0, title, description)
        noteViewModel.insertData(newNote)
        showToast(requireContext(), "Note added.")

        // Navigate back.
        findNavController().navigate(R.id.action_noteAddFragment_to_noteFragment)
    }
}