package com.anibalventura.anothernote.ui.note

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anibalventura.anothernote.Constants.NOTE_TO_ARCHIVE
import com.anibalventura.anothernote.Constants.NOTE_TO_TRASH
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.adapters.NoteAdapter
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentNoteUpdateBinding
import com.anibalventura.anothernote.utils.shareText
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.fragment_note_update.*

class NoteUpdateFragment : Fragment() {

    // DataBinding.
    private var _binding: FragmentNoteUpdateBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<NoteUpdateFragmentArgs>()

    // ViewModels.
    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()

    // Adapter.
    private val adapter: NoteAdapter by lazy { NoteAdapter() }

    // Models.
    private lateinit var noteItem: NoteData
    private lateinit var archiveItem: ArchiveData
    private lateinit var trashItem: TrashData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentNoteUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        // Get notify every time the database change.
        noteViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfNoteIsEmpty(data)
            adapter.setData(data)
        })

        noteItem =
            NoteData(args.currentItem.id, args.currentItem.title, args.currentItem.description)
        archiveItem =
            ArchiveData(args.currentItem.id, args.currentItem.title, args.currentItem.description)
        trashItem =
            TrashData(args.currentItem.id, args.currentItem.title, args.currentItem.description)

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note, menu)
        // Enable required options.
        menu.findItem(R.id.menu_note_update).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_archive).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_share).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_delete).setEnabled(true).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_note_update -> updateNote()
            R.id.menu_note_archive -> {
                sharedViewModel.moveItem(
                    NOTE_TO_ARCHIVE,
                    noteItem,
                    archiveItem,
                    trashItem,
                    requireView()
                )
                findNavController().navigate(R.id.action_noteUpdateFragment_to_noteFragment)
            }
            R.id.menu_note_share -> shareText(requireContext(), args.currentItem.description)
            R.id.menu_note_delete -> {
                sharedViewModel.moveItem(
                    NOTE_TO_TRASH,
                    noteItem,
                    archiveItem,
                    trashItem,
                    requireView()
                )
                findNavController().navigate(R.id.action_noteUpdateFragment_to_noteFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateNote() {
        val title = etUpdateTitle.text.toString()
        val description = etUpdateDescription.text.toString()

        // Update note
        val updatedNote = NoteData(args.currentItem.id, title, description)
        noteViewModel.updateData(updatedNote)
        showToast(requireContext(), getString(R.string.update_successful))

        // Navigate back.
        findNavController().navigate(R.id.action_noteUpdateFragment_to_noteFragment)
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}