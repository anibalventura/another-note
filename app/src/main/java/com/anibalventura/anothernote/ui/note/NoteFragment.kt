package com.anibalventura.anothernote.ui.note

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.data.models.ArchiveData
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.models.TrashData
import com.anibalventura.anothernote.data.viewmodel.ArchiveViewModel
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.data.viewmodel.TrashViewModel
import com.anibalventura.anothernote.databinding.FragmentNoteBinding
import com.anibalventura.anothernote.ui.note.adapter.NoteAdapter
import com.anibalventura.anothernote.utils.SwipeLeft
import com.anibalventura.anothernote.utils.SwipeRight
import com.anibalventura.anothernote.utils.hideKeyboard
import com.anibalventura.anothernote.utils.showToast
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class NoteFragment : Fragment(), SearchView.OnQueryTextListener {

    // ViewModels
    private val noteViewModel: NoteViewModel by viewModels()
    private val archiveViewModel: ArchiveViewModel by viewModels()
    private val trashViewModel: TrashViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    // DataBinding.
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private val adapter: NoteAdapter by lazy { NoteAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel

        // Setup RecyclerView
        setupRecyclerView()

        // Get notify every time the database change.
        noteViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfNoteIsEmpty(data)
            adapter.setData(data)
        })

        // Set menu.
        setHasOptionsMenu(true)

        // Hide soft keyboard.
        hideKeyboard(requireActivity())

        return binding.root
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300 // Milliseconds
        }

        // Swipe to delete.
        swipeToDelete(recyclerView)

        // Swipe to archive.
        swipeToArchive(recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeTODeleteCallBack = object : SwipeLeft() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]

                val trashItem = TrashData(
                    adapter.dataList[viewHolder.adapterPosition].id,
                    adapter.dataList[viewHolder.adapterPosition].title,
                    adapter.dataList[viewHolder.adapterPosition].description
                )

                // Send item to trash.
                trashViewModel.insertData(trashItem)

                // Delete item.
                noteViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                // Restore deleted data.
                restoreDeletedItem(viewHolder.itemView, deletedItem, trashItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeTODeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(view: View, deletedItem: NoteData, trashItem: TrashData) {
        val snackBar = Snackbar.make(view, "Deleted \"${deletedItem.title}\"", Snackbar.LENGTH_LONG)
        snackBar.setAction("Undo") {
            noteViewModel.insertData(deletedItem)
            trashViewModel.deleteItem(trashItem)
        }.show()
    }

    private fun swipeToArchive(recyclerView: RecyclerView) {
        val swipeTODeleteCallBack = object : SwipeRight() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]

                val archiveItem = ArchiveData(
                    adapter.dataList[viewHolder.adapterPosition].id,
                    adapter.dataList[viewHolder.adapterPosition].title,
                    adapter.dataList[viewHolder.adapterPosition].description
                )

                // Send item to archive.
                archiveViewModel.insertData(archiveItem)

                // Delete item.
                noteViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                // Restore deleted data.
                restoreArchiveItem(viewHolder.itemView, deletedItem, archiveItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeTODeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreArchiveItem(view: View, deletedItem: NoteData, archiveItem: ArchiveData) {
        val snackBar = Snackbar.make(view, "Archive \"${deletedItem.title}\"", Snackbar.LENGTH_LONG)
        snackBar.setAction("Undo") {
            noteViewModel.insertData(deletedItem)
            archiveViewModel.deleteItem(archiveItem)
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note, menu)

        val search = menu.findItem(R.id.list_menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.list_menu_delete_all -> confirmDeleteAll()
            R.id.list_menu_title -> noteViewModel.sortByTitle.observe(this, {
                adapter.setData(it)
            })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchTroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchTroughDatabase(query)
        }
        return true
    }

    private fun searchTroughDatabase(query: String) {
        noteViewModel.searchDatabase("%$query%").observe(this, { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }

    private fun confirmDeleteAll() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Delete All")
        dialogBuilder.setTitle("Delete All")
        dialogBuilder.setMessage("Are you sure you want to delete everything?")
        dialogBuilder.setPositiveButton("Yes") { _, _ ->
            noteViewModel.deleteAll()
            showToast(requireContext(), "Successfully Deleted Everything.")
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