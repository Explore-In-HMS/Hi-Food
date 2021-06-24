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
import com.hms.referenceapp.hifood.remote.model.User
import com.hms.referenceapp.hifood.repository.CloudDbZoneWrapper
import com.hms.referenceapp.hifood.repository.UserRepository

class CreateAccountViewModel : ViewModel() {
    var mCloudDbZoneWrapper: CloudDbZoneWrapper = CloudDbZoneWrapper()
    private var userRepository: UserRepository
    var isUserRegistered = MutableLiveData<Boolean>()
    val userId = MutableLiveData<String>()

    init {
        mCloudDbZoneWrapper.createObjectType()
        mCloudDbZoneWrapper.openCloudDBZone()
        userRepository = UserRepository(mCloudDbZoneWrapper.mCloudDBZone)
        isUserRegistered = userRepository.isUserRegistered
    }

    fun registerUser(id: String, gender: String, weight: Int, height: Int) {
        val user = User()

        if (gender == "Female") {
            user.gender = 1
        } else {
            user.gender = 2
        }

        user.weight = weight
        user.height = height.toFloat()


        user.id = id

        userRepository.registerUser(user)
    }
}