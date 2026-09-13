package com.medKnow.medknow.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// DataStore 实例
private val Context.datastore by preferencesDataStore(name = "auth_prefs")

class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context
){
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
    }

    // 观察 token 变化
    val tokenFlow: Flow<String?> = context.datastore.data.map { prefs ->
        prefs[KEY_TOKEN]
    }

    // 保存 token
    suspend fun saveToken(token: String) {
        context.datastore.edit { prefs ->
            prefs[KEY_TOKEN] = token
        }
    }

    // 清空 token
    suspend fun clearToken() {
        context.datastore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
        }
    }

}