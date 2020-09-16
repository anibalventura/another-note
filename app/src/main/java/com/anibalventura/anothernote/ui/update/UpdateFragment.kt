package com.anibalventura.anothernote.ui.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.Utils
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentUpdateBinding
import kotlinx.android.synthetic.main.fragment_update.*

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()

    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        // Set menu.
        setHasOptionsMenu(true)

        // Spinner selected item.
        binding.spinnerUpdatePriority.onItemSelectedListener = sharedViewModel.listener

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_update, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.update_menu_save -> updateItem()
            R.id.update_menu_delete -> confirmDeleteItem()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        val title = etUpdateTitle.text.toString()
        val priority = spinnerUpdatePriority.selectedItem.toString()
        val description = etUpdateDescription.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Update current item.
                val updatedItem = NoteData(
                    args.currentItem.id,
                    title,
                    sharedViewModel.parsePriority(priority),
                    description
                )
                noteViewModel.updateData(updatedItem)
                Utils.showToast(requireContext(), "Successfully Updated")
                // Navigate back.
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            else -> Utils.showToast(requireContext(), "Please fill out all the fields.")
        }
    }

    // Show dialog to confirm delete item.
    private fun confirmDeleteItem() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Delete Note")
        dialogBuilder.setMessage("Are you sure you want to delete \"${args.currentItem.title}\"?")
        dialogBuilder.setPositiveButton("Yes") { _, _ ->
            noteViewModel.deleteItem(args.currentItem)
            Utils.showToast(requireContext(), "Successfully deleted \"${args.currentItem.title}\"")
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