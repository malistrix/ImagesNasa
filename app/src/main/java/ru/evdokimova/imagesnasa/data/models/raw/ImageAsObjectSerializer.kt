package ru.evdokimova.imagesnasa.data.models.raw

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import ru.evdokimova.imagesnasa.data.models.Image

class ImageAsObjectSerializer : KSerializer<Image> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("Item") {
            element<List<Data>>("data")
            element<List<Link>>("links")
            element<String>("href")
        }

    override fun deserialize(decoder: Decoder): Image {

        return decoder.decodeStructure(descriptor) {
            val data = decodeSerializableElement(
                descriptor,
                0, //индекс элемента в descriptor
                ListSerializer(Data.serializer())
            )

            val links = decodeSerializableElement(
                descriptor,
                1,
                ListSerializer(Link.serializer())
            )

            val imgAssets = decodeSerializableElement(descriptor, 2, String.serializer())

            Image(
                nasaId = data[0].nasaId,
                title = data[0].title,
                dateCreated = data[0].dateCreated,
                description = data[0].description,
                location = data[0].location.ifEmpty { null },
                href = links[0].href,
                imageAssets = imgAssets
            )
        }
    }

    override fun serialize(encoder: Encoder, value: Image) {
        //Сериализация не нужна
    }
}
