package com.example.supersoiree.model

data class Favorites (
    val user_id :String,
    var favorite_pubs:List<PubForfav>

        ){
    constructor():this("", emptyList())
}
