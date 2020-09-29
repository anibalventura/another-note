package com.anibalventura.anothernote.ui.note

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.adapters.NoteAdapter
import com.anibalventura.anothernote.data.models.ArchiveModel
import com.anibalventura.anothernote.data.models.NoteModel
import com.anibalventura.anothernote.data.models.TrashModel
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentNoteUpdateBinding
import com.anibalventura.anothernote.utils.Constants.NOTE_DISCARD
import com.anibalventura.anothernote.utils.Constants.NOTE_TO_ARCHIVE
import com.anibalventura.anothernote.utils.Constants.NOTE_TO_TRASH
import com.anibalventura.anothernote.utils.changeNoteBackgroundColor
import com.anibalventura.anothernote.utils.discardDialog
import com.anibalventura.anothernote.utils.share
import com.anibalventura.anothernote.utils.toast
import kotlinx.android.synthetic.main.activity_main.*

class NoteUpdateFragment : Fragment() {

    // DataBinding.
    private var _binding: FragmentNoteUpdateBinding? = null
    private val binding get() = _binding!!

    // ViewModels.
    private val sharedViewModel: SharedViewModel by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()

    // Models.
    private lateinit var currentItem: NoteModel
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

        // Set toolbar color from note.
        activity?.toolbar?.setBackgroundColor(args.currentItem.color)

        // Get notify every time the database change.
        noteViewModel.getDatabase.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfNoteIsEmpty(data)
            adapter.setData(data)
        })

        // Set current items.
        setItems()

        // Handle back pressed for item changes.
        onBackPressed()

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setItems() {
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

    private fun onBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    currentItem = NoteModel(
                        args.currentItem.id,
                        binding.etNoteUpdateTitle.text.toString(),
                        binding.etNoteUpdateDescription.text.toString(),
                        (binding.clNoteUpdate.background as ColorDrawable).color
                    )
                    when {
                        currentItem != noteItem -> {
                            discardDialog(NOTE_DISCARD, requireContext(), requireView())
                        }
                        else -> findNavController().navigate(R.id.action_noteUpdateFragment_to_noteFragment)
                    }
                }
            })
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
                binding.clNoteUpdate, activity?.toolbar, requireContext()
            )
            R.id.menu_note_share
            -> share(requireContext(), args.currentItem.description)
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
        currentItem = NoteModel(
            args.currentItem.id,
            binding.etNoteUpdateTitle.text.toString(),
            binding.etNoteUpdateDescription.text.toString(),
            (binding.clNoteUpdate.background as ColorDrawable).color
        )

        // Insert data to database.
        noteViewModel.updateItem(currentItem)
        toast(requireContext(), R.string.update_successful)

        // Navigate back.
        findNavController().navigate(R.id.action_noteUpdateFragment_to_noteFragment)
    }

    // Destroy all references of the fragment to avoid memory leak.
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}