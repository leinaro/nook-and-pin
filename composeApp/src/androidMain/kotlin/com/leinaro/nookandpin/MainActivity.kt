package com.leinaro.nookandpin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.leinaro.nookandpin.data.FirebaseAuthRepository
import com.leinaro.nookandpin.data.requestGoogleIdToken
import com.leinaro.nookandpin.ui.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authRepository = remember {
                FirebaseAuthRepository(requestGoogleIdToken = { requestGoogleIdToken(this@MainActivity) })
            }
            App(authRepository)
        }
    }
}
