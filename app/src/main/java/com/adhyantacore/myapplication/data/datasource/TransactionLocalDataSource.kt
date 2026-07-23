package com.adhyantacore.myapplication.data.datasource

import android.content.Context
import com.adhyantacore.myapplication.data.model.TransactionResponseDto
import com.adhyantacore.myapplication.data.model.TransactionRowDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader


class TransactionLocalDataSource(
    private val context: Context
) {

    suspend fun fetchTransactions(): TransactionResponseDto = withContext(Dispatchers.IO) {
        val raw = context.assets.open(ASSET_FILE_NAME).use { stream ->
            BufferedReader(InputStreamReader(stream)).readText()
        }
        val jsonObject = JSONObject(raw)
        val sortArray = jsonObject.optJSONArray("sort") ?: org.json.JSONArray()
        val rows = mutableListOf<TransactionRowDto>()
        for (i in 0 until sortArray.length()) {
            val item = sortArray.getJSONObject(i)
            rows.add(
                TransactionRowDto(
                    mid = item.optInt("Mid"),
                    tid = item.optLong("Tid"),
                    amount = item.optDouble("amount"),
                    narration = item.optLong("narration")
                )
            )
        }
        TransactionResponseDto(sort = rows)
    }

    companion object {
        private const val ASSET_FILE_NAME = "transactions.json"
    }
}
