package ru.evdokimova.imagesnasa.data.models.raw

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import ru.evdokimova.imagesnasa.data.models.Page

@Serializable
data class RawImagesResponse(
    @Serializable(UnwrappingJsonSerializerCollection::class)
    @SerialName("collection")
    val page: Page

)
object UnwrappingJsonSerializerCollection:
    JsonTransformingSerializer<Page>(Page.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element
    }
}