package com.kemarport.mahindrakiosk.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.home.model.RgpModel
import com.kemarport.mahindrakiosk.home.model.leveler.LevelerListResponse

class HomeRgpAdapter: RecyclerView.Adapter<HomeRgpAdapter.ViewHolder>() {

    private var rgpModel = mutableListOf<LevelerListResponse>()
    private var context: Context?=null
    fun setRgpList(
        rgpList: ArrayList<LevelerListResponse>,
        context: Context,
    ) {
        this.rgpModel = rgpList
        this.context=context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rgp_list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var rgpMod:LevelerListResponse=rgpModel.get(position)
        if(rgpMod.vrn.toString().toLowerCase()!= "null" || rgpMod.vrn!=null   )
        {
            holder.tvVehicleNo.text=rgpMod.vrn
        }

        if(rgpMod.supplierName.toString().toLowerCase()!= "null" || rgpMod.supplierName!=null  )
        {
            holder.tvSupplierName.text=rgpMod.supplierName.toString()
        }

        if(rgpMod.channelType.equals("GreenLeveler"))
        {
            holder.imgChennal.visibility=View.VISIBLE
        }
        else
        {
            holder.imgChennal.visibility=View.INVISIBLE
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(rgpModel[position])
            }
        }
    }

    override fun getItemCount(): Int {
        if(rgpModel.size==0){
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        }else{
            return rgpModel.size
        }
        return rgpModel.size
    }
    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvVehicleNo: TextView = itemView.findViewById(R.id.tv_vehicle_no)
        val tvSupplierName: TextView = itemView.findViewById(R.id.tv_vehicle_name)
        val imgChennal: ImageView = itemView.findViewById(R.id.img_chennal)
    }
    private var onItemClickListener: ((LevelerListResponse) -> Unit)? = null

    fun setOnItemClickListener(listener: (LevelerListResponse) -> Unit) {
        onItemClickListener = listener
    }
}