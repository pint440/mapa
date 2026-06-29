package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsgsResponse(
    @Json(name = "features") val features: List<UsgsFeature>
)

@JsonClass(generateAdapter = true)
data class UsgsFeature(
    @Json(name = "id") val id: String,
    @Json(name = "properties") val properties: UsgsProperties,
    @Json(name = "geometry") val geometry: UsgsGeometry
)

@JsonClass(generateAdapter = true)
data class UsgsProperties(
    @Json(name = "mag") val mag: Double?,
    @Json(name = "place") val place: String?,
    @Json(name = "time") val time: Long?,
    @Json(name = "url") val url: String?
)

@JsonClass(generateAdapter = true)
data class UsgsGeometry(
    @Json(name = "coordinates") val coordinates: List<Double> // [lon, lat, depth]
)
