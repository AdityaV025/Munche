package models

data class ReviewDetails(var rating: String? = null,
                    var review: String? = null,
                    var recommended: String? = null,
                    var user_name: String? = null,
                    var user_image: String? = null,
                    var uid: String? = null)