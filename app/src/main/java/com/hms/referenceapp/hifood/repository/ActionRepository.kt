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
import com.hms.referenceapp.hifood.remote.model.Action
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception

class ActionRepository(val mCloudDBZone: CloudDBZone) {

    val loadingProgress = MutableLiveData<Boolean>()
    val actionListInfo = MutableLiveData<MutableList<Action>>()
    val TAG: String = "ActionRepository"

    fun getAllAction() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(Action::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Action>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getActions success")
            allActionsResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllActions error: ${it.message}")
        }
    }

    fun allActionsResult(snapshot: CloudDBZoneSnapshot<Action>) {

        val actionCursor: CloudDBZoneObjectList<Action> = snapshot.snapshotObjects
        val actionList = mutableListOf<Action>()

        try {
            while (actionCursor.hasNext()) {
                val action = actionCursor.next()
                actionList.add(action)
            }
        } catch (exception: Exception) {
            Log.w(TAG, "getAllActions error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        actionListInfo.postValue(actionList)
    }
}