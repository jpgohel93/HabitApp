package com.tjcg.habitapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tjcg.habitapp.data.data_class.Journey

class JourneyViewModel : ViewModel() {
    // for handling journeyDescriptionFragment
    private val _showDescriptionOf = MutableLiveData<Long>(0L)
    val showDescriptionOf : LiveData<Long> = _showDescriptionOf

    fun setDescriptionId(id:Long) {
        _showDescriptionOf.value = id
    }

    // store all journeys
    private val _allJourneys = MutableLiveData<ArrayList<Journey>>()
    var allJourney : LiveData<ArrayList<Journey>> = _allJourneys

    fun setAllJourney(journeys: ArrayList<Journey>) {
        _allJourneys.value = journeys
        Log.d("jvModel", "all journey set: ${_allJourneys.value?.size}")
    }
}