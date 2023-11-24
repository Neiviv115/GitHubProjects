package com.example.supersoiree

import android.Manifest
import android.app.Activity
import android.content.Context
import kotlin.coroutines.suspendCoroutine
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.supersoiree.databinding.FragmentSecondBinding
import com.example.supersoiree.model.PubResponse
import com.example.supersoiree.model.SearchRouteResponse
import com.example.supersoiree.service.OverpassService
import com.example.supersoiree.service.RouteService
import com.example.supersoiree.service.SearchPub
import com.example.supersoiree.service.SearchRoute
import com.example.supersoiree.ui.BottomSheetFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.api.IGeoPoint
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


//@Suppress("DEPRECATION")

class SecondFragment : Fragment(),ListenerFunction {
    companion object{
        private const val TAG="Mapping"
    }

    private lateinit var map: MapView
    private lateinit var mapController : IMapController // Comes from the interface "IMapController" of the osm library
    val myPath = Polyline()

    private val listItems = arrayListOf<OverlayItem>() // Making a list of items to show on the map
    private lateinit var overlay: ItemizedOverlayWithFocus<OverlayItem> // Corresponds to the UI to be displayed above the map
   // private lateinit var centerPoint:GeoPoint
    val listLines = arrayListOf<Polyline>()

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!
    private var myContext: Context? =null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

    override fun onDetach() {
        super.onDetach()
        myContext = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listLines.add(Polyline())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {


        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map = requireView().findViewById(R.id.map)
        mapController = map.controller
        map.setTileSource(TileSourceFactory.MAPNIK)
       // val startPoint = GeoPoint(45, 5) // Initialising the first coordinates
        val mapController =
            map.controller  // Comes from the interface "IMapController" of the osm library

        mapController.setZoom(15.0)         // Setting arbitrary zoom level

        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false)
        //mapController.setCenter(startPoint) // We put the center of the map to the coordinates we found earlier
        val listItems = arrayListOf<OverlayItem>() // Making a list of items to show on the map

        GlobalScope.launch {
            mainFunction()

        }


    }

     fun mainFunction(){
        GlobalScope.launch {
            try {
                var location=locateUser(LocationServices.getFusedLocationProviderClient(requireActivity()))
                var args: Array<Any> =findPubVicinity(location)
                loadOverlay(args[0] as DoubleArray, args[1] as Pair<Double, Double>,
                    args[2] as ArrayList<String>)
            }catch (e:java.lang.Exception){
                Log.e(TAG,"failed to launch coroutine")
            }


        }


    }

