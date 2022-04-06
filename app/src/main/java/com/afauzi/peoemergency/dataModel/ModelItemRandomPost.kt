package com.afauzi.peoemergency.dataModel

data class ModelItemRandomPost(
    var username: String,
    var date: String,
    var desc: String,
    var media: Int = 1,
    var profilePhoto: Int = 1
)
