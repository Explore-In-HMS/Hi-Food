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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.viewmodel.NeartoBuyViewModel
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.*
import kotlinx.android.synthetic.main.near_to_buy_fragment.*
import java.lang.String

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class NearToBuyFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var hmap: HuaweiMap? = null
    private val TAG = "MapViewDemoActivity"
    private val mMapView: MapView? = null
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private var mMarker: Marker? = null
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    val sites: List<Site>? = null

    companion object {
        fun newInstance() = NearToBuyFragment()
    }

    private lateinit var viewModel: NeartoBuyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.near_to_buy_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)
        getLastLocation()

        MapsInitializer.setApiKey("CgB6e3x9o7AUFo2hhiT/FSgY9IyWMXVmfZ3498yNhnH/ZyX/oVpI127YpzxvoJdwrGh6MK0vPgkr6vxJIcv6DbeC")
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this);
    }

    private fun getLastLocation() {
        try {
            val lastLocation =
                mFusedLocationProviderClient.lastLocation
            lastLocation.addOnSuccessListener(OnSuccessListener { location ->
                if (location == null) {
                    Log.i(TAG, "getLastLocation onSuccess location is null")
                    return@OnSuccessListener
                }
                lat = location.latitude
                lng = location.longitude
                Log.i(
                    TAG,
                    "getLastLocation onSuccess location[Longitude,Latitude]:${location.longitude},${location.latitude}"
                )
                return@OnSuccessListener
            }).addOnFailureListener { e ->
                Log.e(TAG, "getLastLocation onFailure:${e.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "getLastLocation exception:${e.message}")
        }
    }

    override fun onMapReady(map: HuaweiMap?) {
        Log.d(TAG, "onMapReady: ");
        hmap = map;

        //Site Kit
        val searchService = SearchServiceFactory.create(
            activity,
            "CgB6e3x9o7AUFo2hhiT/FSgY9IyWMXVmfZ3498yNhnH/ZyX/oVpI127YpzxvoJdwrGh6MK0vPgkr6vxJIcv6DbeC"
        )
        val request = NearbySearchRequest()
        val location = Coordinate(lat, lng)
        request.location = location
        request.query = "Istanbul"
        request.radius = 1000
        request.hwPoiType = HwLocationType.MARKET
        request.language = "en"
        request.pageIndex = 1
        request.pageSize = 5

        val resultListener: SearchResultListener<NearbySearchResponse?> =
            object : SearchResultListener<NearbySearchResponse?> {
                override fun onSearchResult(results: NearbySearchResponse?) {
                    if (results == null || results.totalCount <= 0) {
                        return
                    }
                    val sites: List<Site> = results.sites
                    if (!(sites.size !== 0)) {
                        return
                    }
                    for (site in sites) {
                        mMarker = hmap!!.addMarker(
                            MarkerOptions()
                                .position(LatLng(site.location.lat, site.location.lng))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location_on_24))
                        )
                        val currentLocation = LatLng(site.location.lat, site.location.lng)
                        hmap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f))
                        Log.i(
                            "TAG",
                            String.format(
                                "siteId: '%s', name: %s\r\n",
                                site.siteId,
                                site.name
                            )
                        )
                    }
                }

                // Return the result code and description upon a search exception.
                override fun onSearchError(status: SearchStatus) {
                    Log.i(
                        "TAG",
                        "Error site : " + status.errorCode
                            .toString() + " " + status.errorMessage
                    )
                }
            }
        searchService.nearbySearch(request, resultListener)
    }
}