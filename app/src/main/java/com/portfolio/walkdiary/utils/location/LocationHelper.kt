package com.portfolio.walkdiary.utils.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices

class LocationHelper(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission") // 呼び出し側で権限チェック済みと仮定
    fun getCurrentLocation(onLocationResult: (Double, Double) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationResult(location.latitude, location.longitude)
            } else {
                // lastLocationがnullの場合は、新規に1回だけリクエストする処理をここに書く
            }
        }
    }
}