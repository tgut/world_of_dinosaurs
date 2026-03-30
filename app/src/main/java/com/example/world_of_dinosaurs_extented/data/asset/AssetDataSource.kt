package com.example.world_of_dinosaurs_extented.data.asset

import android.content.Context
import com.example.world_of_dinosaurs_extented.data.remote.dto.DinosaurDto
import com.example.world_of_dinosaurs_extented.data.remote.dto.QuizQuestionDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) {
    private var cachedDinosaurs: List<DinosaurDto>? = null
    private var cachedQuestions: List<QuizQuestionDto>? = null

    fun loadDinosaurs(): List<DinosaurDto> {
        cachedDinosaurs?.let { return it }
        val json = context.assets.open("dinosaurs_extended.json").bufferedReader().use { it.readText() }
        val type = Types.newParameterizedType(List::class.java, DinosaurDto::class.java)
        val adapter = moshi.adapter<List<DinosaurDto>>(type)
        return (adapter.fromJson(json) ?: emptyList()).also { cachedDinosaurs = it }
    }

    fun loadQuizQuestions(): List<QuizQuestionDto> {
        cachedQuestions?.let { return it }
        val json = context.assets.open("quiz_questions.json").bufferedReader().use { it.readText() }
        val type = Types.newParameterizedType(List::class.java, QuizQuestionDto::class.java)
        val adapter = moshi.adapter<List<QuizQuestionDto>>(type)
        return (adapter.fromJson(json) ?: emptyList()).also { cachedQuestions = it }
    }
}
