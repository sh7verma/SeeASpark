package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.MyNotesActivity
import com.seeaspark.R
import com.seeaspark.SearchNotesActivity
import kotlinx.android.synthetic.main.item_notes.view.*
import kotlinx.android.synthetic.main.item_progress.view.*
import models.NotesListingModel
import utils.Constants
import utils.Utils

/**
 * Created by dev on 9/8/18.
 */
class ShareNotesAdapter (mNotesArray: ArrayList<NotesListingModel.ResponseBean>, mContext: Context, mNotesActivity: MyNotesActivity?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mNotesArray = ArrayList<NotesListingModel.ResponseBean>()
    var mContext: Context? = null
    var mUtils: Utils? = null
    private var mNotesActivity: MyNotesActivity? = null
    private var mSearchInstance: SearchNotesActivity? = null

    constructor(mNotesArray: ArrayList<NotesListingModel.ResponseBean>, mContext: Context, mNotesActivity: Nothing?,
                mSearchInstance: SearchNotesActivity) : this(mNotesArray, mContext, mNotesActivity) {
        this.mSearchInstance = mSearchInstance
    }

    init {
        this.mNotesArray = mNotesArray
        this.mContext = mContext
        mUtils = Utils(mContext)
        this.mNotesActivity = mNotesActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            Constants.MYNOTES.toInt() -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_notes, parent, false)
                NotesViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
                LoadMoreViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (mNotesArray[position].note_type) {
            Constants.MYNOTES -> {
                (holder as ShareNotesAdapter.NotesViewHolder)
                holder.txtTileNotes.text = mNotesArray[position].title
                holder.txtTimeNotes.text = Constants.displayDateTimeNotes(mNotesArray[position].updated_at)

                holder.llClickNotes.setOnClickListener {
                    if (mNotesActivity != null)
                        mNotesActivity!!.moveToDetail(mNotesArray[position])
//                    else
//                        mSearchInstance!!.moveToNotesDetail(mNotesArray[position], position)
                }

            }
            else -> {
                (holder as ShareNotesAdapter.LoadMoreViewHolder)
            }
        }
    }

    override fun getItemCount(): Int {
        return mNotesArray.size
    }

    override fun getItemViewType(position: Int): Int {
        when (mNotesArray[position].note_type.toInt()) {
            1 -> return Constants.MYNOTES.toInt()
            else -> Constants.PROGRESS
        }
        return 0
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar = itemView.progressBar!!
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llClickNotes = itemView.llClickNotes!!
        val txtTimeNotes = itemView.txtTimeNotes!!
        val txtTileNotes = itemView.txtTileNotes!!
        val imgShare = itemView.imgShare!!

        init {
            if (mSearchInstance == null) {
                if (mUtils!!.getInt("nightMode", 0) == 1)
                    displayNightMode()
                else
                    displayDayMode()
            }
        }

        private fun displayDayMode() {
//            llClickNotes.setBackgroundResource(R.drawable.notes_background)
            imgShare.setVisibility(View.GONE)
        }

        private fun displayNightMode() {
//            llClickNotes.setBackgroundResource(R.drawable.black_notes_background)
            imgShare.setVisibility(View.GONE)
        }
    }
}