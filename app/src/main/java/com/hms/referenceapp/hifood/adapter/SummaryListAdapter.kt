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

package com.hms.referenceapp.hifood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hms.referenceapp.hifood.R
import com.hms.referenceapp.hifood.model.LogSummary
import kotlinx.android.synthetic.main.item_user_summary_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SummaryListAdapter(
    var logs: ArrayList<LogSummary>,
    var clickListener: OnSummaryClickListener
) : RecyclerView.Adapter<SummaryListAdapter.SummaryLogsViewHolder>() {

    fun updateLogs(newLogs: List<LogSummary>) {
        logs.clear()
        logs.addAll(newLogs)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SummaryLogsViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_summary_layout, parent, false)
    )

    override fun getItemCount() = logs.size

    override fun onBindViewHolder(holder: SummaryListAdapter.SummaryLogsViewHolder, position: Int) {
        holder.bind(logs[position], clickListener)
    }

    class SummaryLogsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val date = view.dateTV
        private val calories = view.caloriesValueTV

        val dateFormat: SimpleDateFormat = SimpleDateFormat("dd-MMM-yyyy")

        fun bind(log: LogSummary, action: OnSummaryClickListener) {
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            date.text = dateFormat.format(log.date)
            calories.text = log.calories.toString()

            itemView.setOnClickListener {
                action.onItemClick(log)
            }
        }
    }
}

interface OnSummaryClickListener {
    fun onItemClick(item: LogSummary)
}