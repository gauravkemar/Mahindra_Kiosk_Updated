package com.kemarport.mahindrakiosk.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.home.model.dock.ListOfDockReportingVehicleResponseItem
import kotlin.collections.ArrayList

class HomeDockAdapter : RecyclerView.Adapter<HomeDockAdapter.ViewHolder>() {
    private var dockList = mutableListOf<ListOfDockReportingVehicleResponseItem>()
    private var context: Context? = null
    private var truckParkingIcon: Int? = null
    private var highlightedPosition = -1
    fun setDockList(
        dockList: ArrayList<ListOfDockReportingVehicleResponseItem>,
        context: Context,
        truckParkingIcon: Int,
    ) {
        this.dockList = dockList
        this.context = context
        this.truckParkingIcon = truckParkingIcon
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_dock_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dockMod: ListOfDockReportingVehicleResponseItem = dockList.get(position)
        if(dockMod.vrn.toString().toLowerCase()!= "null" || dockMod.vrn!=null )
        {
            holder.tvDockQueue.text = dockMod.vrn

        }
        else
        {
            holder.tvDockQueue.text = "NA"
        }


        if(dockMod.supplier.toString().toLowerCase()!= "null" || dockMod.supplier!=null )
        {
            holder.tvSupplierName.text = dockMod.supplier.toString()
        }
        else
        {
            holder.tvSupplierName.text = "NA"
        }

/*        truckParkingIcon?.let { holder.tvDockQueue.setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0) }*/
        truckParkingIcon?.let { holder.tvTruckImg.setImageResource(it) }
        if(onItemClickListener!=null)
        {
            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(dockList[position])
                }
            }
        }
        if(dockMod.isGreenChannel == true)
        {
            holder.imgChennal.visibility=View.VISIBLE
        }
        else
        {
            holder.imgChennal.visibility=View.INVISIBLE
        }
        holder.itemView.setBackgroundColor(
            if (position == highlightedPosition) ContextCompat.getColor(
                context!!,
                R.color.light_blue
            ) else ContextCompat.getColor(context!!, R.color.transparent)
        )
    }

    fun highlightItem(position: Int) {
        highlightedPosition = position
        if (position != -1)
            onItemClickListener?.let {
                it(dockList[position])
            }
        notifyDataSetChanged()
    }
    fun removeItem(position: Int) {
        if (position in 0 until dockList.size) {
            dockList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    override fun getItemCount(): Int {
        if (dockList.size == 0) {
            //Toast.makeText(context, "List is empty", Toast.LENGTH_LONG).show()
        } else {
            return dockList.size
        }
        return dockList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDockQueue: TextView = itemView.findViewById(R.id.home_dock_queue)
        val tvSupplierName: TextView = itemView.findViewById(R.id.suplier_name)
        val tvTruckImg: ImageView = itemView.findViewById(R.id.ic_truck)
        val imgChennal: ImageView = itemView.findViewById(R.id.img_chennal)
    }


    private var onItemClickListener: ((ListOfDockReportingVehicleResponseItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (ListOfDockReportingVehicleResponseItem) -> Unit) {
        onItemClickListener = listener
    }

}