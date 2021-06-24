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
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.permission.AppPermission
import com.hms.referenceapp.hifood.permission.handlePermission
import com.hms.referenceapp.hifood.permission.requestPermission
import com.hms.referenceapp.hifood.viewmodel.ScanViewModel
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions

private const val REQUEST_CODE_SCAN_ONE = 1112

class ScanFragment : Fragment() {

    companion object {
        fun newInstance() = ScanFragment()
    }

    private lateinit var viewModel: ScanViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ScanViewModel::class.java)

        return inflater.inflate(R.layout.scan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startScan()

        viewModel.convenienceExistancy.observe(requireActivity(), Observer {
            if (it) {
                val action =
                    ScanFragmentDirections.actionScanFragmentToConvenienceDetailFragment(viewModel.scannedConvenience.value?.id)
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "NOT FOUND", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun startScan() {
        requestPermission(AppPermission.CAMERA)
        handlePermission(AppPermission.CAMERA,
            onGranted = {
                val options = HmsScanAnalyzerOptions.Creator().create()
                ScanUtil.startScan(requireActivity(), REQUEST_CODE_SCAN_ONE, options)
            },
            onDenied = {
                Log.e("MainFragment", "permission denied")
            },
            onRationaleNeeded = {
                Log.e("MainFragment", "permission denied")
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            val obj: HmsScan? = data?.getParcelableExtra<HmsScan>(ScanUtil.RESULT)
            viewModel.getConvenience(obj!!.originalValue!!)
        }
    }
}