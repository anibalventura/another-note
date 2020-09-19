package com.anibalventura.anothernote.ui.archiveupdate

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
import com.anibalventura.anothernote.data.viewmodel.ArchiveViewModel
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentArchiveUpdateBinding
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.fragment_archive_update.*

class ArchiveUpdateFragment : Fragment() {

    private val args by navArgs<ArchiveUpdateFragmentArgs>()

    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
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
        inflater.inflate(R.menu.menu_archive_update, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.archive_update_menu_save -> updateItem()
            R.id.archive_update_menu_unarchive -> unarchiveItem()
            R.id.archive_update_menu_delete -> confirmDeleteItem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        val title = etArchiveUpdateTitle.text.toString()
        val description = etArchiveUpdateDescription.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Update current item.
                val updatedItem = ArchiveData(args.currentItem.id, title, description)
                archiveViewModel.updateData(updatedItem)
                showToast(requireContext(), "Successfully Updated")
                // Navigate back.
                findNavController().navigate(R.id.action_updateArchiveFragment_to_archiveFragment)
            }
            else -> showToast(requireContext(), "Please fill out all the fields.")
        }
    }

    private fun unarchiveItem() {
        val title = etArchiveUpdateTitle.text.toString()
        val description = etArchiveUpdateDescription.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Update current item.
                val unarchiveItem = NoteData(args.currentItem.id, title, description)
                val deletedItem = ArchiveData(args.currentItem.id, title, description)

                archiveViewModel.deleteItem(deletedItem)
                noteViewModel.insertData(unarchiveItem)
                showToast(requireContext(), "Successfully Unarchive")
                // Navigate back.
                findNavController().navigate(R.id.action_updateArchiveFragment_to_archiveFragment)
            }
        }
    }

    // Show dialog to confirm delete item.
    private fun confirmDeleteItem() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Delete Note")
        dialogBuilder.setMessage("Are you sure you want to delete \"${args.currentItem.title}\"?")
        dialogBuilder.setPositiveButton("Yes") { _, _ ->

            val archiveItem =
                ArchiveData(
                    args.currentItem.id,
                    args.currentItem.title,
                    args.currentItem.description
                )

            archiveViewModel.deleteItem(archiveItem)
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