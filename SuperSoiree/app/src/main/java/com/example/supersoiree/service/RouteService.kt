package com.example.supersoiree.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request

class RouteService : Service(){
    companion object { private const val TAG = "RouteService" }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }



    private val key = "716d5b33-b8a5-4dc2-a6d7-d8e3a095a330"

    fun getRoute(lat1 : Double, lon1 : Double, lat2 : Double, lon2 : Double, callback : SearchRoute) {

        val link = "https://graphhopper.com/api/1/route?point=${lat1},${lon1}&point=${lat2},${lon2}&profile=foot&vehicle=car&key=${key}&type=json&points_encoded=false"

        Log.d(TAG, link)

        val client = OkHttpClient()
        val request: Request = Request.Builder().url("https://graphhopper.com/api/1/route?point=${lat1},${lon1}&point=${lat2},${lon2}&profile=foot&vehicle=car&key=${key}&type=json&points_encoded=false").build()

        client.newCall(request).enqueue(callback)
        Log.d(TAG, " Routing !")
    }



}