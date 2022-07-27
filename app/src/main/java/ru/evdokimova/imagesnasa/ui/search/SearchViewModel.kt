package ru.evdokimova.imagesnasa.ui.search

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.evdokimova.imagesnasa.data.entity.ImageEntity
import ru.evdokimova.imagesnasa.data.entity.PageEntity
import ru.evdokimova.imagesnasa.data.repository.PageImagesRepository
import ru.evdokimova.imagesnasa.utils.ConnectivityLiveData
import ru.evdokimova.imagesnasa.utils.Constants
import ru.evdokimova.imagesnasa.utils.Resource
import javax.inject.Inject

private const val TAG = "SearchViewModel "

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: PageImagesRepository,
    private val connectionLiveData: ConnectivityLiveData
) : ViewModel() {

    private val _imagesLiveData: MutableLiveData<Resource<List<ImageEntity>>> = MutableLiveData()
    val imagesLiveData: LiveData<Resource<List<ImageEntity>>>
        get() = _imagesLiveData

    var page: PageEntity? = null

    var hasInternet = false

    private val observerConnection = Observer<Boolean> { hasInternet ->
        hasInternet?.let {
            Log.d(TAG, "observer Connection: $hasInternet")
            this.hasInternet = hasInternet
        }
    }

    init {
        restorePageImages()
        connectionLiveData.observeForever(observerConnection)
    }

    private fun restorePageImages() = viewModelScope.launch {
        page = repository.getPage() ?: return@launch
        _imagesLiveData.postValue(repository.getResourceImagesFromDB())
    }

    fun getImages(query: String, pageN: Int = Constants.DEFAULT_PAGE) =
        viewModelScope.launch {
            val lastData = _imagesLiveData.value?.data
            _imagesLiveData.postValue(Resource.Loading())
            if (hasInternet) {
                val newImages = repository.getNewResourceImages(query, pageN)
                if (newImages is Resource.Error) {
                    newImages.data = lastData
                } else {
                    page = repository.getPage()
                }
                _imagesLiveData.postValue(newImages)
            } else {
                _imagesLiveData.postValue(Resource.Error(message = "No Internet", lastData))
            }
        }

    override fun onCleared() {
        super.onCleared()
        connectionLiveData.removeObserver(observerConnection)
    }
}
