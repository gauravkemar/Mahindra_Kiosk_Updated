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
import com.kemarport.mahindrakiosk.home.model.parking.ParkingListResponse

class HomeParkingAdapter : RecyclerView.Adapter<HomeParkingAdapter.ViewHolder>() {
    private var parkingList = mutableListOf<ParkingListResponse>()
    private var context: Context? = null
    private var truckParkingIcon: Int? = null
    fun setParkingList(
        parkingList: ArrayList<ParkingListResponse>,
        context: Context,
        truckParkingIcon: Int,
    ) {
        this.parkingList = parkingList
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
        var parkingMod: ParkingListResponse = parkingList.get(position)

        if(parkingMod.vrn.toString().toLowerCase()!= "null" || parkingMod.vrn!=null )
        {
            holder.tvDockQueue.text = parkingMod.vrn
        }
        else
        {
            holder.tvDockQueue.text = "NA"
        }

        if(parkingMod.vendorName.toString().toLowerCase()!= "null" || parkingMod.vendorName!=null  )
        {
            holder.tvSupplierName.text = parkingMod.vendorName
        }
        else
        {
            holder.tvSupplierName.text = "NA"
        }
/*        truckParkingIcon?.let { holder.tvDockQueue.setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0) }*/
        truckParkingIcon?.let { holder.tvTruckImg.setImageResource(it) }
    /*    if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(dockList[position])
                }
            }
        }*/

    /*    holder.itemView.setBackgroundColor(
            if (position == highlightedPosition) ContextCompat.getColor(
                context!!,
                R.color.light_blue
            ) else ContextCompat.getColor(context!!, R.color.transparent)
        )*/
    }

   /* fun highlightItem(position: Int) {
        highlightedPosition = position
        if (position != -1)
            onItemClickListener?.let {
                it(dockList[position])
            }
        notifyDataSetChanged()
    }*/

    override fun getItemCount(): Int {
        if (parkingList.size == 0) {
           // Toast.makeText(context, "List is empty", Toast.LENGTH_LONG).show()
        } else {
            return parkingList.size
        }
        return parkingList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDockQueue: TextView = itemView.findViewById(R.id.home_dock_queue)
        val tvSupplierName: TextView = itemView.findViewById(R.id.suplier_name)
        val tvTruckImg: ImageView = itemView.findViewById(R.id.ic_truck)
    }


/*    private var onItemClickListener: ((ListOfDockReportingVehicleResponseItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (ListOfDockReportingVehicleResponseItem) -> Unit) {
        onItemClickListener = listener
    }*/
}