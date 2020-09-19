package com.anibalventura.anothernote.ui.viewtrash

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
import com.anibalventura.anothernote.databinding.FragmentViewTrashBinding
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.fragment_view_trash.*

class ViewTrashFragment : Fragment() {

    private val args by navArgs<ViewTrashFragmentArgs>()

    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()
    private val trashViewModel: TrashViewModel by viewModels()

    private var _binding: FragmentViewTrashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentViewTrashBinding.inflate(inflater, container, false)
        binding.args = args

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_view_trash, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.view_trash_menu_restore -> restoreItem()
            R.id.view_trash_menu_delete -> deleteForever()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun restoreItem() {
        val title = etViewTrashTitle.text.toString()
        val description = etViewTrashDescription.text.toString()

        when (sharedViewModel.verifyData(title, description)) {
            true -> {
                // Update current item.
                val restoreItem = NoteData(args.currentItem.id, title, description)
                val deletedItem = TrashData(args.currentItem.id, title, description)

                trashViewModel.deleteItem(deletedItem)
                noteViewModel.insertData(restoreItem)
                showToast(requireContext(), "Successfully Restored")
                // Navigate back.
                findNavController().navigate(R.id.action_viewTrashFragment_to_trashFragment)
            }
        }
    }

    // Show dialog to confirm delete item.
    private fun deleteForever() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Delete Forever")
        dialogBuilder.setMessage("Are you sure you want to delete \"${args.currentItem.title}\" forever?")
        dialogBuilder.setPositiveButton("Yes") { _, _ ->

            val trashItem =
                TrashData(args.currentItem.id, args.currentItem.title, args.currentItem.description)

            trashViewModel.deleteItem(trashItem)
            showToast(requireContext(), "Successfully deleted forever\"${args.currentItem.title}\"")
            findNavController().navigate(R.id.action_viewTrashFragment_to_trashFragment)
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