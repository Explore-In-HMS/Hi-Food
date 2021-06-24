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
import com.hms.referenceapp.hifood.remote.model.UserConvenienceLog
import com.hms.referenceapp.hifood.utils.DateUtils
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception
import java.time.LocalDate
import java.util.*

class UserConvenienceLogRepository(val mCloudDBZone: CloudDBZone) {

    val loadingProgress = MutableLiveData<Boolean>()
    val userConvenienceLogListInfo = MutableLiveData<MutableList<UserConvenienceLog>>()
    val TAG: String = "ConvenienceLogRepo"

    val isConvenienceRecorded = MutableLiveData<Boolean>(false)

    var userConvenienceLogList = MutableLiveData<MutableList<UserConvenienceLog>>()


    fun getAllUserConvenienceLog() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(UserConvenienceLog::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<UserConvenienceLog>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getUserConvenienceLogs success")
            allUserConvenienceLogsResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllUserConvenienceLogs error: ${it.message}")
        }
    }

    fun allUserConvenienceLogsResult(snapshot: CloudDBZoneSnapshot<UserConvenienceLog>) {

        val userConvenienceLogCursor: CloudDBZoneObjectList<UserConvenienceLog> =
            snapshot.snapshotObjects
        val userConvenienceLogListLocal = mutableListOf<UserConvenienceLog>()

        try {
            while (userConvenienceLogCursor.hasNext()) {
                val userConvenienceLog = userConvenienceLogCursor.next()
                userConvenienceLogListLocal.add(userConvenienceLog)
            }
        } catch (exception: Exception) {
            Log.w(TAG, "getAllUserConvenienceLogs error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        userConvenienceLogListInfo.postValue(userConvenienceLogListLocal)
    }

    fun add(userId: String, convenienceId: String) {
        loadingProgress.value = true

        val log: UserConvenienceLog = UserConvenienceLog()

        val date = DateUtils.asDate(LocalDate.now())

        log.id = UUID.randomUUID().toString()
        log.userid = userId
        log.convenienceid = convenienceId
        log.date = date
        log.value = 1

        val upsertTask = mCloudDBZone.executeUpsert(log)

        upsertTask.addOnSuccessListener {
            loadingProgress.value = false
            isConvenienceRecorded.postValue(true)
        }.addOnFailureListener {
            Log.w(TAG, "UserConvenienceLog add error: ${it.message}")
        }
    }

    fun getAllUserConvenienceLogsById(userId: String) {
        loadingProgress.value = true

        val query = CloudDBZoneQuery.where(UserConvenienceLog::class.java).equalTo("userid", userId)

        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<UserConvenienceLog>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getAllUserConvenienceLogsById success")
            allUserConvenienceLogResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllUserConvenienceLogsById error: ${it.message}")
        }
    }

    private fun allUserConvenienceLogResult(snapshot: CloudDBZoneSnapshot<UserConvenienceLog>) {

        val userConvenienceLogCursor: CloudDBZoneObjectList<UserConvenienceLog> = snapshot.snapshotObjects
        val userConvenienceLogListLocal = mutableListOf<UserConvenienceLog>()

        try {

            while (userConvenienceLogCursor.hasNext()) {
                val userConvenienceLog = userConvenienceLogCursor.next()
                userConvenienceLogListLocal.add(userConvenienceLog)
            }

            userConvenienceLogList.postValue(userConvenienceLogListLocal)

        } catch (exception: Exception) {
            Log.w(TAG, "allUserConvenienceLogResult error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)

    }
}