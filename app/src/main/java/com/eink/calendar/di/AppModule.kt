package com.eink.calendar.di

import android.content.Context
import android.content.ContentResolver
import com.eink.calendar.data.local.CalendarContentProvider
import com.eink.calendar.data.repository.CalendarRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 依赖注入配置
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver {
        return context.contentResolver
    }

    @Singleton
    @Provides
    fun provideCalendarContentProvider(
        contentResolver: ContentResolver
    ): CalendarContentProvider {
        return CalendarContentProvider(contentResolver)
    }

    @Singleton
    @Provides
    fun provideCalendarRepository(
        contentProvider: CalendarContentProvider
    ): CalendarRepository {
        return CalendarRepository(contentProvider)
    }
}
