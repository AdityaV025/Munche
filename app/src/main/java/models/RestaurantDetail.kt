package models

data class RestaurantDetail(var restaurant_name: String? = null,
                       var restaurant_spotimage: String? = null,
                       var restaurant_prep_time: String? = null,
                       var average_price: String? = null,
                       var restaurant_uid: String? = null,
                       var restaurant_phonenumber: String? = null,
                       var latitude: Double? = null,
                       var longitude: Double? = null)