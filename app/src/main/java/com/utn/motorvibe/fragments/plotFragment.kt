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
import kotlinx.coroutines.tasks.await

class plotFragment : Fragment() {
    lateinit var v: View

    private var db = FirebaseFirestore.getInstance()
    private val parentJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + parentJob)
    private lateinit var plotGraph: GraphView

    private lateinit var motorReadings: ArrayList<Double>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_plot, container, false)
        plotGraph = v.findViewById(R.id.plot_graph)
        return v
    }

    override fun onResume() {
        super.onResume()

        plotGraph.title = selected_motor_name.plus(resources.getString(R.string.plot_title))

        plotGraph.viewport.isXAxisBoundsManual = true
        plotGraph.viewport.isYAxisBoundsManual = true

        plotGraph.gridLabelRenderer.horizontalAxisTitle = " "
        plotGraph.gridLabelRenderer.verticalAxisTitle = " "
        plotGraph.gridLabelRenderer.padding = 15
        plotGraph.viewport.setMaxY(3.5)

        plotGraph.viewport.isScrollable = false
        plotGraph.viewport.isScalable = false


        scope.launch {
            motorReadings = getReadings()
            plotReadings()
        }
    }

    private fun plotReadings() {
        val plotData = LineGraphSeries<DataPoint>()
        plotData.isDrawDataPoints = true

        val readingsCount = motorReadings.size

        motorReadings?.forEachIndexed { i, data ->
            plotData.appendData(DataPoint(i.toDouble(), data), true, readingsCount)
        }

        plotGraph.gridLabelRenderer.numHorizontalLabels = readingsCount
        plotGraph.gridLabelRenderer.numVerticalLabels = 10

        plotGraph.viewport.setMaxX((readingsCount + 1).toDouble())
        plotGraph.addSeries(plotData)
    }

    private suspend fun getReadings(): ArrayList<Double> {
        motorReadings = ArrayList<Double>()
        val query = db.collection("readings").document(selected_motor_name)
        try {
            val data = query.get().await()
            motorReadings = data.get("readings") as ArrayList<Double>
            Log.d("TAG", "DocumentSnapshot data: ${data.data}")
        } catch (e: Exception) {
            Log.d("TAG", "getReadings failed ", e)
        }
        return motorReadings
    }
}

