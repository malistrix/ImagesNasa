package ru.evdokimova.imagesnasa

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test

import org.junit.Assert.*
import ru.evdokimova.imagesnasa.data.models.raw.RawImagesResponse

/**
 * Тесты для проверки успешной десериализации response в модели
 */
class SerializerUnitTest {
    @Test
    fun testSimpleResponse() {
        val format = Json { ignoreUnknownKeys = true }
        val response = format.decodeFromString<RawImagesResponse>(responseSimple)
        assertEquals(2, response.page.nextPage)
        assertEquals(null, response.page.prevPage)
        assertEquals(102, response.page.totalCountImages)
        assertEquals(2, response.page.images.size)
        assertEquals("PIA18906", response.page.images[0].nasaId)
        assertEquals("Sun Shines in High-Energy X-rays", response.page.images[0].title)
        assertEquals("2014-12-22T18:53:13Z", response.page.images[0].dateCreated)
        assertEquals(null, response.page.images[0].location)
        assertEquals(
            "https://images-assets.nasa.gov/image/PIA18906/collection.json",
            response.page.images[0].imageAssets
        )
        assertEquals(
            "https://images-assets.nasa.gov/image/PIA18906/PIA18906~thumb.jpg",
            response.page.images[0].href
        )
        assertEquals(
            "X-rays stream off the sun ",
            response.page.images[0].description
        )
    }

    @Test
    fun testResponseWithEmptyItem() {
        val format = Json { ignoreUnknownKeys = true }
        val response = format.decodeFromString<RawImagesResponse>(responseWithEmptyItem)
        assertEquals(true, response.page.images.isEmpty())
        assertEquals(0, response.page.totalCountImages)
        assertEquals(null, response.page.prevPage)
        assertEquals(null, response.page.nextPage)
    }

    private val responseSimple = """{"collection":
        |{"version":"1.0",
        |"href":"http://images-api.nasa.gov/search?q=sun&media_type=image",
        |"items":[
            |{"href":"https://images-assets.nasa.gov/image/PIA18906/collection.json",
                |"data":[{"center":"JPL","title":"Sun Shines in High-Energy X-rays","nasa_id":"PIA18906",
                    |"date_created":"2014-12-22T18:53:13Z",
                    |"keywords":["Sun","NuSTAR"],
                    |"media_type":"image",
                    |"description_508":"X-rays stream off the sun in this first picture of the sun..",
                    |"secondary_creator":"NASA/JPL-Caltech/GSFC","description":"X-rays stream off the sun "}],
               |"links":[{"href":"https://images-assets.nasa.gov/image/PIA18906/PIA18906~thumb.jpg","rel":"preview","render":"image"}]},
           |{"href":"https://images-assets.nasa.gov/image/PIA15179/collection.json",
               |"data":[{"center":"JPL","title":"The Sun Magnetic Field","nasa_id":"PIA15179",
                    |"date_created":"2011-12-16T19:18:05Z","keywords":["Sun","Voyager"],"media_type":"image",
                    |"description_508":"This frame from an animation shows how the.",
                    |"secondary_creator":"NASA/JPL-Caltech","description":"This frame from an animation "}],
               |"links":[{"href":"https://images-assets.nasa.gov/image/PIA15179/PIA15179~thumb.jpg","rel":"preview","render":"image"}]}],
       |"metadata":{"total_hits":102},"links":[{"rel":"next","prompt":"Next","href":"http://images-api.nasa.gov/search?q=sun&media_type=image&page=2"}]}}
       |""".trimMargin()

    private val responseWithEmptyItem = """{"collection":
    |{"version":"1.0",
    |"href":"http://images-api.nasa.gov/search?q=rerwrwe&media_type=image&page=1",
    |"items":[],
    |"metadata":{"total_hits":0}}}""".trimMargin()
}


