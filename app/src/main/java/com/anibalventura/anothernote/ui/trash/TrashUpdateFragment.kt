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
import com.anibalventura.anothernote.data.models.ArchiveModel
import com.anibalventura.anothernote.data.models.NoteModel
import com.anibalventura.anothernote.data.models.TrashModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentTrashUpdateBinding
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*

class TrashUpdateFragment : Fragment() {

    // DataBinding.
    private var _binding: FragmentTrashUpdateBinding? = null
    private val binding get() = _binding!!

    // ViewModels.
    private val sharedViewModel: SharedViewModel by viewModels()
    private val trashViewModel: TrashViewModel by viewModels()

    // Models.
    private lateinit var noteItem: NoteModel
    private lateinit var archiveItem: ArchiveModel
    private lateinit var trashItem: TrashModel

    // SafeArgs.
    private val args by navArgs<TrashUpdateFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment.
        _binding = FragmentTrashUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        // Set current items.
        noteItem = NoteModel(
            args.currentItem.id,
            args.currentItem.title,
            args.currentItem.description,
            args.currentItem.color
        )
        archiveItem = ArchiveModel(
            args.currentItem.id,
            args.currentItem.title,
            args.currentItem.description,
            args.currentItem.color
        )
        trashItem = TrashModel(
            args.currentItem.id,
            args.currentItem.title,
            args.currentItem.description,
            args.currentItem.color
        )

        // Can't update note on trash.
        binding.clTrashUpdate.setOnClickListener { view ->
            sharedViewModel.moveItem(TRASH_TO_NOTE_EDIT, noteItem, archiveItem, trashItem, view)
        }

        // Set toolbar color from note.
        activity?.toolbar?.setBackgroundColor(args.currentItem.color)

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
            .setTitle(getString(R.string.dialog_delete_forever))
            .setMessage(getString(R.string.dialog_delete_forever_you_sure))
            .setPositiveButton(getString(R.string.dialog_confirmation)) { _, _ ->

                val trashItem = TrashModel(
                    args.currentItem.id,
                    args.currentItem.title,
                    args.currentItem.description,
                    args.currentItem.color
                )

                trashViewModel.deleteItem(trashItem)
                showToast(requireContext(), getString(R.string.successfully_deleted_forever))
                findNavController().navigate(R.id.action_trashUpdateFragment_to_trashFragment)
            }
            .setNegativeButton(getString(R.string.dialog_negative)) { _, _ -> }

        dialogBuilder.show()
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}