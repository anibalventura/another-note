package com.anibalventura.anothernote.ui.trash

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentTrashUpdateBinding
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.fragment_trash_update.*

class TrashUpdateFragment : Fragment() {

    private val args by navArgs<TrashUpdateFragmentArgs>()

    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    private val trashViewModel: TrashViewModel by viewModels()

    private var _binding: FragmentTrashUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentTrashUpdateBinding.inflate(inflater, container, false)
        binding.args = args

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
            R.id.menu_note_restore -> restoreItem()
            R.id.menu_note_delete_forever -> deleteForever()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun restoreItem() {
        val title = etViewTrashTitle.text.toString()
        val description = etViewTrashDescription.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Update current note.
                val restoreItem = NoteData(args.currentItem.id, title, description)
                val deletedItem = TrashData(args.currentItem.id, title, description)

                trashViewModel.deleteItem(deletedItem)
                noteViewModel.insertData(restoreItem)
                showToast(requireContext(), getString(R.string.successfully_restore))
                // Navigate back.
                findNavController().navigate(R.id.action_trashUpdateFragment_to_trashFragment)
            }
        }
    }

    // Show dialog to confirm delete note.
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