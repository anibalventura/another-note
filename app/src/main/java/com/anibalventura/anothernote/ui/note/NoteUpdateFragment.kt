package com.anibalventura.anothernote.ui.note

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anibalventura.anothernote.utils.Constants.NOTE_TO_ARCHIVE
import com.anibalventura.anothernote.utils.Constants.NOTE_TO_TRASH
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.adapters.NoteAdapter
import com.anibalventura.anothernote.data.models.ArchiveModel
import com.anibalventura.anothernote.data.models.NoteModel
import com.anibalventura.anothernote.data.models.TrashModel
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentNoteUpdateBinding
import com.anibalventura.anothernote.utils.changeNoteBackgroundColor
import com.anibalventura.anothernote.utils.setBarsColor
import com.anibalventura.anothernote.utils.shareText
import com.anibalventura.anothernote.utils.showToast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_note_update.*

class NoteUpdateFragment : Fragment() {

    // DataBinding.
    private var _binding: FragmentNoteUpdateBinding? = null
    private val binding get() = _binding!!

    // ViewModels.
    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()

    // Models.
    private lateinit var noteItem: NoteModel
    private lateinit var archiveItem: ArchiveModel
    private lateinit var trashItem: TrashModel

    // SafeArgs.
    private val args by navArgs<NoteUpdateFragmentArgs>()

    // RecyclerView Adapter.
    private val adapter: NoteAdapter by lazy { NoteAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment.
        _binding = FragmentNoteUpdateBinding.inflate(inflater, container, false)
        binding.args = args

        // Get notify every time the database change.
        noteViewModel.getDatabase.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfNoteIsEmpty(data)
            adapter.setData(data)
        })

        // Set current items.
        setCurrentItems()

        // Set ToolBar/NavigationBar/StatusBar color from note.
        setBarsColor(args.currentItem.color, activity?.toolbar, activity?.window)

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setCurrentItems() {
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note, menu)
        // Enable required options.
        menu.findItem(R.id.menu_note_update).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_archive).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_color).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_share).setEnabled(true).isVisible = true
        menu.findItem(R.id.menu_note_delete).setEnabled(true).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_note_update -> updateNote()
            R.id.menu_note_archive -> {
                sharedViewModel.moveItem(
                    NOTE_TO_ARCHIVE,
                    noteItem,
                    archiveItem,
                    trashItem,
                    requireView()
                )
                findNavController().navigate(R.id.action_noteUpdateFragment_to_noteFragment)
            }
            R.id.menu_note_color -> changeNoteBackgroundColor(
                binding.clNoteUpdate, activity?.toolbar, activity?.window, requireContext()
            )
            R.id.menu_note_share
            -> shareText(requireContext(), args.currentItem.description)
            R.id.menu_note_delete -> {
                sharedViewModel.moveItem(
                    NOTE_TO_TRASH,
                    noteItem,
                    archiveItem,
                    trashItem,
                    requireView()
                )
                findNavController().navigate(R.id.action_noteUpdateFragment_to_noteFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateNote() {
        // Get data to insert.
        val title = etUpdateTitle.text.toString()
        val description = etUpdateDescription.text.toString()
        val color = (binding.clNoteUpdate.background as ColorDrawable).color

        // Insert data to database.
        val updatedNote = NoteModel(args.currentItem.id, title, description, color)
        noteViewModel.updateItem(updatedNote)
        showToast(requireContext(), getString(R.string.update_successful))

        // Navigate back.
        findNavController().navigate(R.id.action_noteUpdateFragment_to_noteFragment)
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}