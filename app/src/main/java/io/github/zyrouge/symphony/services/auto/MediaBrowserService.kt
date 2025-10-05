package io.github.zyrouge.symphony.services.auto

import android.app.Application
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.media.MediaBrowserServiceCompat
import io.github.zyrouge.symphony.Symphony


class MediaBrowserService: MediaBrowserServiceCompat() {
    val viewModelStoreOwner = object: ViewModelStoreOwner {
        override val viewModelStore = ViewModelStore()
    }
    lateinit var viewModerProvider: ViewModelProvider

    override fun onCreate() {
        Log.w("debug", "🚩🚩🚩🚩🚩🚩")
        super.onCreate()
        viewModerProvider = ViewModelProvider.create(
            viewModelStoreOwner,
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return (Symphony(application) as? T) ?: super.create(modelClass)
                }
            },
            CreationExtras.Empty)

        val symphony: Symphony = viewModerProvider[Symphony::class]
        symphony.emitActivityReady()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot? {
        val symphony = viewModerProvider.get(Symphony::class)

        return BrowserRoot(ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem?>?>,
    ) {
        Log.w("debug", parentId)
        val symphony = viewModerProvider.get(Symphony::class)
        val items: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()

        when(parentId) {
            ROOT_ID -> {
                symphony.groove.exposer.explorer.name
                items.add(MediaBrowserCompat.MediaItem(
                    MediaDescriptionCompat.Builder()
                        .setMediaId(FILES_ID)
                        .setTitle("Files")
                        .build(),
                    MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
                ))
            }
            FILES_ID -> {

            }
        }

        result.sendResult(items)
    }


    companion object {
        val ROOT_ID = ":root:"
        val FILES_ID = ":files:"
    }
}