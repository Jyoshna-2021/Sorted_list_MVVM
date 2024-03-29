package com.example.sorted_list_view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sorted_list_view.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    val adapter = NotesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        val notesDao = NotesDatabase.getDatabase(applicationContext).notesDao()
        val notesRepository = NotesRepository(notesDao)

       val mainViewModel = ViewModelProvider(this, MainViewModelFactory(notesRepository))[MainViewModel::class.java]
        val decorator = DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL)
        decorator.setDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.divider)!!)
        recyclerView.addItemDecoration(decorator)
        binding.button2.setOnClickListener {
            if (binding.textET.text!!.isEmpty()) {
                binding.textET.error = "Empty"
            } else {
                mainViewModel.insertNotes(Notes(0, binding.textET.text.toString()))
                Toast.makeText(this, "DONE", Toast.LENGTH_SHORT).show()
                binding.textET.text = null
            }
        }
        recyclerView.adapter = adapter
        mainViewModel.getNotes().observe(this) { adapter.differ.submitList(it) }
        val sectionItemDecoration = ItemsDecoration(
            this,
            resources.getDimensionPixelSize(R.dimen.recycler_section_header_height),
            true,
            getSectionCallback()
        )
        recyclerView.addItemDecoration(sectionItemDecoration)
        //Swipe to delete
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
            attachToRecyclerView(binding.recyclerView) }

    }
    private fun getSectionCallback(): ItemsDecoration.SectionCallback {
        return object : ItemsDecoration.SectionCallback {
            override fun isSection(data: MutableList<Notes>, pos: Int): Boolean {
                return (pos == 0||data[pos].title[0]!=data[pos-1].title[0])
            }
            override fun getSectionHeaderName(data: MutableList<Notes>, pos: Int): String {
                return data[pos].title.subSequence(0,1) as String
            }
        }
    }

}







