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

package com.hms.referenceapp.hifood.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.repository.CloudDbZoneWrapper
import com.huawei.agconnect.auth.AGConnectAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val navController = findNavController(R.id.nav_host_fragment)

        CloudDbZoneWrapper.initAGConnectCloudDB(applicationContext)
    }

    override fun onStart() {
        super.onStart()

        val user = AGConnectAuth.getInstance().currentUser

        if (user != null) {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(USER_ID, user.uid)
            }
            startActivity(intent)
        }
    }
}