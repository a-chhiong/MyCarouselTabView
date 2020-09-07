/*
 * Copyright 2018 The Android Open Source Project
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

import android.os.Bundle
import android.util.Log
import androidx.viewpager2.integration.testapp.cards.Card
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CardViewTabLayoutActivity : CardViewActivity() {

    private lateinit var tabLayout: TabLayout

    override val layoutId: Int = R.layout.activity_tablayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val newPosition: Int = Card.getCarouselPosition(position)
            tab.text = Card.DECK[newPosition].toString()
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var iCurrentItem: Int = -1

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
                    if (iCurrentItem != -1) {
                        viewPager.setCurrentItem(iCurrentItem, false)
                        Log.v("myCarouselTabView", "setCurrentItem(" + iCurrentItem + ")")
                        iCurrentItem = -1   // avoiding of repeated setCurrentItem()
                    }
                }
            }
        })

        viewPager.setCurrentItem(Card.getCarouselInitial(), false)
    }
}
