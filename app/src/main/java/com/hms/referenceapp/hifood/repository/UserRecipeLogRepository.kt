/* Copyright 2020. Explore in HMS. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0

 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hms.referenceapp.hifood.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hms.referenceapp.hifood.remote.model.UserRecipeLog
import com.hms.referenceapp.hifood.utils.DateUtils
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception
import java.time.LocalDate
import java.util.*

class UserRecipeLogRepository(val mCloudDBZone: CloudDBZone) {

    val loadingProgress = MutableLiveData<Boolean>()
    val userRecipeLogListInfo = MutableLiveData<MutableList<UserRecipeLog>>()
    val TAG: String = "UserRecipeLogRepository"
    val isMealRecorded = MutableLiveData<Boolean>(false)

    var userRecipeLogList = MutableLiveData<MutableList<UserRecipeLog>>()

    fun getAllUserRecipeLog() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(UserRecipeLog::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<UserRecipeLog>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getUserRecipeLogs success")
            allUserRecipeLogsResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllUserRecipeLogs error: ${it.message}")
        }
    }

    fun allUserRecipeLogsResult(snapshot: CloudDBZoneSnapshot<UserRecipeLog>) {

        val userRecipeLogCursor: CloudDBZoneObjectList<UserRecipeLog> = snapshot.snapshotObjects
        val userRecipeLogList = mutableListOf<UserRecipeLog>()

        try {
            while (userRecipeLogCursor.hasNext()) {
                val userRecipeLog = userRecipeLogCursor.next()
                userRecipeLogList.add(userRecipeLog)
            }
        } catch (exception: Exception) {
            Log.w(TAG, "getAllUserRecipeLogs error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        userRecipeLogListInfo.postValue(userRecipeLogList)
    }

    fun add(userId: String, value: Double, recipeId: String) {
        loadingProgress.value = true

        val log: UserRecipeLog = UserRecipeLog()

        val date = DateUtils.asDate(LocalDate.now())

        log.id = UUID.randomUUID().toString()
        log.userid = userId
        log.recipeid = recipeId
        log.date = date
        log.value = value.toFloat()

        val upsertTask = mCloudDBZone.executeUpsert(log)

        upsertTask.addOnSuccessListener {
            loadingProgress.value = false
            isMealRecorded.postValue(true)
        }.addOnFailureListener {
            Log.w(TAG, "UserRecipeLog add error: ${it.message}")
        }
    }

    fun getAllUserRecipeLogsById(userId: String) {
        loadingProgress.value = true

        val query = CloudDBZoneQuery.where(UserRecipeLog::class.java).equalTo("userid", userId)

        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<UserRecipeLog>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getAllUserRecipeLogsById success")
            allUserRecipeLogResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllUserRecipeLogsById error: ${it.message}")
        }
    }

    private fun allUserRecipeLogResult(snapshot: CloudDBZoneSnapshot<UserRecipeLog>) {

        val userRecipeLogCursor: CloudDBZoneObjectList<UserRecipeLog> = snapshot.snapshotObjects
        val userRecipeLogListLocal = mutableListOf<UserRecipeLog>()

        try {

            while (userRecipeLogCursor.hasNext()) {
                val userRecipeLog = userRecipeLogCursor.next()
                userRecipeLogListLocal.add(userRecipeLog)
            }

            userRecipeLogList.postValue(userRecipeLogListLocal)

        } catch (exception: Exception) {
            Log.w(TAG, "getAllRecipes error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)

    }
}