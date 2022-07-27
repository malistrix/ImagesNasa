package ru.evdokimova.imagesnasa.ui.zoom

import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ru.evdokimova.imagesnasa.data.workers.DownloadImageWorker

class ZoomViewModel : ViewModel() {

    fun startDownloadImageWorker(workManager: WorkManager, href: String) {
        val downloadImageRequest = OneTimeWorkRequestBuilder<DownloadImageWorker>()
            .setInputData(createInputDataForDownloadImageWorker(href)).build()
        workManager.enqueue(downloadImageRequest)
    }

    private fun createInputDataForDownloadImageWorker(href: String): Data =
        Data.Builder().putString("HREF", href).build()

}