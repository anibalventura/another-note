package com.anibalventura.anothernote.ui.note

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
import com.anibalventura.anothernote.databinding.FragmentNoteUpdateBinding
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.fragment_note_update.*

class NoteUpdateFragment : Fragment() {

    private val args by navArgs<NoteUpdateFragmentArgs>()

    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    private val trashViewModel: TrashViewModel by viewModels()

    private var _binding: FragmentNoteUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentNoteUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note_update, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.update_menu_save -> updateItem()
            R.id.archive_update_menu_delete -> confirmDeleteItem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        val title = etUpdateTitle.text.toString()
        val description = etUpdateDescription.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Update current navdrawer_selector.
                val updatedItem = NoteData(args.currentItem.id, title, description)
                noteViewModel.updateData(updatedItem)
                showToast(requireContext(), "Successfully Updated")
                // Navigate back.
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            else -> showToast(requireContext(), "Please fill out all the fields.")
        }
    }

    // Show dialog to confirm delete navdrawer_selector.
    private fun confirmDeleteItem() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Delete Note")
        dialogBuilder.setMessage("Are you sure you want to delete \"${args.currentItem.title}\"?")
        dialogBuilder.setPositiveButton("Yes") { _, _ ->

            val trashItem =
                TrashData(args.currentItem.id, args.currentItem.title, args.currentItem.description)

            trashViewModel.insertData(trashItem)
            noteViewModel.deleteItem(args.currentItem)
            showToast(requireContext(), "Successfully deleted \"${args.currentItem.title}\"")
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        dialogBuilder.setNegativeButton("No") { _, _ -> }
        dialogBuilder.show()
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}