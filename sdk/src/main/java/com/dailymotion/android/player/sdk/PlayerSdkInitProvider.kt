package com.dailymotion.android.player.sdk

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayerSdkInitProvider : ContentProvider() {

    companion object {
        internal lateinit var appContext: Context
            private set

        internal val visitorInfoManager = VisitorInfoManager()
    }

    override fun onCreate(): Boolean {
        context?.let {
            appContext = it
            CoroutineScope(Dispatchers.Main).launch {
                visitorInfoManager.getAdvertisingInfo(appContext)
            }
        }
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        throw Exception("unimplemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        throw Exception("unimplemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw Exception("unimplemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw Exception("unimplemented")
    }

    override fun getType(uri: Uri): String {
        throw Exception("unimplemented")
    }

}