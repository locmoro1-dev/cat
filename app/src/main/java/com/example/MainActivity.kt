package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.auth.FirebaseAuthManager
import com.example.data.local.AppDatabase
import com.example.data.repository.MarketplaceRepository
import com.example.data.sync.FirestoreMachineSyncManager
import com.example.ui.screens.MainMarketplaceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MarketplaceViewModel
import com.example.ui.viewmodel.MarketplaceViewModelFactory

class MainActivity : ComponentActivity() {

    private var syncManager: FirestoreMachineSyncManager? = null
    private var authManager: FirebaseAuthManager? = null

    private val viewModel: MarketplaceViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val manager = FirestoreMachineSyncManager(applicationContext, database.marketplaceDao())
        val auth = FirebaseAuthManager(applicationContext, database.marketplaceDao())
        syncManager = manager
        authManager = auth
        manager.startRealtimeSync()
        val repository = MarketplaceRepository(
            dao = database.marketplaceDao(),
            syncManager = manager,
            authManager = auth
        )
        MarketplaceViewModelFactory(repository)
    }

    override fun onDestroy() {
        super.onDestroy()
        syncManager?.stopRealtimeSync()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainMarketplaceScreen(viewModel = viewModel)
            }
        }
    }
}
