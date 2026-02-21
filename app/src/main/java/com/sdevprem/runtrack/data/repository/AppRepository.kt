package com.sdevprem.runtrack.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sdevprem.runtrack.data.db.dao.RunDao
import com.sdevprem.runtrack.data.model.Run
import com.sdevprem.runtrack.data.utils.RunSortOrder
import kotlinx.coroutines.flow.Flow
import java.util.Date
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.coroutines.ExperimentalCoroutinesApi

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class AppRepository @Inject constructor(
    private val runDao: RunDao,
    private val settingsRepository: SettingsRepository,
) {
    suspend fun insertRun(run: Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getSortedAllRun(sortingOrder: RunSortOrder) = Pager(
        config = PagingConfig(pageSize = 20),
    ) {
        when (sortingOrder) {
            RunSortOrder.DATE           -> runDao.getAllRunSortByDate()
            RunSortOrder.DURATION       -> runDao.getAllRunSortByDuration()
            RunSortOrder.CALORIES_BURNED -> runDao.getAllRunSortByCaloriesBurned()
            RunSortOrder.AVG_SPEED      -> runDao.getAllRunSortByAvgSpeed()
            RunSortOrder.DISTANCE       -> runDao.getAllRunSortByDistance()
        }
    }.flow

    suspend fun getRunStatsInDateRange(fromDate: Date?, toDate: Date?): List<Run> {
        return runDao.getRunStatsInDateRange(fromDate, toDate)
    }

    fun getRunByDescDateWithLimit(limit: Int) = runDao.getRunByDescDateWithLimit(limit)

    fun getTotalRunningDuration(fromDate: Date? = null, toDate: Date? = null): Flow<Long> =
                runDao.getTotalRunningDuration(fromDate, toDate)

    fun getTotalCaloriesBurned(fromDate: Date? = null, toDate: Date? = null): Flow<Long> =
                runDao.getTotalCaloriesBurned(fromDate, toDate)

    fun getTotalDistance(fromDate: Date? = null, toDate: Date? = null): Flow<Long> =
                runDao.getTotalDistance(fromDate, toDate)

    fun getTotalAvgSpeed(fromDate: Date? = null, toDate: Date? = null): Flow<Float> =
                runDao.getTotalAvgSpeed(fromDate, toDate)

}