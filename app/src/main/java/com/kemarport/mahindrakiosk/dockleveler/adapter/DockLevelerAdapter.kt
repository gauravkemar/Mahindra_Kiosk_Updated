package com.kemarport.mahindrakiosk.dockleveler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.dockleveler.model.CurrentDockLevelerResponse
import com.kemarport.mahindrakiosk.dockleveler.model.DockLevelerModel

class DockLevelerAdapter: RecyclerView.Adapter<DockLevelerAdapter.ViewHolder>() {

    private var dockList = mutableListOf<CurrentDockLevelerResponse>()
    private var context: Context?=null
    var onItemStateChanged: ((position: Int, isEnabled: Boolean) -> Unit)? = null
    fun setDockLevelerList(
        dockList: ArrayList<CurrentDockLevelerResponse>,
        context: Context,
    ) {
        this.dockList = dockList
        this.context=context
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.leveler_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dockMod:CurrentDockLevelerResponse=dockList.get(position)
        holder.tvDockLevelerQueue.text=dockMod.levelerNumber
        if(dockMod.isActive==true)
        {
            holder.radioGroupLeveler.check(holder.radioBtn1.getId())
        }
        else
        {
            holder.radioGroupLeveler.check(holder.radioBtn2.getId())
        }

        holder.radioGroupLeveler.setOnCheckedChangeListener { group, checkedId ->
            val isActive = checkedId == holder.radioBtn1.id
            onItemStateChanged?.invoke(position, isActive) // Communicate state change
        }

    }

    override fun getItemCount(): Int {
        if(dockList.size==0){
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        }else{
            return dockList.size
        }
        return dockList.size
    }
    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDockLevelerQueue: TextView = itemView.findViewById(R.id.leveler_tv)
        val radioGroupLeveler: RadioGroup = itemView.findViewById(R.id.radioGroupLeveler)
        val radioBtn1: AppCompatRadioButton = itemView.findViewById(R.id.radioBtn1)
        val radioBtn2: AppCompatRadioButton = itemView.findViewById(R.id.radioBtn2)
    }
}