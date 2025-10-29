package com.example.librarymanager.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleSyncWork() {
        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }

    fun cancelSyncWork() {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
    }

    companion object {
        private const val SYNC_WORK_NAME = "library_sync_work"
    }
}
