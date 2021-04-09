package com.sn.videoplayer.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sn.videoplayer.R
import com.sn.videoplayer.data.GuideTitle
import kotlinx.android.synthetic.main.item_guide.view.*
import java.util.ArrayList

class GuideAdapter(private var data: ArrayList<GuideTitle>?, var context: Context) :
    RecyclerView.Adapter<GuideAdapter.GuideHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.item_guide, parent, false)
        return GuideHolder(inflate)
    }

    override fun getItemCount(): Int {
        if (data == null) {
            return 0
        }
        return data!!.size
    }

    fun setData(data: ArrayList<GuideTitle>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GuideHolder, position: Int) {
        val guideData = data?.get(position)
        holder.guideTitle.text = guideData?.title
        holder.guideDesc.text = guideData?.desc
        holder.rootView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener?.onClick(it, position)
            }
        }

    }


    class GuideHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var guideTitle: TextView = itemView.tv_guide_title
        var guideDesc : TextView= itemView.tv_guide_desc
        var rootView: ConstraintLayout = itemView.root_view
    }

    var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(view: View, position: Int)
    }
}