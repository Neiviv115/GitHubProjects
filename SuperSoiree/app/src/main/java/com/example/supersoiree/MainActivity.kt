package com.example.supersoiree

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.example.supersoiree.databinding.ActivityMainBinding
import com.example.supersoiree.model.Favorites
import com.example.supersoiree.model.PubForfav
import com.example.supersoiree.model.PubResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.osmdroid.config.Configuration
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth


    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.nav_host_fragment)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        val paramValue = intent.getStringExtra("user")
        if (paramValue != null) {

            Log.d(TAG, "Paramètre : $paramValue")
            val args = bundleOf("user" to paramValue)
            navController.navigate(R.id.SecondFragment,args)
        }
        auth = Firebase.auth
        val topAppBar=findViewById<MaterialToolbar>(R.id.topAppBar)
        val drawerLayout:DrawerLayout=findViewById(R.id.drawer)
        val navigationView:NavigationView=findViewById(R.id.navigationView)
        topAppBar.setNavigationOnClickListener{
            drawerLayout.open()
        }
        navigationView.setNavigationItemSelectedListener {menuItem->
            when(menuItem.title){
                "Mon compte"->{
                    //go to profile settings

                }
                "Favoris"->{
                    navController.navigate(R.id.FavouritesFragment)//go to favs
                }
                "Mes soirées"->{
                    //navController.navigate()//go to historic
                    Toast.makeText(this,"Mes soirées: In progress",Toast.LENGTH_SHORT).show()
                }
            }

            menuItem.isChecked=true
            drawerLayout.close()
            true
        }





    }

    override fun onPause(){

        super.onPause()
    }

    override fun onResume(){

        super.onResume()
    }

    fun setToFav(name:String){
        //register the bar in database if not already done
        //create a fav for this user with uid as a data
        // add a reference to the bar in the fav
        //each time we want the fav just SELECT* FROM favs WHERE uid=uid;
        val database = Firebase.firestore
        val bar=PubForfav(name)
        auth=Firebase.auth


                //Log.d(AuthActivity.TAG, "Document successfully created")

        auth.currentUser?.uid?.let { it1 ->
            var favorites= Favorites(it1, listOf(bar))

            database.collection("favs").whereEqualTo("user_id",it1).get()
                .addOnSuccessListener {documents->
                    if(documents.isEmpty){
                        database.collection("favs").document().set(favorites, SetOptions.merge())
                    }else{
                        for (document in documents){
                            var update_favorites =Favorites(it1,document.toObject<Favorites>().favorite_pubs+bar)
                            database.collection("favs").document(document.id).set(update_favorites,
                                SetOptions.merge())
                        }
                    }

                }.addOnFailureListener{

                }
        }
    }






    fun removeFromFav(name:String){
         auth=Firebase.auth
        val database =Firebase.firestore

        auth.currentUser?.uid?.let { it3->
            database.collection("favs").whereEqualTo("user_id",it3).get().addOnSuccessListener {documents ->
                if(documents.isEmpty){

                }else{
                    for(document in documents){

                        var pubList : List<PubForfav> = listOf()
                        for(pub in document.toObject<Favorites>().favorite_pubs){
                            if(pub.name!=name){
                                pubList+=pub

                            }
                        }
                        var newFavorites =Favorites(it3,pubList )
                        database.collection("favs").document(document.id).set(newFavorites)


                    }
                }
            }
        }

    }

    suspend fun getFav(): List<PubForfav> {
        return suspendCoroutine { continuation ->
             auth = Firebase.auth
            val database = Firebase.firestore

            val favoritesList = mutableListOf<PubForfav>()

            auth.currentUser?.uid?.let { userId ->
                database.collection("favs")
                    .whereEqualTo("user_id", userId)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val favorites = document.toObject<Favorites>()
                            // Ajouter les favoris à la liste
                            favoritesList.addAll(favorites.favorite_pubs)
                        }
                        continuation.resume(favoritesList)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }




}



interface PubListener {
    fun onCustomObjectReceived(pubObject: PubResponse)
}