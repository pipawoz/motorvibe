package com.utn.motorvibe.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.utn.motorvibe.R
import com.utn.motorvibe.fragments.listFragment.Companion.selected_motor_name


class plotFragment : Fragment() {
    lateinit var v : View

    private val db = FirebaseFirestore.getInstance()

    //private var db_readings: readingsDatabase? = null
    //private var readingsDao: readingsDao? = null
    //var readings: MutableList<Reading> = ArrayList<Reading>()

   lateinit var plotGraph : GraphView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_plot, container, false)
        plotGraph = v.findViewById(R.id.plot_graph)
        return v
    }

    override fun onStart() {
        super.onStart()
        //db_readings = readingsDatabase.getAppDataBase(v.context)
        //readingsDao = db_readings?.readingsDao()
    }

    override fun onResume() {
        super.onResume()

        val plot_data = LineGraphSeries<DataPoint>()

        plotGraph.title = selected_motor_name.plus(" Acceleration Readings [mm/s²]")

        var motor_readings = arrayListOf<Double>()

        db.collection("readings").document(listFragment.selected_motor_name).get().addOnSuccessListener {
            motor_readings = it.get("readings") as ArrayList<Double>
        } .addOnFailureListener { exception ->
            Log.d("TAG", "Motor didn't have readings",  exception)
        }

        val readings_count = motor_readings.size

        motor_readings?.forEachIndexed{ i, data ->
            if (data != null) {
                plot_data.appendData(DataPoint(i.toDouble(), data.toDouble()), true, readings_count)
            }
        }

        plot_data.isDrawDataPoints = true

        plotGraph.viewport.isXAxisBoundsManual = true
        plotGraph.viewport.isYAxisBoundsManual = true

        plotGraph.gridLabelRenderer.horizontalAxisTitle = " "
        plotGraph.gridLabelRenderer.verticalAxisTitle = " "
        plotGraph.gridLabelRenderer.padding = 15
        plotGraph.gridLabelRenderer.numHorizontalLabels = readings_count
        plotGraph.gridLabelRenderer.numVerticalLabels = 10

        plotGraph.viewport.setMaxX((readings_count+1).toDouble())
        plotGraph.viewport.setMaxY(5.0)

        plotGraph.viewport.isScrollable = true
        plotGraph.viewport.isScalable = true

        plotGraph.addSeries(plot_data)
    }
}

