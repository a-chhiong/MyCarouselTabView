/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.viewpager2.integration.testapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.integration.testapp.cards.Card
import androidx.viewpager2.integration.testapp.cards.CarouselCardViewAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ParallelCardViewTabLayoutActivity : Activity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewPager = ViewPager2(this).apply {
            layoutParams = matchParent()
            orientation = ORIENTATION_HORIZONTAL
            adapter = VpAdapter()
            isNestedScrollingEnabled = true
            isHorizontalScrollBarEnabled = true
        }
        setContentView(viewPager)
    }

    class VpAdapter : RecyclerView.Adapter<VpAdapter.ViewHolder>() {
        override fun getItemCount(): Int {
            return 4
        }

        @SuppressLint("ResourceType")
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val root = inflater.inflate(R.layout.item_nested_carousel_tab, parent, false)
            return ViewHolder(root).apply {
                init()
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder) {
                title.text = title.context.getString(R.string.page_position, adapterPosition)
                itemView.setBackgroundResource(PAGE_COLORS[position % PAGE_COLORS.size])
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var iCurrentItem: Int = -1
            val title: TextView = itemView.findViewById(R.id.page_title)
            val tabLayout: TabLayout = itemView.findViewById(R.id.tabs)
            val viewPager: ViewPager2 = itemView.findViewById(R.id.view_pager)

            fun init(){
                viewPager.adapter = CarouselCardViewAdapter()

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    val newPosition: Int = Card.getCarouselPosition(position)
                    tab.text = Card.DECK[newPosition].toString()
                }.attach()

                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        Log.v("myCarouselTabView", "onPageSelected, position: " + position)
                        iCurrentItem = Card.getCarouselOffset(position)
                        Log.v("myCarouselTabView", "iCurrentItem = " + iCurrentItem)
                    }

                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        Log.v("myCarouselTabView", "onPageScrolled: position: " + position + ", positionOffset: " + positionOffset + ", positionOffsetPixels: " + positionOffsetPixels)
                        iCurrentItem = Card.getCarouselOffset(position)
                        Log.v("myCarouselTabView", "iCurrentItem = " + iCurrentItem)
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                        Log.v("myCarouselTabView", "onPageScrollStateChanged, state = " + state)
                        if (state == ViewPager2.SCROLL_STATE_IDLE) {
                            updateCurrentItem()
                        }
                    }
                })

                viewPager.setCurrentItem(Card.getCarouselInitial(), false)
            }

            fun updateCurrentItem() {
                if (iCurrentItem != -1) {
                    viewPager.setCurrentItem(iCurrentItem, false)
                    Log.v("myCarouselTabView", "setCurrentItem(" + iCurrentItem + ")")
                    iCurrentItem = -1   // avoiding of repeated setCurrentItem()
                }
            }
        }
    }
}
