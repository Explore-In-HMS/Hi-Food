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

package com.hms.referenceapp.hifood.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hms.referenceapp.hifood.model.LogSummary
import com.hms.referenceapp.hifood.remote.model.*
import com.hms.referenceapp.hifood.repository.*
import java.util.*
import kotlin.collections.HashMap

class ProfileViewModel : ViewModel() {

    var mCloudDbZoneWrapper: CloudDbZoneWrapper = CloudDbZoneWrapper()

    private var userRecipeLogRepository: UserRecipeLogRepository
    private var userConvenienceLogRepository: UserConvenienceLogRepository
    private var recipeRepository: RecipeRepository
    private var convenienceRepository: ConvenienceRepository
    private var userRepository: UserRepository

    var userRecipeLogList: MutableLiveData<MutableList<UserRecipeLog>>
    var userConvenienceLogList: MutableLiveData<MutableList<UserConvenienceLog>>
    val recipeCaloriesList: MutableLiveData<HashMap<String, Float>>
    val convenienceCaloriesList: MutableLiveData<HashMap<String, Float>>
    val isRecipeCaloriesHandled: MutableLiveData<Boolean>
    val isConvenienceCaloriesHandled: MutableLiveData<Boolean>
    val userInfo: MutableLiveData<User>

    var logSummaryList = MutableLiveData<MutableList<LogSummary>>()

    init {
        mCloudDbZoneWrapper.createObjectType()
        mCloudDbZoneWrapper.openCloudDBZone()

        userRecipeLogRepository = UserRecipeLogRepository(mCloudDbZoneWrapper.mCloudDBZone)
        userConvenienceLogRepository =
            UserConvenienceLogRepository(mCloudDbZoneWrapper.mCloudDBZone)
        recipeRepository = RecipeRepository(mCloudDbZoneWrapper.mCloudDBZone)
        convenienceRepository = ConvenienceRepository(mCloudDbZoneWrapper.mCloudDBZone)
        userRepository = UserRepository(mCloudDbZoneWrapper.mCloudDBZone)

        userRecipeLogList = userRecipeLogRepository.userRecipeLogList
        userConvenienceLogList = userConvenienceLogRepository.userConvenienceLogList
        recipeCaloriesList = recipeRepository.recipeCaloriesList
        convenienceCaloriesList = convenienceRepository.convenienceCaloriesList
        userInfo = userRepository.userInfo

        isRecipeCaloriesHandled = recipeRepository.isRecipeCaloriesHandled
        isConvenienceCaloriesHandled = convenienceRepository.isConvenienceCaloriesHandled

    }

    fun getUserInfo(userId: String) {
        userRepository.getUserById(userId)
    }

    fun getRecipeSummariesById(userId: String) {
        userRecipeLogRepository.getAllUserRecipeLogsById(userId)
    }

    fun getConvenienceSummariesById(userId: String) {
        userConvenienceLogRepository.getAllUserConvenienceLogsById(userId)
    }

    fun getRecipeCalories() {
        recipeRepository.getCaloriesForSummary(userRecipeLogList.value!!)
    }

    fun getConvenienceCalories() {
        convenienceRepository.getCaloriesForSummary(userConvenienceLogList.value!!)
    }

    fun calculateSummary() {
        val logSummaryListLocal = mutableListOf<LogSummary>()
        val logSummaryHashMapLocal = hashMapOf<Date, Float>()

        if (!recipeCaloriesList.value.isNullOrEmpty()) {
            for (userRecipeLog in userRecipeLogList.value!!) {

                val logDate = userRecipeLog.date
                val recipeCalory = recipeCaloriesList.value?.get(userRecipeLog.recipeid) as Float

                if (logSummaryHashMapLocal[logDate] != null) {
                    logSummaryHashMapLocal[logDate] =
                        logSummaryHashMapLocal[logDate]?.plus(recipeCalory * userRecipeLog.value) as Float
                } else {
                    logSummaryHashMapLocal[logDate] = recipeCalory * userRecipeLog.value
                }
            }
        }

        if (!convenienceCaloriesList.value.isNullOrEmpty()) {
            for (userConvenienceLog in userConvenienceLogList.value!!) {
                val logDate = userConvenienceLog.date
                val convenienceCalories =
                    convenienceCaloriesList.value?.get(userConvenienceLog.convenienceid) as Float

                if (logSummaryHashMapLocal[logDate] != null) {
                    logSummaryHashMapLocal[logDate] =
                        logSummaryHashMapLocal[logDate]?.plus(convenienceCalories * userConvenienceLog.value) as Float
                } else {
                    logSummaryHashMapLocal[logDate] =
                        convenienceCalories * userConvenienceLog.value
                }
            }
        }


        for (logSummary in logSummaryHashMapLocal) {
            logSummaryListLocal.add(LogSummary(logSummary.key, logSummary.value))
        }

        logSummaryList.postValue(logSummaryListLocal)
    }
}