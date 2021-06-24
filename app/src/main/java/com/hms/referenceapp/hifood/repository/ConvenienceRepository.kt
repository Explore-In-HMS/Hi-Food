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
import com.hms.referenceapp.hifood.remote.model.Convenience
import com.hms.referenceapp.hifood.remote.model.UserConvenienceLog
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception
import java.util.HashMap

class ConvenienceRepository(val mCloudDBZone: CloudDBZone) {

    val loadingProgress = MutableLiveData<Boolean>()
    private val convenienceListInfo = MutableLiveData<MutableList<Convenience>>()
    val TAG: String = "ConvenienceRepository"
    val convenienceInfo = MutableLiveData<Convenience>()
    val convenienceExistancy = MutableLiveData<Boolean>()
    val convenienceCaloriesList = MutableLiveData<HashMap<String, Float>>()
    val isConvenienceCaloriesHandled = MutableLiveData<Boolean>()

    fun getConvenienceById(convenienceId: String) {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(Convenience::class.java).equalTo("id", convenienceId)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Convenience>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                convenienceResult(it)
            } else {
                convenienceExistancy.postValue(false)
                loadingProgress.value = false
            }
        }.addOnFailureListener {
            Log.w(TAG, "getConvenienceById error: ${it.message}")
        }
    }

    private fun convenienceResult(snapshot: CloudDBZoneSnapshot<Convenience>) {

        val convenienceCursor: CloudDBZoneObjectList<Convenience> = snapshot.snapshotObjects
        var convenienceResult: Convenience =
            Convenience()

        try {
            convenienceResult = convenienceCursor.next()
        } catch (exception: Exception) {
            convenienceExistancy.postValue(false)
            Log.w(TAG, "getConvenienceById error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        convenienceInfo.postValue(convenienceResult)
        convenienceExistancy.postValue(true)

    }

    fun getAllConvenience() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(Convenience::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Convenience>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getConveniences success")
            allConveniencesResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllConveniences error: ${it.message}")
        }
    }

    fun allConveniencesResult(snapshot: CloudDBZoneSnapshot<Convenience>) {

        val convenienceCursor: CloudDBZoneObjectList<Convenience> = snapshot.snapshotObjects
        val convenienceList = mutableListOf<Convenience>()

        try {
            while (convenienceCursor.hasNext()) {
                val convenience = convenienceCursor.next()
                convenienceList.add(convenience)
            }
        } catch (exception: Exception) {
            Log.w(TAG, "getAllConveniences error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        convenienceListInfo.postValue(convenienceList)
    }

    fun getCaloriesForSummary(userConvenienceLogList: MutableList<UserConvenienceLog>) {

        if(userConvenienceLogList.isNullOrEmpty()){
            isConvenienceCaloriesHandled.postValue(true)
        }

        val convenienceList = userConvenienceLogList.map { it.convenienceid }.distinct().toTypedArray()

        val query = CloudDBZoneQuery.where(Convenience::class.java).`in`("id", convenienceList)

        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<Convenience>> =
            mCloudDBZone.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
            )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                caloriesForSummaryResult(it)
            }
        }.addOnFailureListener {
            Log.w(TAG, "getCaloriesForSummary error: ${it.message}")
            loadingProgress.value = false
        }
    }

    private fun caloriesForSummaryResult(snapshot: CloudDBZoneSnapshot<Convenience>) {
        val convenienceCursor: CloudDBZoneObjectList<Convenience> = snapshot.snapshotObjects
        val convenienceCaloriesListLocal = hashMapOf<String, Float>()

        try {

            while (convenienceCursor.hasNext()) {
                val convenience = convenienceCursor.next()
                convenienceCaloriesListLocal[convenience.id] = convenience.calories
            }

            convenienceCaloriesList.postValue(convenienceCaloriesListLocal)
            isConvenienceCaloriesHandled.postValue(true)


        } catch (exception: Exception) {
            Log.w(TAG, "caloriesForSummaryResult error: ${exception.message}")
            loadingProgress.value = false
        }
        snapshot.release()

        loadingProgress.value = false
    }
}