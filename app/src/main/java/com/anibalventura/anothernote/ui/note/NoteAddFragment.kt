package com.anibalventura.anothernote.ui.note

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.NoteModel
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.databinding.FragmentNoteAddBinding
import com.anibalventura.anothernote.utils.changeNoteBackgroundColor
import kotlinx.android.synthetic.main.activity_main.*

class NoteAddFragment : Fragment() {

    // DataBinding.
    private var _binding: FragmentNoteAddBinding? = null
    private val binding get() = _binding!!

    // ViewModel.
    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentNoteAddBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note, menu)
        // Enable required options.
        menu.findItem(R.id.menu_note_save).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_color).setEnabled(true).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_note_save -> insertNewItem()
            R.id.menu_note_color -> changeNoteBackgroundColor(
                binding.clNoteAdd,
                activity?.toolbar,
                activity?.window,
                requireContext()
            )
        }

        return super.onOptionsItemSelected(item)
    }

    private fun insertNewItem() {
        // Get data to insert.
        val title = binding.etNotAddTitle.text.toString()
        val description = binding.etNoteAddNote.text.toString()
        val color = (binding.clNoteAdd.background as ColorDrawable).color

        // Insert data to database.
        val newNote = NoteModel(0, title, description, color)
        noteViewModel.insertItem(newNote)

        // Navigate back.
        findNavController().navigate(R.id.action_noteAddFragment_to_noteFragment)
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}