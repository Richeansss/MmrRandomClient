import com.example.mrrrandomclient.model.Player
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/players/register")
    fun registerPlayer(@Query("name") name: String): Call<Player>

    @POST("/players/battle")
    fun startBattle(): Call<String>

    @GET("/players/ranking")
    fun getRanking(): Call<List<Player>>
}
