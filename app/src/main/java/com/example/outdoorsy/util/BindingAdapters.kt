package com.example.outdoorsy.util

import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.example.outdoorsy.R

@BindingAdapter("app:likedByUser")
fun ImageButton.setLikeImage(isLiked: Boolean) {
    this.setImageResource(if (isLiked) R.drawable.ic_like else R.drawable.ic_heart_outline)
}

//package com.example.outdoorsy.util
//
//import android.widget.ImageButton
//import androidx.databinding.BindingAdapter
//import com.example.outdoorsy.R
//
//@BindingAdapter("app:likedByUser")
//fun ImageButton.setLikeImage(isLiked: Boolean) {
//    this.setImageResource(if (isLiked) R.drawable.ic_like else R.drawable.ic_heart_outline)
//}
