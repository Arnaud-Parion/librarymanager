package com.example.librarymanager.di

import android.content.Context
import androidx.room.Room
import com.example.librarymanager.data.local.LibraryDatabase
import com.example.librarymanager.data.remote.LibraryApiService
import com.example.librarymanager.data.repository.BookRepositoryImpl
import com.example.librarymanager.data.repository.ClientRepositoryImpl
import com.example.librarymanager.data.repository.LoanRepositoryImpl
import com.example.librarymanager.domain.repository.BookRepository
import com.example.librarymanager.domain.repository.ClientRepository
import com.example.librarymanager.domain.repository.LoanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLibraryDatabase(
        @ApplicationContext context: Context
    ): LibraryDatabase {
        return Room.databaseBuilder(
            context,
            LibraryDatabase::class.java,
            "library_database"
        )
            .createFromAsset("databases/library_database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideBookDao(database: LibraryDatabase) = database.bookDao()

    @Singleton
    @Provides
    fun provideClientDao(database: LibraryDatabase) = database.clientDao()

    @Singleton
    @Provides
    fun provideLoanDao(database: LibraryDatabase) = database.loanDao()

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.100:8080/") // Change to your API base URL
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideLibraryApiService(retrofit: Retrofit): LibraryApiService {
        return retrofit.create(LibraryApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideBookRepository(
        bookDao: com.example.librarymanager.data.local.dao.BookDao,
        apiService: LibraryApiService
    ): BookRepository {
        return BookRepositoryImpl(bookDao, apiService)
    }

    @Singleton
    @Provides
    fun provideClientRepository(
        clientDao: com.example.librarymanager.data.local.dao.ClientDao,
        apiService: LibraryApiService
    ): ClientRepository {
        return ClientRepositoryImpl(clientDao, apiService)
    }

    @Singleton
    @Provides
    fun provideLoanRepository(
        loanDao: com.example.librarymanager.data.local.dao.LoanDao,
        apiService: LibraryApiService
    ): LoanRepository {
        return LoanRepositoryImpl(loanDao, apiService)
    }
}
