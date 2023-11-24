package com.example.supersoiree.ui

import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.supersoiree.ListenerFunction
import com.example.supersoiree.MainActivity
import com.example.supersoiree.R
import com.example.supersoiree.SecondFragment
import com.example.supersoiree.model.PubResponse
import com.example.supersoiree.service.OverpassService
import com.example.supersoiree.service.SearchPub
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint


class BottomSheetFragment : BottomSheetDialogFragment() {


    private var functionListener: ListenerFunction? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.behavior.peekHeight = 200
        return dialog
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler(Looper.getMainLooper())
        var launchButton : Button = requireView().findViewById(R.id.start_pub_itinerary)
        val pubName=arguments?.getString("name")
        var pubNameText:TextView=requireView().findViewById(R.id.pubName)
        pubNameText.text=pubName
        var description:TextView=requireView().findViewById(R.id.pub_description)
        var opening_hours:TextView=requireView().findViewById(R.id.hours)

        GlobalScope.launch {
            functionListener?.getLoc(){location ->
                var pair=Pair(location.latitude,location.longitude)
                OverpassService().searchPub(pair,10000,object :SearchPub(){
                    override fun response(data: PubResponse) {
                        handler.post {
                            for(pub in data.elements){
                                if(pub.tags.name==pubName){
                                    description.text=pub.tags.description
                                    Log.d(TAG,"${pub.tags.description}")
                                    opening_hours.text=pub.tags.opening_hours
                                }
                            }
                        }

                    }

                })
            }

        }

        var favButton : ImageButton=requireView().findViewById(R.id.fav)
        favButton.setOnClickListener{
            if (pubName != null) {
                MainActivity().setToFav(pubName)
            }
        }
        launchButton.setOnClickListener{
            val latitude = arguments?.getDouble("latitude")
            val longitude = arguments?.getDouble("longitude")
            val myLat=arguments?.getDouble("myLat")
            val myLon=arguments?.getDouble("myLon")
            val centerPoint=GeoPoint(latitude!!,longitude!!)
            if (latitude != null) {
                if (longitude != null) {
                    if (myLat != null) {
                        if (myLon != null) {
                            functionListener?.onFunctionCalled(longitude,latitude,myLon,myLat,centerPoint)
                        }
                    }
                }
            }
        }
    }



    fun setFunctionListener(listener: ListenerFunction) {
        functionListener = listener
    }

    // Reste de votre bottomsheet
    // ...



    companion object {
        const val TAG="BottomSheet"
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}