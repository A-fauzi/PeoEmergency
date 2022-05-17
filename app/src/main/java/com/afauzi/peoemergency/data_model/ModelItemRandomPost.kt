package com.afauzi.peoemergency.data_model

data class ModelItemRandomPost(
    var username: String? = null,
    var photoProfile: String? = null,
    var postLocationName: String? = null,
    var postText: String? = null,
    var postDate: String? = null,
    var photoPost: String? = null,
    var postId: String? = null,
    var postLocationCoordinate: String? = null,
    var userId: String? = null,
    var userReplyPost: ModelReplyPost? = null,
    var countCommentPostUser: String? = null,
    var countReactPostUser: String? = null,
    var userReact: ModelReactPost? = null

)
