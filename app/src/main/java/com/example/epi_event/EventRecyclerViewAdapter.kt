package com.example.epi_event

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.epi_event.databinding.ActivitySignUpBinding

class EventRecyclerViewAdapter(
    val data: MutableList<EventObject>,
    val context: Activity,
    val onItemClickListener: View.OnClickListener,
) :
    RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvEventName: TextView = itemView.findViewById(R.id.event_item_tv_event_name)
        val tvEventOrganiser: TextView = itemView.findViewById(R.id.event_item_tv_event_organiser)
        val tvEventDate: TextView = itemView.findViewById(R.id.event_item_tv_event_date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowView: View =
            LayoutInflater.from(context).inflate(R.layout.event_item, parent, false)

        //For click response of recycler view
        rowView.setOnClickListener(onItemClickListener)

        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event: EventObject = data[position]

        holder.tvEventName.text = event.eventName
        holder.tvEventOrganiser.text = event.eventOrganiser
        holder.tvEventDate.text = event.eventDate

        holder.itemView.tag = position
    }

    override fun getItemCount(): Int {
        return data.size
        Log.d("Size", "" + data.size)
    }
}