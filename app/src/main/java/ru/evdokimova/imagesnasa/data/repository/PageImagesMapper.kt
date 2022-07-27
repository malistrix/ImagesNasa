package ru.evdokimova.imagesnasa.data.repository

import ru.evdokimova.imagesnasa.data.entity.ImageEntity
import ru.evdokimova.imagesnasa.data.entity.PageEntity
import ru.evdokimova.imagesnasa.data.models.Image
import ru.evdokimova.imagesnasa.data.models.Page
import javax.inject.Inject


class PageImagesMapper @Inject constructor() {

    fun imageModelToEntity(imageModel: Image) =
        ImageEntity(
            nasaId = imageModel.nasaId,
            title = imageModel.title,
            dateCreated = imageModel.dateCreated,
            description = imageModel.description,
            location = imageModel.location,
            href = imageModel.href,
            imageAssets = imageModel.imageAssets
        )

    fun pageModelToEntity(pageModel: Page, query: String) =
        PageEntity(
            id = 0,
            totalCount = pageModel.totalCountImages,
            prevPage = pageModel.prevPage,
            nextPage = pageModel.nextPage,
            query = query,
        )
}