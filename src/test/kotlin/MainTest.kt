import app.cash.turbine.test
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.*
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.exception.CacheMissException
import com.example.TestVideoQuery
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class MainTest {
  @Test
  fun testStuff() = runBlocking {
    val apolloStore = ApolloStore(MemoryCacheFactory())
    val apolloClientBuilder = ApolloClient.Builder().serverUrl("unused")
    val apolloClient = apolloClientBuilder.store(apolloStore).build()
    val watchedQuery =
      apolloClient
        .query(TestVideoQuery())
        .fetchPolicy(FetchPolicy.CacheOnly)
        .refetchPolicy(FetchPolicy.CacheOnly)
        .writeToCacheAsynchronously(true)
        .watch()

    val testVideoID = 80119234
    val testVideoTitle = "Bright"
    val dataToWrite = TestVideoQuery.Data(
      videos = listOf(
        TestVideoQuery.Video(
          __typename = "Movie",
          videoId = testVideoID,
          title = testVideoTitle,
        )
      )
    )

    watchedQuery.test {
      val failureResponse = awaitItem()
      assertIs<CacheMissException>(failureResponse.exception)

      val keys = apolloStore.writeOperation(TestVideoQuery(), dataToWrite)
      assertEquals(4, keys.size)
      apolloStore.publish(keys)

      val response = awaitItem()
      assertNull(response.exception)
      val video = response.data?.videos?.first()
      assertNotNull(video)
      assertEquals(testVideoID, video?.videoId)
      assertEquals(testVideoTitle, video?.title)
    }
  }
}