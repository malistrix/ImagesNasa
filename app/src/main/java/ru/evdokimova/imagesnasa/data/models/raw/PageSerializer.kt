package ru.evdokimova.imagesnasa.data.models.raw

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import ru.evdokimova.imagesnasa.data.models.Page
import ru.evdokimova.imagesnasa.data.models.Image
import ru.evdokimova.imagesnasa.utils.Constants.Companion.MAX_SIZE_ITEMS_ON_PAGE

class PageSerializer: KSerializer<Page> {
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("Collection") {
            element<List<Image>>("items")
            element<Metadata>("metadata")
            element<List<Link>>("links", isOptional = true)
        }

    override fun deserialize(decoder: Decoder): Page {

        return decoder.decodeStructure(descriptor) {
            val listItem = decodeSerializableElement(
                descriptor,
                0, //индекс элемента в descriptor
                ListSerializer(Image.serializer())
            )

            val metadata = decodeSerializableElement(
                descriptor,
                1,
                Metadata.serializer())

            var prev: Int? = null
            var next: Int? = null

            // Если total_hits <= MAX_SIZE_ITEMS_ON_PAGE,
            // елемент "links" будет отсутствовать в json и возникнет NoSuchElementException
            if (metadata.total_hits > MAX_SIZE_ITEMS_ON_PAGE) {
                val links = decodeSerializableElement(
                    descriptor,
                    2, //индекс элемента в descriptor
                    ListSerializer(Link.serializer())
                )
                for (link in links) {
                    when (link.rel) {
                        "prev" -> {
                            prev = link.href.substringAfter("page=").toInt()
                        }
                        "next" -> {
                            next = link.href.substringAfter("page=").toInt()
                        }
                    }
                }
            }
            Page(listItem, metadata.total_hits, prev, next)
        }
    }

    override fun serialize(encoder: Encoder, value: Page) {
        //Сериализация не нужна
    }
}