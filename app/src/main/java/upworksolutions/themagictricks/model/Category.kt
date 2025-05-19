package upworksolutions.themagictricks.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String,
    val name: String,
    val iconResId: Int
) : Parcelable 