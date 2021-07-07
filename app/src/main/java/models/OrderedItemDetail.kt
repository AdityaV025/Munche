package models

import java.util.*

data class OrderedItemDetail(var ordered_items: ArrayList<String>? = null,
                        var ordered_restaurant_name: String? = null,
                        var ordered_time: String? = null,
                        var total_amount: String? = null,
                        var ordered_restaurant_spotimage: String? = null)


