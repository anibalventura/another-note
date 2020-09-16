package com.anibalventura.anothernote.ui.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anibalventura.anothernote.R
import com.anibalventura.anothernote.Utils
import com.anibalventura.anothernote.data.models.NoteData
import com.anibalventura.anothernote.data.viewmodel.NoteViewModel
import com.anibalventura.anothernote.data.viewmodel.SharedViewModel
import com.anibalventura.anothernote.databinding.FragmentListBinding
import com.anibalventura.anothernote.ui.list.adapter.ListAdapter
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment() {

    private val noteViewModel: NoteViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val adapter: ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // DataBinding.
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.sharedViewModel = sharedViewModel

        // Setup RecyclerView
        setupRecyclerView()

        // Get notify every time the database change.
        noteViewModel.getAllData.observe(viewLifecycleOwner, { data ->
            sharedViewModel.checkIfDatabaseIsEmpty(data)
            adapter.setData(data)
        })

        // Set menu.
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        // Swipe to delete.
        swipeToDelete(recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeTODeleteCallBack = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]

                // Delete item.
                noteViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                // Restore deleted data.
                restoreDeletedItem(viewHolder.itemView, deletedItem, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeTODeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(view: View, deletedItem: NoteData, position: Int) {
        val snackBar = Snackbar.make(view, "Deleted \"${deletedItem.title}\"", Snackbar.LENGTH_LONG)
        snackBar.setAction("Undo") {
            noteViewModel.insertData(deletedItem)
            adapter.notifyItemChanged(position)
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.list_menu_delete_all -> confirmDeleteAll()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmDeleteAll() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Delete All")
        dialogBuilder.setMessage("Are you sure you want to delete everything?")
        dialogBuilder.setPositiveButton("Yes") { _, _ ->
            noteViewModel.deleteAll()
            Utils.showToast(requireContext(), "Successfully Deleted Everything.")
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