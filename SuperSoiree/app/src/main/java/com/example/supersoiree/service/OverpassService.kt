package com.example.supersoiree.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import okhttp3.OkHttpClient
import okhttp3.Request


class OverpassService: Service() {
    private companion object{
        private const val TAG="OverPassService"
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    fun searchPub( pos: Pair<Double, Double>, dist: Int, callback: SearchPub) {

        val boxData = calculateDistance(pos, dist)

        val boxCoord = "bbox:" + boxData[0].first.toString() + "," +
                boxData[0].second.toString() + "," +
                boxData[1].first.toString() + "," +
                boxData[1].second.toString()

        Log.d(TAG, boxCoord)
        val name="McDonald's"
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(
        "http://overpass-api.de/api/interpreter?data=[${boxCoord}][out:json];nwr[amenity=pub];nwr[amenity=bar];out center;").build()

        client.newCall(request).enqueue(callback)
        Log.d(TAG, " -> Searching pub ")
        Log.d(TAG, dist.toString())
    }

    private fun calculateDistance(pos: Pair<Double, Double>, dist: Int): ArrayList<Pair<Double, Double>> {

        // En attendant cela fonctionne à peu près MAIS on va utiliser la formule de Haversine

        val degrees : Int = dist / 5

        val array = arrayListOf<Pair<Double, Double>>()

        val smallCoords = Pair(pos.first - degrees * 0.0001, pos.second - degrees * 0.0001)
        array.add(smallCoords)

        val bigCoords = Pair(pos.first + degrees * 0.0001, pos.second + degrees * 0.0001)
        array.add(bigCoords)

        return(array)
    }

    fun findThisPub(lat:Double,lon:Double){
        val request: Request=Request.Builder().url("http:/overpass-api.de/api/interpreter?data=[out:json]").build()
    }


    override fun onCreate() {
        super.onCreate()


    }



}