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

package com.hms.referenceapp.hifood.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.hms.referenceapp.hifood.R

    fun Fragment.isGranted(permission: AppPermission) = run {
        context?.let {
            (PermissionChecker.checkSelfPermission(
                it,
                permission.permissionName
            ) == PermissionChecker.PERMISSION_GRANTED)
        } ?: false
    }

    fun Fragment.shouldShowRationale(permission: AppPermission) = run {
        shouldShowRequestPermissionRationale(permission.permissionName)
    }

    fun Fragment.requestPermission(permission: AppPermission) {
        requestPermissions(arrayOf(permission.permissionName), permission.requestCode)
    }

    fun Fragment.handlePermission(
        permission: AppPermission,
        onGranted: (AppPermission) -> Unit,
        onDenied: (AppPermission) -> Unit,
        onRationaleNeeded: ((AppPermission) -> Unit)? = null
    ) {
        when {
            isGranted(permission) -> onGranted.invoke(permission)
            shouldShowRationale(permission) -> onRationaleNeeded?.invoke(permission)
            else -> onDenied.invoke(permission)
        }
    }

    fun Fragment.handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        onPermissionGranted: (AppPermission) -> Unit,
        onPermissionDenied: ((AppPermission) -> Unit)? = null,
        onPermissionDeniedPermanently: ((AppPermission) -> Unit)? = null
    ) {

        AppPermission.permissions.find {
            it.requestCode == requestCode
        }?.let { appPermission ->
            val permissionGrantResult = mapPermissionsAndResults(
                permissions, grantResults
            )[appPermission.permissionName]
            when {
                PermissionChecker.PERMISSION_GRANTED == permissionGrantResult -> {
                    onPermissionGranted(appPermission)
                }
                shouldShowRationale(appPermission) -> onPermissionDenied?.invoke(appPermission)
                else -> {
                    goToAppDetailsSettings()
                    onPermissionDeniedPermanently?.invoke(appPermission)
                }
            }
        }
    }

    private fun Fragment.goToAppDetailsSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context?.packageName, null)
        }
        activity?.let {
            it.startActivityForResult(intent, 0)
        }
    }

    private fun mapPermissionsAndResults(
        permissions: Array<out String>, grantResults: IntArray
    ): Map<String, Int> = permissions.mapIndexedTo(mutableListOf<Pair<String, Int>>()
    ) { index, permission -> permission to grantResults[index] }.toMap()


    sealed class AppPermission(
        val permissionName: String, val requestCode: Int, val deniedMessageId: Int, val explanationMessageId: Int
    ) {
        companion object {
            val permissions: List<AppPermission> by lazy {
                listOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION,
                    ACCESS_BACKGROUND_LOCATION
                )
            }
        }

        object ACCESS_FINE_LOCATION : AppPermission(
            Manifest.permission.ACCESS_FINE_LOCATION, 42,
            R.string.permission_required_text, R.string.permission_required_text
        )

        object ACCESS_COARSE_LOCATION : AppPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION, 43,
            R.string.permission_required_text, R.string.permission_required_text
        )

        object ACCESS_BACKGROUND_LOCATION : AppPermission(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION, 43,
            R.string.permission_required_text, R.string.permission_required_text
        )
        object CAMERA : AppPermission(
            Manifest.permission.CAMERA, 43,
            R.string.permission_required_text, R.string.permission_required_text
        )
    }