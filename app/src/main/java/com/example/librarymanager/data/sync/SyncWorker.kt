package com.example.librarymanager.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.librarymanager.domain.repository.BookRepository
import com.example.librarymanager.domain.repository.ClientRepository
import com.example.librarymanager.domain.repository.LoanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val bookRepository: BookRepository,
    private val clientRepository: ClientRepository,
    private val loanRepository: LoanRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Sync all data from the remote server
            val bookResult = bookRepository.syncBooks()
            val clientResult = clientRepository.syncClients()
            val loanResult = loanRepository.syncLoans()

            if (bookResult.isSuccess && clientResult.isSuccess && loanResult.isSuccess) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
