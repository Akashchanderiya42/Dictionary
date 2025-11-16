package com.example.dictionary

import androidx.compose.ui.graphics.vector.Path
import okhttp3.Response
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface DictionaryApi {

    @GET("en/{word}")
    suspend fun getMeaning(@Path("word") word : String) : Response<List<WordResult>>

}