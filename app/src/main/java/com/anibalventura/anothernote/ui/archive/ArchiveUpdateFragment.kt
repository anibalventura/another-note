package com.anibalventura.anothernote.ui.archive

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anibalventura.anothernote.Constants.ARCHIVE_TO_NOTE
import com.anibalventura.anothernote.Constants.ARCHIVE_TO_TRASH
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveModel
import com.anibalventura.anothernote.data.models.NoteModel
import com.anibalventura.anothernote.data.models.TrashModel
import com.anibalventura.anothernote.data.viewmodel.ArchiveViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentArchiveUpdateBinding
import com.anibalventura.anothernote.utils.changeNoteBackgroundColor
import com.anibalventura.anothernote.utils.shareText
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_archive_update.*

class ArchiveUpdateFragment : Fragment() {

    // DataBinding.
    private var _binding: FragmentArchiveUpdateBinding? = null
    private val binding get() = _binding!!

    // ViewModels.
    private val sharedViewModel: SharedViewModel by viewModels()
    private val archiveViewModel: ArchiveViewModel by viewModels()

    // Models.
    private lateinit var noteItem: NoteModel
    private lateinit var archiveItem: ArchiveModel
    private lateinit var trashItem: TrashModel

    // SafeArgs.
    private val args by navArgs<ArchiveUpdateFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment.
        _binding = FragmentArchiveUpdateBinding.inflate(inflater, container, false)
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

        // Set toolbar color from note.
        activity?.toolbar?.setBackgroundColor(args.currentItem.color)

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note, menu)
        // Enable required options.
        menu.findItem(R.id.menu_note_update).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_unarchive).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_color).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_share).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_delete).setEnabled(true).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_note_update -> updateItem()
            R.id.menu_note_unarchive -> sharedViewModel.moveItem(
                ARCHIVE_TO_NOTE,
                noteItem,
                archiveItem,
                trashItem,
                requireView()
            )
            R.id.menu_note_color -> changeNoteBackgroundColor(
                binding.clArchiveUpdate,
                activity?.toolbar,
                requireContext()
            )
            R.id.menu_note_share -> shareText(requireContext(), args.currentItem.description)
            R.id.menu_note_delete -> sharedViewModel.moveItem(
                ARCHIVE_TO_TRASH,
                noteItem,
                archiveItem,
                trashItem,
                requireView()
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        // Get data to insert.
        val title = etArchiveUpdateTitle.text.toString()
        val description = etArchiveUpdateDescription.text.toString()
        val color = (binding.clArchiveUpdate.background as ColorDrawable).color

        // Insert data to database.
        val updatedItem = ArchiveModel(args.currentItem.id, title, description, color)
        archiveViewModel.updateItem(updatedItem)
        showToast(requireContext(), getString(R.string.update_successful))

        // Navigate back.
        findNavController().navigate(R.id.action_archiveUpdateFragment_to_archiveFragment)
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}