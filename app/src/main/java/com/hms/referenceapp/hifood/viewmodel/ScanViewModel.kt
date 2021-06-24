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
import com.hms.referenceapp.hifood.remote.model.Convenience
import com.hms.referenceapp.hifood.repository.CloudDbZoneWrapper
import com.hms.referenceapp.hifood.repository.ConvenienceRepository

class ScanViewModel : ViewModel() {
    var mCloudDbZoneWrapper: CloudDbZoneWrapper = CloudDbZoneWrapper()

    private var convenienceRepository: ConvenienceRepository

    var convenienceExistancy: MutableLiveData<Boolean>
    var scannedConvenience: MutableLiveData<Convenience>

    init {
        mCloudDbZoneWrapper.createObjectType()
        mCloudDbZoneWrapper.openCloudDBZone()

        convenienceRepository = ConvenienceRepository(mCloudDbZoneWrapper.mCloudDBZone)

        scannedConvenience = convenienceRepository.convenienceInfo
        convenienceExistancy = convenienceRepository.convenienceExistancy
    }

    fun getConvenience(id: String) {
        convenienceRepository.getConvenienceById(id)
    }
}