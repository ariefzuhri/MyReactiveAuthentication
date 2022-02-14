package com.ariefzuhri.mra.data.source.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisteredUsersResponse(

    @field:Json(name = "emails")
    val emails: List<String?>? = null,
)