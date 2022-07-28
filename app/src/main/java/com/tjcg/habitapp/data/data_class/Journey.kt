package com.tjcg.habitapp.data.data_class

import android.graphics.drawable.Drawable

class Journey(val id:Long, val title:String, val description : String,
              val logo: Drawable,
              val keyResults : ArrayList<JourneyKeyResult>,
              val moreToExpect: Array<String>)