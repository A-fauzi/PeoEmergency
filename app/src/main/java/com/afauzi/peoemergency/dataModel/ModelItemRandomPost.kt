package com.afauzi.peoemergency.dataModel

data class ModelItemRandomPost(
    var user: ModelUser,
    var post_location: String,
    var post_date: String,
    var post_desc: String,
    var post_image: Int = 1,
    var post_comment: List<ModelUser>,
    var post_like: List<ModelUser>,
)
