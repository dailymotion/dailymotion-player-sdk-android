package com.dailymotion.android.player.sdk.test

import com.dailymotion.android.player.sdk.iab.OMHelper
import org.junit.Test

class OMHelperScriptTest {
    val listPayloads = listOf<String>(
        //Enter Payload here
    )
    @Test
    fun test() {
        listPayloads.forEach {
            OMHelper.parseVerificationScriptData(it).forEach {
                println("---URL = ${it.url}")
                println("---PARAMETERS = ${it.parameters}")
                println("---VENDOR = ${it.vendorKey}")
                assert(it.url.isNotEmpty())
                assert(it.parameters.isNotEmpty())
                assert(it.vendorKey.isNotEmpty())
            }
        }
    }
}