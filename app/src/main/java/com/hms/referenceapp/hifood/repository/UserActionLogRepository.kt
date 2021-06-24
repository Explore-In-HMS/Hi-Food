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
import com.hms.referenceapp.hifood.remote.model.UserActionLog
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception

class UserActionLogRepository(val mCloudDBZone: CloudDBZone) {

    val loadingProgress = MutableLiveData<Boolean>()
    val userActionLogListInfo = MutableLiveData<MutableList<UserActionLog>>()
    val TAG: String = "UserActionLogRepository"

    fun getAllUserActionLogs() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(UserActionLog::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<UserActionLog>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getAllUserActionLogs success")
            allUserActionLogsResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllUserActionLogs error: ${it.message}")
        }
    }

    fun allUserActionLogsResult(snapshot: CloudDBZoneSnapshot<UserActionLog>) {

        val userActionLogCursor: CloudDBZoneObjectList<UserActionLog> = snapshot.snapshotObjects
        val userActionLogList = mutableListOf<UserActionLog>()

        try {
            while (userActionLogCursor.hasNext()) {
                val userActionLog = userActionLogCursor.next()
                userActionLogList.add(userActionLog)
            }
        } catch (exception: Exception) {
            Log.w(TAG, "getAllUserActionLogds error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        userActionLogListInfo.postValue(userActionLogList)
    }
}