package com.kemarport.mahindrakiosk.unloadingstart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.unloadingstart.model.UnloadingDockQueueModel

class UnloadingStartAdapter : RecyclerView.Adapter<UnloadingStartAdapter.ViewHolder>(){
    private var dockList = mutableListOf<UnloadingDockQueueModel>()
    private var context: Context?=null
    fun setDockList(
        dockList: ArrayList<UnloadingDockQueueModel>,
        context: Context,
    ) {
        this.dockList = dockList
        this.context = context
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dock_queue_rc_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dockMod:UnloadingDockQueueModel=dockList.get(position)
        holder.tvDockQueue.text=dockMod.dockQueue
    }

    override fun getItemCount(): Int {
        if(dockList.size==0){
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        }else{
            return dockList.size
        }
        return dockList.size
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDockQueue: TextView = itemView.findViewById(R.id.tv_dock_queue_unload)
    }
}