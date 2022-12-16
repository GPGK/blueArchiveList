package kr.ac.kumoh.s20180088.termproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20180088.termproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: SchoolViewModel
    private val schoolAdapter = SchoolAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = ViewModelProvider(this)[SchoolViewModel::class.java]

        binding.list.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = schoolAdapter
        }

        model.list.observe(this) {
            schoolAdapter.notifyItemRangeInserted(0,
                model.list.value?.size ?: 0)
        }

        model.requestSchool()
    }

    inner class SchoolAdapter: RecyclerView.Adapter<SchoolAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View)
            : RecyclerView.ViewHolder(itemView), OnClickListener {
            val txName: TextView = itemView.findViewById(R.id.text1)
            val txDescrip: TextView = itemView.findViewById(R.id.text2)
            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)

            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val intent = Intent(application, SchoolMainActivity::class.java)

                intent.putExtra(SchoolMainActivity.KEY_ID, model.list.value?.get(adapterPosition)?.sch_id)

                startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_school,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txName.text = model.list.value?.get(position)?.sch_name
            holder.txDescrip.text = model.list.value?.get(position)?.sch_description
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)
        }

        override fun getItemCount() = model.list.value?.size ?: 0
    }
}