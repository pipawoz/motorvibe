package com.utn.motorvibe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.utn.motorvibe.R
import com.utn.motorvibe.entities.Motor

class MotorListAdapter(
    private var motorList: MutableList<Motor>,
    private val gridMode: String,
    private val displayImages: Boolean,
    val onItemClick: (Int) -> Boolean
) : RecyclerView.Adapter<MotorHolder>() {
    companion object {
        private val TAG = "MotorListAdapter"
    }

    private var selectedImage: String? = ""

    private var imagesList =
        listOf("motor1", "motor2", "motor3", "motor4", "motor5", "motor6", "motor7")
    private var modelList =
        listOf("W22", "IEEE 841", "Cooling Tower", "Crusher Duty", "W40", "Aegis 1", "JP-Aegis")

    //private var motor_images = hashMapOf("W22" to "motor1", "IEEE 841" to "motor2", "Cooling Tower" to "motor3",
    //"Crusher Duty" to "motor4", "W40" to "motor5", "Aegis 1" to "motor7", "JP-Aegis" to "motor7")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotorHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_motor_list, parent, false)

        if (gridMode == "grid_mode_grid") {
            view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_motor_grid, parent, false)
        }
        return (MotorHolder(view))
    }

    override fun getItemCount(): Int {
        return motorList.size
    }

    fun setData(newData: ArrayList<Motor>) {
        this.motorList = newData
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MotorHolder, position: Int) {
        holder.setName(motorList[position].name)
        holder.setModel(motorList[position].model)
        holder.setStatus("Status: ".plus(motorList[position].status))

        selectedImage = imagesList[modelList.indexOf(motorList[position].model)]
        val id = holder.getImageView().context.resources.getIdentifier(
            selectedImage,
            "drawable",
            holder.getImageView().context.packageName
        )

        if (displayImages) {
            holder.getImageView().setImageResource(id)
        } else {
            holder.getImageView().setImageResource(R.drawable.motoricon)
        }

        holder.getCardLayout().setOnClickListener {  //For each card, set listener
            onItemClick(position)
        }
    }

}


class MotorHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View = v

    fun setName(name: String) {
        val txtName: TextView = view.findViewById(R.id.txt_name_item)
        txtName.text = name
    }

    fun setModel(model: String) {
        val txtModel: TextView = view.findViewById(R.id.txt_model_item)
        txtModel.text = model
    }

    fun setStatus(status: String) {
        val txtStatus: TextView = view.findViewById(R.id.txt_status_item)
        txtStatus.text = status
    }

    fun getCardLayout(): CardView {
        return view.findViewById(R.id.card_package_item)
    }

    fun getImageView(): ImageView {
        return view.findViewById(R.id.img_item)
    }

}