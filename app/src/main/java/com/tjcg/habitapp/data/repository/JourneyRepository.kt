package com.tjcg.habitapp.data.repository

import android.util.Log
import com.tjcg.habitapp.data.data_class.Journey
import com.tjcg.habitapp.viewmodel.JourneyViewModel

object JourneyRepository {

    lateinit var jvModel : JourneyViewModel
    var isRepositorySet = false

    fun setRepository(viewModel: JourneyViewModel) {
        jvModel = viewModel
        isRepositorySet = true
    }

    fun storeAllJourneys(journeys: ArrayList<Journey>) {
        jvModel.setAllJourney(journeys)
    }

    fun setIdForDescription(id: Long) {
        jvModel.setDescriptionId(id)
    }

    fun getJourneyFromId(id: Long) : Journey {
        var journey = jvModel.allJourney.value?.get(0)
        val journeys = jvModel.allJourney.value
        if (!journeys.isNullOrEmpty()) {
            for (i in journeys) {
                if (i.id == id) {
                    journey = i
                }
            }
        }
        Log.d("repository", "Journey: ${journey?.id} , ${journey?.title}")
        return journey!!
    }

}