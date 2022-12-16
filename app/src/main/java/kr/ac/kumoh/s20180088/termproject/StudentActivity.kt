package kr.ac.kumoh.s20180088.termproject

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.collection.LruCache
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.youtube.player.YouTubePlayerView
import kr.ac.kumoh.s20180088.termproject.databinding.ActivityStudentBinding
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class StudentActivity : AppCompatActivity() {
    data class Profile(
        var info_id: Int,
        var profile_image: String,
        var yt_url: String,
        var weapon_name: String,
        var weapon_origin: String,
        var weapon_type: String,
        var weapon_image: String
    )

    companion object {
        const val STUDENT_KEY = "StudentNumber"
        const val QUEUE_TAG = "ProfileVolleyRequest"
        const val SERVER_URL = "https://expresssongdb-rwhxi.run.goorm.io"
    }

    private val profiles = ArrayList<Profile>()

    private lateinit var queue: RequestQueue

    private lateinit var binding: ActivityStudentBinding
    private lateinit var imageLoader: ImageLoader


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        queue = Volley.newRequestQueue(application)

        val student_ID = intent.getIntExtra(STUDENT_KEY, 1)

        imageLoader = ImageLoader(Volley.newRequestQueue(this),
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(100)
                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })

        val request = JsonArrayRequest(
            Request.Method.GET,
            SERVER_URL + "/prof?id=" + student_ID,
            null,
            {
                profiles.clear()
                parseJson(it)
                binding.textName.text = profiles[0].weapon_name
                binding.textOrigin.text = profiles[0].weapon_origin
                binding.textType.text = profiles[0].weapon_type
                binding.imageWeapon.setImageUrl(getWeaponImageUrl(), imageLoader)
                binding.ytPlayer.play(profiles[0].yt_url)
                binding.imageProfile.setImageUrl(getProfileImageUrl(), imageLoader)

            },
            {
                Toast.makeText(application, it.toString(), Toast.LENGTH_LONG).show()
            }
        )
//
        request.tag = QUEUE_TAG
        queue.add(request)

    }

//items.length()
    private fun parseJson(items: JSONArray) {
        for (i in 0..0) {
            val item: JSONObject = items[0] as JSONObject
            val id = item.getInt("info_id")
            val pImage = item.getString("profile_image")
            val urlYT = item.getString("yt_url")
            val name = item.getString("weapon_name")
            val origin = item.getString("weapon_origin")
            val type = item.getString("weapon_type")
            val wImage = item.getString("weapon_image")

            profiles.add(Profile(id, pImage, urlYT, name, origin, type, wImage))
        }
    }

    fun getWeaponImageUrl(): String = "$SERVER_URL/weapon/" + URLEncoder.encode(profiles[0].weapon_image, "utf-8")
    fun getProfileImageUrl(): String = "$SERVER_URL/profile/" + URLEncoder.encode(profiles[0].profile_image, "utf-8")
}