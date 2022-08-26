package com.example.sorted_list_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class ItemsDecoration(
    private var context: Context,
    private var headerOffset: Int,
    private var sticky: Boolean,
    private var sectionCallback: SectionCallback
) :
    ItemDecoration() {
    private var headerView: View? = null
    private var tvTitle: TextView? = null
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val data=(parent.adapter as NotesAdapter).differ.currentList
        val pos = parent.getChildAdapterPosition(view)
        if (sectionCallback.isSection(data,pos)) {
            outRect.top = headerOffset
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (headerView == null) {
            headerView = inflateHeader(parent)
            tvTitle = headerView!!.findViewById<View>(R.id.list_item_section_text) as TextView
            fixLayoutSize(headerView, parent)
        }
        var prevTitle = ""
        val data=(parent.adapter as NotesAdapter).differ.currentList
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val childPos = parent.getChildAdapterPosition(child)
            val title = sectionCallback.getSectionHeaderName(data,childPos)
            tvTitle!!.text = title
            if (!prevTitle.equals(
                    title,
                    ignoreCase = true
                ) || sectionCallback.isSection(data, childPos)
            ) {
                drawHeader(c, child, headerView)
                prevTitle = title
            }


        }
    }

    private fun drawHeader(c: Canvas, child: View, headerView: View?) {
        c.save()
        if (sticky) {
            c.translate(0f, 0.coerceAtLeast(child.top - headerView!!.height).toFloat())
        } else {
            c.translate(0f, (child.top - headerView!!.height).toFloat())
        }
        headerView.draw(c)
        c.restore()
    }

    private fun fixLayoutSize(view: View?, viewGroup: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(viewGroup.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(viewGroup.height, View.MeasureSpec.UNSPECIFIED)
        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            viewGroup.paddingLeft + viewGroup.paddingRight,
            view!!.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            viewGroup.paddingTop + viewGroup.paddingBottom,
            view.layoutParams.height
        )
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    private fun inflateHeader(recyclerView: RecyclerView): View {
        return LayoutInflater.from(context)
            .inflate(R.layout.recycler_section_header, recyclerView, false)
    }

    interface SectionCallback {
        fun isSection(data: MutableList<Notes>, pos: Int): Boolean
        fun getSectionHeaderName(data: MutableList<Notes>, pos: Int): String
    }
}