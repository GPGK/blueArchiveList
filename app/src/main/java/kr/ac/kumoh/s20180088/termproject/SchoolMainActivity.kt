package kr.ac.kumoh.s20180088.termproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20180088.termproject.databinding.ActivitySchoolMainBinding

class SchoolMainActivity : AppCompatActivity() {
    companion object {
        const val KEY_ID = "SchoolNum"
    }

    private lateinit var binding: ActivitySchoolMainBinding
    private lateinit var model: StudentViewModel
    private val studentAdapter = StudentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchoolMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val school_id = intent.getIntExtra(KEY_ID, 1)


        model = ViewModelProvider(this)[StudentViewModel(application)::class.java]

        binding.studentList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = studentAdapter
        }

        model.updateValue(school_id)

        model.list.observe(this) {
            studentAdapter.notifyItemRangeInserted(0,
                model.list.value?.size ?: 0)
        }

        model.requestStudent()
    }

    inner class StudentAdapter: RecyclerView.Adapter<StudentAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View)
            : RecyclerView.ViewHolder(itemView), OnClickListener{
            val txName: TextView = itemView.findViewById(R.id.text1)
            val txSchool: TextView = itemView.findViewById(R.id.text2)
            val txClub: TextView = itemView.findViewById(R.id.text3)
            val niImage: NetworkImageView = itemView.findViewById(R.id.image)

            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                var intent = Intent(application, StudentActivity::class.java)

                intent.putExtra(StudentActivity.STUDENT_KEY, model.list.value?.get(adapterPosition)?.stu_id)

                startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_student,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txName.text = model.list.value?.get(position)?.stu_name
            holder.txClub.text = model.list.value?.get(position)?.stu_club
            holder.txSchool.text = model.list.value?.get(position)?.stu_school
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)
        }

        override fun getItemCount() = model.list.value?.size ?: 0
    }

}