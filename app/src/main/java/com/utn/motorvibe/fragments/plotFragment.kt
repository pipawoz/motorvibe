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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class plotFragment : Fragment() {
    lateinit var v : View

    private var db = FirebaseFirestore.getInstance()
    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + parentJob)
    lateinit var plotGraph : GraphView

    private lateinit var motorReadings : ArrayList<Double>

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

        scope.launch {
            motorReadings = getReadings() as ArrayList<Double>
        }
    }

    override fun onResume() {
        super.onResume()

        scope.launch {
            motorReadings = getReadings()  as ArrayList<Double>
        }

        plotGraph.title = selected_motor_name.plus(" Acceleration Readings [mm/s²]")

        val plotData = LineGraphSeries<DataPoint>()
        val readingsCount = motorReadings.size

        motorReadings?.forEachIndexed{ i, data ->
            if (data != null) {
                plotData.appendData(DataPoint(i.toDouble(), data), true, readingsCount)
            }
        }

        plotData.isDrawDataPoints = true

        plotGraph.viewport.isXAxisBoundsManual = true
        plotGraph.viewport.isYAxisBoundsManual = true

        plotGraph.gridLabelRenderer.horizontalAxisTitle = " "
        plotGraph.gridLabelRenderer.verticalAxisTitle = " "
        plotGraph.gridLabelRenderer.padding = 15
        plotGraph.gridLabelRenderer.numHorizontalLabels = readingsCount
        plotGraph.gridLabelRenderer.numVerticalLabels = 10

        plotGraph.viewport.setMaxX((readingsCount+1).toDouble())
        plotGraph.viewport.setMaxY(5.0)

        plotGraph.viewport.isScrollable = true
        plotGraph.viewport.isScalable = true

        plotGraph.addSeries(plotData)
    }

    private suspend fun getReadings(): ArrayList<Double> {
        db.collection("readings").document(selected_motor_name)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    var motorReadings: ArrayList<Double> = document.get("readings") as ArrayList<Double>
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
        return motorReadings
    }
}

