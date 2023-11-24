package com.example.supersoiree.service

import android.util.Log
import com.example.supersoiree.model.PubResponse
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

abstract class SearchPub : Callback {
    companion object{
        private const val TAG="SearchPub Callback"
    }

    abstract fun response(data: PubResponse)

    override fun onFailure(call: Call, e: IOException) {
        Log.e(TAG, " -> Failed to get query", e)
    }

    override fun onResponse(call: Call, response: Response) {
        Log.e(TAG, " -> Query executed")

        val gson = Gson()
        val data: PubResponse = gson.fromJson(response.body!!.charStream(), PubResponse::class.java)

        response(data)
    }
}