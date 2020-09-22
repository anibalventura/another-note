package com.anibalventura.anothernote.ui.archive

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.data.viewmodel.ArchiveViewModel
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentArchiveUpdateBinding
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.fragment_archive_update.*

class ArchiveUpdateFragment : Fragment() {

    private val args by navArgs<ArchiveUpdateFragmentArgs>()

    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    private val trashViewModel: TrashViewModel by viewModels()
    private val archiveViewModel: ArchiveViewModel by viewModels()

    private var _binding: FragmentArchiveUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentArchiveUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note, menu)
        // Enable required options.
        menu.findItem(R.id.menu_note_update).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_unarchive).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_delete).setEnabled(true).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_note_update -> updateItem()
            R.id.menu_note_unarchive -> unarchiveItem()
            R.id.menu_note_delete -> confirmDeleteNote()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        val title = etArchiveUpdateTitle.text.toString()
        val description = etArchiveUpdateDescription.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Update current note.
                val updatedItem = ArchiveData(args.currentItem.id, title, description)
                archiveViewModel.updateData(updatedItem)
                showToast(requireContext(), getString(R.string.update_successful))
                // Navigate back.
                findNavController().navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
            }
            else -> showToast(requireContext(), getString(R.string.update_fill_fields))
        }
    }

    private fun unarchiveItem() {
        val title = etArchiveUpdateTitle.text.toString()
        val description = etArchiveUpdateDescription.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Update current note.
                val unarchiveItem = NoteData(args.currentItem.id, title, description)
                val deletedItem = ArchiveData(args.currentItem.id, title, description)
                archiveViewModel.deleteItem(deletedItem)
                noteViewModel.insertData(unarchiveItem)
                showToast(requireContext(), getString(R.string.successfully_unarchive))
                // Navigate back.
                findNavController().navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
            }
        }
    }

    // Show dialog to confirm delete note.
    private fun confirmDeleteNote() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.delete_note))
        dialogBuilder.setMessage(getString(R.string.dialog_delete_note))
        dialogBuilder.setPositiveButton(getString(R.string.dialog_confirmation)) { _, _ ->

            val archiveNote = ArchiveData(
                args.currentItem.id,
                args.currentItem.title,
                args.currentItem.description
            )
            val trashNote = TrashData(
                args.currentItem.id,
                args.currentItem.title,
                args.currentItem.description
            )

            archiveViewModel.deleteItem(archiveNote)
            trashViewModel.insertData(trashNote)

            showToast(requireContext(), getString(R.string.successfully_delete_note))
            findNavController().navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
        }
        dialogBuilder.setNegativeButton(getString(R.string.dialog_negative)) { _, _ -> }
        dialogBuilder.show()
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}