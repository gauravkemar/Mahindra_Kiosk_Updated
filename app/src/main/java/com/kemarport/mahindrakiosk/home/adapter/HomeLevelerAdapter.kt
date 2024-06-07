package com.kemarport.mahindrakiosk.home.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.kemarport.mahindrakiosk.R
import com.kemarport.mahindrakiosk.home.model.leveler.LevelerListResponse
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.media.MediaPlayer
class HomeLevelerAdapter: RecyclerView.Adapter<HomeLevelerAdapter.ViewHolder>() {
    private var levelerList = mutableListOf<LevelerListResponse>()
    private var context: Context?=null

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, R.raw.warning_buzzer)
    }
    fun setLevelerList(

        newlevelerList: ArrayList<LevelerListResponse>,
        context: Context,
    ) {
        this.levelerList = newlevelerList
        this.context=context

        val startIndex = levelerList.size // Store the current size of the list
        levelerList.addAll(newlevelerList.filter { isNewItem(it) }) // Add only new items
        notifyItemRangeInserted(startIndex, levelerList.size - startIndex)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_leveler_item_layout, parent, false)
        return  ViewHolder(view)
    }

    private fun isNewItem(newItem: LevelerListResponse): Boolean {
        // Check if the new item already exists in the list based on a unique identifier (e.g., id)
        return levelerList.none { it.vrn == newItem.vrn }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var levelerMod:LevelerListResponse=levelerList.get(position)
        holder.tvLeveler.text=levelerMod.leverlerName.toString()
        //holder.tvLevelerVehicle.text=levelerMod.vrn
        //holder.progress_bar.setProgress(70,true)


        // holder.clStart.setBackgroundColor(levelerMod.color)
        /* holder.tvLevelerValue.text=levelerMod.dockQueue
         holder.tvLevelerTime.text=levelerMod.time
         holder.tvLevelerName.text=levelerMod.name*/

        if(levelerMod.vrn.toString().toLowerCase()!= "null" || levelerMod.vrn!=null)
        {
            holder.tvContractors.setText("Unloading Started")
            holder.tvLevelerVehicle.text=levelerMod.vrn.toString()
            holder.materialCardView.setOnClickListener {
                onItemClickListener?.let {
                    it(levelerList[position])
                }
            }
            if(levelerMod.supplierName.toString().toLowerCase()!= "null" || levelerMod.supplierName!=null)
            {
                holder.tvLevelerName.text=levelerMod.supplierName.toString()
            }
            else{
                holder.tvLevelerName.text="NA"
            }

            if(levelerMod.unloadingStartTime.toString().toLowerCase()!= "null" || levelerMod.unloadingStartTime!=null)
            {
                holder.tvTimer.text=formatDate(levelerMod.unloadingStartTime.toString())
            }
            else{
                holder.tvTimer.text="NA"
            }
        }
        else{
            holder.tvContractors.setText("Available")
            if(levelerMod.supplierName.toString().toLowerCase()!= "null" || levelerMod.supplierName!=null)
            {
                holder.tvLevelerName.text=levelerMod.supplierName.toString()
            }
            else{
                holder.tvLevelerName.text="Available"
            }
        }


        setLevelerBgColorFromStatus(levelerMod,holder )

    }
    private fun setLevelerBgColorFromStatus(
        levelerMod: LevelerListResponse,
        clStart: ViewHolder
    ) {
        if(levelerMod.isOverTime == true)
        {
            clStart.clStart.setBackgroundColor(  ContextCompat.getColor(context!!, R.color.radio_shortage_color))
            clStart.tvTimer.setTextColor(Color.RED)
            playBuzzerSound()

        }else if(levelerMod.isOverTime == false && levelerMod.channelType.equals("GreenLeveler"))
        {
            clStart.clStart.setBackgroundColor(  ContextCompat.getColor(context!!, R.color.blue))
        }
        else if(levelerMod.isLevelerFree!! && levelerMod.channelType.equals("NormalLeveler")){
            clStart.clStart.setBackgroundColor(  ContextCompat.getColor(context!!, R.color.green))
        }
        else if(!levelerMod.isLevelerFree){
            clStart.clStart.setBackgroundColor( ContextCompat.getColor(context!!, R.color.light_orange))
        }

    }
    private fun playBuzzerSound() {
        // Check if the media player is playing
        if (!mediaPlayer.isPlaying) {
            // Start playing the buzzer sound
            mediaPlayer.start()

            // Set a listener to release the media player when playback is complete
            mediaPlayer.setOnCompletionListener {
               // mediaPlayer.release()
            }
        }
    }
    override fun getItemCount(): Int {
        if(levelerList.size==0){
            //Toast.makeText(context,"List is empty", Toast.LENGTH_LONG).show()
        }else{
            return levelerList.size
        }
        return levelerList.size
    }
    class ViewHolder (ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvLeveler: TextView = itemView.findViewById(R.id.tv_leveler)
        val tvLevelerName: TextView = itemView.findViewById(R.id.tv_supervisor)
        val tvLevelerVehicle: TextView = itemView.findViewById(R.id.tv_vehicle_no)
        val clStart: ConstraintLayout = itemView.findViewById(R.id.clStart)
        val tvTimer:TextView=itemView.findViewById(R.id.tv_timer)
        val tvContractors:TextView=itemView.findViewById(R.id.tv_contractors)
        val materialCardView:MaterialCardView=itemView.findViewById(R.id.cardview_main)
        // val progress_bar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        /*val tvLevelerValue: TextView = itemView.findViewById(R.id.tv_leveler_value)
        val tvLevelerTime: TextView = itemView.findViewById(R.id.tv_leveler_time)
        val tvLevelerName: TextView = itemView.findViewById(R.id.tv_leveler_name)*/
    }
    private var onItemClickListener: ((LevelerListResponse) -> Unit)? = null

    fun setOnItemClickListener(listener: (LevelerListResponse) -> Unit) {
        onItemClickListener = listener
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        try {
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            return inputDate // Return the original string in case of an error
        }
    }

    /*private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }*/
}