package com.afauzi.peoemergency.dataModel

data class ModelItemRandomPost(
    var username: String? = null,
    var photoProfile: String? = null,
    var postLocationCityName: String? = null,
    var postText: String? = null,
    var postDate: String? = null,
    var photoPost: String? = null,
    var postId: String? = null,
    var postLocationCoordinate: String? = null,
    var userId: String? = null,
    var userReplyPost: ModelReplyPost? = null
)
