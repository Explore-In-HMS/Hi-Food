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
import com.hms.referenceapp.hifood.remote.model.User
import com.huawei.agconnect.cloud.database.*
import java.lang.Exception

class UserRepository(val mCloudDBZone: CloudDBZone) {

    val loadingProgress = MutableLiveData<Boolean>()
    val userInfo = MutableLiveData<User>()
    val isUserRegistered = MutableLiveData<Boolean>(false)
    var isUserExist = MutableLiveData<Boolean>()
    val userListInfo = MutableLiveData<MutableList<User>>()

    val TAG: String = "UserRepository"

    fun getAllUser() {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(User::class.java)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<User>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            Log.w(TAG, "getUsers success")
            allUsersResult(it)
        }.addOnFailureListener {
            Log.w(TAG, "getAllUsers error: ${it.message}")
        }
    }

    private fun allUsersResult(snapshot: CloudDBZoneSnapshot<User>) {

        val userCursor: CloudDBZoneObjectList<User> = snapshot.snapshotObjects
        val userList = mutableListOf<User>()

        try {
            while (userCursor.hasNext()) {
                val user = userCursor.next()
                userList.add(user)
            }
        } catch (exception: Exception) {
            Log.w(TAG, "getAllUsers error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        userListInfo.postValue(userList)
    }

    fun getUserById(userId: String) {
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(User::class.java).equalTo("id", userId)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<User>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            if (it.snapshotObjects.hasNext()) {
                userResult(it)
            } else {
                loadingProgress.value = false
            }
        }.addOnFailureListener {
            Log.w(TAG, "getUserById error: ${it.message}")
        }
    }

    private fun userResult(snapshot: CloudDBZoneSnapshot<User>) {

        val userCursor: CloudDBZoneObjectList<User> = snapshot.snapshotObjects
        var userResult: User =/**/
            User()

        try {
            userResult = userCursor.next()
        } catch (exception: Exception) {
            Log.w(TAG, "getUserById error: ${exception.message}")
        }

        snapshot.release()

        loadingProgress.postValue(false)
        userInfo.postValue(userResult)
    }

    fun registerUser(user: User) {
        loadingProgress.value = true

        val upsertTask = mCloudDBZone.executeUpsert(user)

        upsertTask.addOnSuccessListener {
            loadingProgress.value = false
            isUserRegistered.postValue(true)
        }.addOnFailureListener {
            Log.w(TAG, "registerUser error: ${it.message}")
        }
    }

    fun isExist(userId: String){
        loadingProgress.value = true
        val query = CloudDBZoneQuery.where(User::class.java).equalTo("id", userId)
        val queryTask: CloudDBZoneTask<CloudDBZoneSnapshot<User>> = mCloudDBZone.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask.addOnSuccessListener {
            isUserExist.value = it.snapshotObjects.hasNext()
        }.addOnFailureListener {
            Log.w(TAG, "getUserById error: ${it.message}")
        }.addOnCompleteListener {
            loadingProgress.value = false
        }
    }

}