    override fun onDestroy() {
        super.onDestroy()
        myContext=null




    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    // Demande de la permission d'accès à la géolocalisation
    private val LOCATION_PERMISSION_REQUEST_CODE = 100




    // Gestion de la réponse de l'utilisateur à la demande de permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // L'utilisateur a accordé la permission, vous pouvez continuer avec la géolocalisation
                    // ou appeler une fonction pour démarrer la géolocalisation
                } else {
                    // L'utilisateur a refusé la permission, vous pouvez afficher un message ou prendre une autre action
                }
            }
        }
    }


    /*private fun locateUser(provider: FusedLocationProviderClient)  {
        val request = provider.getCurrentLocation(
            LocationRequest.QUALITY_HIGH_ACCURACY,

            // Cancellation token : used when willing to sending location requests
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            }
        );if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
        }


        request.addOnSuccessListener {
            if (it != null) {

                findPubVicinity(it)
            }
        }
        request.addOnFailureListener { Log.d(TAG, "Failed to find coordinates") }

    }*/

    suspend fun locateUser(provider: FusedLocationProviderClient): Location {
        return suspendCoroutine { continuation ->
            val request = provider.getCurrentLocation(
                LocationRequest.QUALITY_HIGH_ACCURACY,

                // Cancellation token : used when willing to sending location requests
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }
            ); if(ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            )
                provider.getCurrentLocation(
                LocationRequest.QUALITY_HIGH_ACCURACY,
                object : CancellationToken() {


                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                        TODO("Not yet implemented")
                    }

                    override fun isCancellationRequested(): Boolean = false
                }
            ) else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            request.addOnSuccessListener { location ->
                continuation.resume(location)
            }
            request.addOnFailureListener { exception ->
                // Gérer l'échec de la récupération des coordonnées de localisation
                continuation.resumeWithException(exception)
            }
        }
    }


    fun loadOverlay(listPubs:DoubleArray,position:Pair<Double,Double>,names:ArrayList<String>){

        val listSize =listPubs!!.size
        var centerPoint=GeoPoint(position.first,position.second)
        mapController.setCenter(centerPoint)
        val userPos=OverlayItem("","",centerPoint)

        userPos.setMarker(getDrawable(resources,R.drawable.pos_icon,context?.theme))
        listItems.add(userPos)
        Log.d(TAG,"nb noms ${names.size} vs ${listPubs.size}")
        var j=0

        for(i in 0 until listSize-1 step 2){
            val pubPoint=GeoPoint(listPubs[i],listPubs[i+1])

            var pubItems:OverlayItem = OverlayItem(names[j],"",pubPoint)
            j++
            //pubItems.setMarker(ResourcesCompat.getDrawable(resources,R.drawable.beer_icon,context?.theme)) //Does not work because of a bug of osmdroid

            listItems.add(pubItems)

        }

       //Redefining the action to be done when the item is selected
        val itemizedOverlay = CustomItemizedOverlayWithFocus(listItems, object :
            ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {

                if(item.title==null){

                }else{

                    openBottomSheetWithCoords(item,userPos.point)

                }

                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                return true
            }
        }, context)

        //add the overlay with our items on the map
        overlay= itemizedOverlay
        itemizedOverlay.setFocusItemsOnTap(true)
        map.overlays.add(overlay)

    }

    // to be called when we want to get  the route between our position and the selected item
     private fun routeOnClick(lat:Double, lon:Double, myLat:Double, myLon:Double, centerPoint: GeoPoint){

        RouteService().getRoute(myLat,myLon,lat,lon,object :SearchRoute(){
            override fun response(data: SearchRouteResponse) {
                val routeArray = data.paths[0].points.coordinates
                drawRoute(routeArray,map,centerPoint)
            }

        })
    }

    // to be called when an item is selected. Open bottom sheet with info
    fun openBottomSheetWithCoords(coords: OverlayItem,myCoords:IGeoPoint) {
        val bottomSheet = BottomSheetFragment()
        bottomSheet.setFunctionListener(this)//add a listener in order to get the map
        bottomSheet.arguments = Bundle().apply {
            putDouble("latitude", coords.point.latitude)
            putDouble("longitude", coords.point.longitude)
            putDouble("myLat",myCoords.latitude)
            putDouble("myLon",myCoords.longitude)
            putString("name",coords.title)
        }
        bottomSheet.show(parentFragmentManager, "MyBottomSheetFragment")
    }

    //to be called for default SuperSoiree
    fun getNearest(centerPoint: GeoPoint){
        var nearest:Pair<List<List<Double>>,OverlayItem>

        var initList = listOf(listOf(0.0))
        nearest=Pair(initList,OverlayItem("","",centerPoint))
        var distance:Double= 9999999999999.9
        for(item in listItems){
            RouteService().getRoute(centerPoint.latitude,centerPoint.longitude,item.point.latitude,item.point.longitude,object:SearchRoute(){
                override fun response(data: SearchRouteResponse) {
                    if(distance>data.paths[0].distance ){
                        distance=data.paths[0].distance
                        nearest= Pair(data.paths[0].points.coordinates,item)
                    }
                }

            })
        }
        drawRoute(nearest.first,map,centerPoint)

    }

    //to be called when we want to draw the route on the map
    fun drawRoute(route:List<List<Double>>,map:MapView,centerPoint: GeoPoint){
        val length=route.size
        map.overlays.remove(listLines[0])
        map.overlays.remove(overlay)
        map.overlays.add(overlay)
        listLines.clear()



        listLines.add(Polyline())
        // Creation of our route
        listLines[0].width = 4.5F
        listLines[0].color = Color.BLUE

        // Adding our position to the Route then the destination position
        //listLines[0].addPoint(centerPoint)

        var tempPoint: GeoPoint

        for(i in 0 until length) {

            // We go through all the coordinates and add them to our Route
            tempPoint = GeoPoint(route[i][1],route[i][0])
            Log.d(TAG,"fonction drawing $tempPoint")
            listLines[0].addPoint(tempPoint)
        }

        //myPath.addPoint(GeoPoint(array[indexOfCoords], array[indexOfCoords+1]))

        // We :

        map.overlays.add(listLines[0])     // Add the route in the overlay
        map.overlays.remove(overlay) // Remove the items of the overlay
        map.overlays.add(overlay)    // Add them again (so they remain above the p
    }



     suspend fun findPubVicinity(location : Location) : Array<Any> = suspendCoroutine{ continuation ->
        // Declaring the variables as global (when inside this if statement)
        var foundPubs = false // Used as a flag


        var pubsArray = DoubleArray(0) // The array of the first API call
        var pubsName= ArrayList<String>()

        var nbPubs = 0 // The size of the first API call

        val userPosition = Pair(location.latitude, location.longitude) // Our position
        Log.d(TAG,"my position {$userPosition}")

        // If it succeeds we call the overpass API
        //val value = arguments?.getString("value4")
        //val name = arguments?.getString("name")

        OverpassService().searchPub(userPosition,10000,object:SearchPub(){
            override fun response(data: PubResponse) {
                nbPubs=data.elements.size
                Log.d(TAG,"taille de la liste récupérée {$nbPubs}")
                var j = 0

                // In the array we need all our answers (lat / long)
                pubsArray = DoubleArray(nbPubs * 2)

                for(i in 0 until nbPubs) {

                    pubsArray[j] = data.elements[i].lat
                    pubsName.add(data.elements[i].tags!!.name)
                    j++
                    pubsArray[j] = data.elements[i].lon
                    Log.d(TAG,"pubs coord ${pubsArray[j]}")
                    j++

                }

                foundPubs = true // We indicate that this step is finished
                var result : Array<Any> = arrayOf(pubsArray,userPosition,pubsName)
                //loadOverlay(pubsArray ,userPosition,pubsName)
                continuation.resume(result)
            }

        })
    }


    ///function to call from other fragments

    //the entry point for foreign fragments to call routing
    override fun onFunctionCalled(lon: Double,lat: Double,myLon: Double,myLat: Double,centerPoint: GeoPoint) {

        if (::map.isInitialized ) {//check if map as a lateinit is initialized before asking for it
            routeOnClick(lat,lon,myLat,myLon,centerPoint)
        }
    }

    override fun getLoc(callback:(location:Location)-> Unit) {

        GlobalScope.launch {
          val location=locateUser(LocationServices.getFusedLocationProviderClient(requireActivity()))
            callback(location)
        }

    }


}


//////Defining interface for listener to wake the fragment
interface ListenerFunction{
    fun onFunctionCalled(lon: Double,lat: Double,myLon: Double,myLat: Double,centerPoint: GeoPoint)
    fun getLoc(callback:(location:Location)->Unit)

}