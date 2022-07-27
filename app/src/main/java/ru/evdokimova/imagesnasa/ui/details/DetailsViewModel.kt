package ru.evdokimova.imagesnasa.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.evdokimova.imagesnasa.data.repository.ImageRepository
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel@Inject constructor(
    private val repository: ImageRepository,
) : ViewModel() {

    private var _href: String = ""
    val href: String
        get() = _href


    fun getHrefLargeImage(nasaId: String) =
        viewModelScope.launch {
            val listHref =  repository.getAssetsImage(nasaId)
            if(listHref.isNotEmpty())
                _href = listHref.first()
        }
}