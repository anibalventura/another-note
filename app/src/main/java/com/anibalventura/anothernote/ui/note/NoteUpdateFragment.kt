package com.anibalventura.anothernote.ui.note

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anibalventura.anothernote.Constants.ARCHIVE_NOTE
import com.anibalventura.anothernote.Constants.DELETE_NOTE
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.data.viewmodel.ArchiveViewModel
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentNoteUpdateBinding
import com.anibalventura.anothernote.ui.note.adapter.NoteAdapter
import com.anibalventura.anothernote.utils.showToast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_note_update.*

class NoteUpdateFragment : Fragment() {

    // DataBinding.
    private var _binding: FragmentNoteUpdateBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<NoteUpdateFragmentArgs>()

    // ViewModels.
    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    private val archiveViewModel: ArchiveViewModel by viewModels()
    private val trashViewModel: TrashViewModel by viewModels()

    // Adapter.
    private val adapter: NoteAdapter by lazy { NoteAdapter() }

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

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note_update, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val noteItem =
            NoteData(args.currentItem.id, args.currentItem.title, args.currentItem.description)
        val archiveItem =
            ArchiveData(args.currentItem.id, args.currentItem.title, args.currentItem.description)
        val trashItem =
            TrashData(args.currentItem.id, args.currentItem.title, args.currentItem.description)

        when (item.itemId) {
            R.id.note_update_menu_save -> updateNote()
            R.id.note_update_menu_archive ->
                archiveOrDeleteNote(
                    ARCHIVE_NOTE,
                    noteItem,
                    archiveItem,
                    trashItem
                )
            R.id.note_update_menu_delete -> archiveOrDeleteNote(
                DELETE_NOTE,
                noteItem,
                archiveItem,
                trashItem
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateNote() {
        val title = etUpdateTitle.text.toString()
        val description = etUpdateDescription.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Update current note.
                val updatedItem = NoteData(args.currentItem.id, title, description)
                noteViewModel.updateData(updatedItem)
                showToast(requireContext(), getString(R.string.update_successful))
                // Navigate back.
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            else -> showToast(requireContext(), getString(R.string.update_fill_fields))
        }
    }

    private fun archiveOrDeleteNote(
        action: Int,
        noteItem: NoteData,
        archiveItem: ArchiveData,
        trashItem: TrashData
    ) {
        when (action) {
            // Send note to trash.
            DELETE_NOTE -> {
                trashViewModel.insertData(trashItem)
                noteViewModel.deleteItem(noteItem)
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                Snackbar.make(
                    requireView(),
                    getString(R.string.snack_moved_trash),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.snack_undo)) {
                        noteViewModel.insertData(noteItem)
                        trashViewModel.deleteItem(trashItem)
                    }.show()
            }
            // Send note to archive.
            ARCHIVE_NOTE -> {
                archiveViewModel.insertData(archiveItem)
                noteViewModel.deleteItem(noteItem)
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                Snackbar.make(
                    requireView(),
                    getString(R.string.snack_note_archived),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.snack_undo)) {
                        noteViewModel.insertData(noteItem)
                        archiveViewModel.deleteItem(archiveItem)
                    }.show()
            }
        }
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}