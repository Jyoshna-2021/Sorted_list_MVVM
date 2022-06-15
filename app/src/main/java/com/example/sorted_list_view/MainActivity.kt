package com.example.sorted_list_view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.sorted_list_view.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        val notesDao = NotesDatabase.getDatabase(applicationContext).notesDao()
        val notesRepository = NotesRepository(notesDao)
        val mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(notesRepository)
        )[MainViewModel::class.java]
        val adapter = NotesAdapter()

        recyclerView.adapter = adapter

        mainViewModel.getNotes().observe(this) {
            adapter.differ.submitList(it)
        }
        binding.button2.setOnClickListener {
            if (binding.textET.text!!.isEmpty()) {
                binding.textET.error = "Empty"
            } else {
                mainViewModel.insertNotes(Notes(0, binding.textET.text.toString()))
                Toast.makeText(this, "DONE", Toast.LENGTH_SHORT).show()
                binding.textET.text = null
            }
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.bindingAdapterPosition
                val notes = adapter.differ.currentList[pos]

                mainViewModel.deleteNode(notes)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerView)
        }
    }
}





