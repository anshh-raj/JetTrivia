package com.example.jettrivia.di

import android.content.Context
import androidx.room.Room
import com.example.jettrivia.data.IndexDatabase
import com.example.jettrivia.data.IndexDatabaseDao
import com.example.jettrivia.network.QuestionApi
import com.example.jettrivia.repository.QuestionRepository
import com.example.jettrivia.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideQuestionRepository(api: QuestionApi, indexDatabaseDao: IndexDatabaseDao) = QuestionRepository(api, indexDatabaseDao)

    @Singleton
    @Provides
    fun provideQuestionApi(): QuestionApi{
        return Retrofit.Builder()
            .baseUrl(Constants.Base_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideIndexDao(indexDatabase: IndexDatabase): IndexDatabaseDao
    = indexDatabase.indexDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): IndexDatabase
    = Room.databaseBuilder(
        context,
        IndexDatabase::class.java,
        name = "index_db")
        .fallbackToDestructiveMigrationFrom()
        .build()
}