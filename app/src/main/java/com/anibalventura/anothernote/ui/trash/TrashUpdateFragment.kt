package com.anibalventura.anothernote.ui.trash

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anibalventura.anothernote.Constants.TRASH_TO_NOTE
import com.anibalventura.anothernote.Constants.TRASH_TO_NOTE_EDIT
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentTrashUpdateBinding
import com.anibalventura.anothernote.utils.showToast

class TrashUpdateFragment : Fragment() {

    private var _binding: FragmentTrashUpdateBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by viewModels()
    private val trashViewModel: TrashViewModel by viewModels()

    private val args by navArgs<TrashUpdateFragmentArgs>()

    private lateinit var noteItem: NoteData
    private lateinit var archiveItem: ArchiveData
    private lateinit var trashItem: TrashData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentTrashUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        noteItem =
            NoteData(args.currentItem.id, args.currentItem.title, args.currentItem.description)
        archiveItem =
            ArchiveData(args.currentItem.id, args.currentItem.title, args.currentItem.description)
        trashItem =
            TrashData(args.currentItem.id, args.currentItem.title, args.currentItem.description)

        binding.clTrashUpdate.setOnClickListener { view ->
            sharedViewModel.moveItem(TRASH_TO_NOTE_EDIT, noteItem, archiveItem, trashItem, view)
        }

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note, menu)
        // Enable required options.
        menu.findItem(R.id.menu_note_restore).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_delete_forever).setEnabled(true).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_note_restore -> sharedViewModel.moveItem(
                TRASH_TO_NOTE,
                noteItem,
                archiveItem,
                trashItem,
                requireView()
            )
            R.id.menu_note_delete_forever -> deleteForever()
        }
        return super.onOptionsItemSelected(item)
    }

    // Show dialog to confirm delete note forever.
    private fun deleteForever() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.dialog_delete_forever))
        dialogBuilder.setMessage(getString(R.string.dialog_delete_forever_you_sure))
        dialogBuilder.setPositiveButton(getString(R.string.dialog_confirmation)) { _, _ ->

            val trashItem =
                TrashData(args.currentItem.id, args.currentItem.title, args.currentItem.description)

            trashViewModel.deleteItem(trashItem)
            showToast(requireContext(), getString(R.string.successfully_deleted_forever))
            findNavController().navigate(R.id.action_trashUpdateFragment_to_trashFragment)
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