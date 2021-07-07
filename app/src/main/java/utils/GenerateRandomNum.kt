package utils

import kotlin.math.floor

class GenerateRandomNum {

    companion object {
        fun generateRandNum() : String{
            return (floor(Math.random() * 9000000000000L) + 1000000000000L).toString()
        }
    }

}