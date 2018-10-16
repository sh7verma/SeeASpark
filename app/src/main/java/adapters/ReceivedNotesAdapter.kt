package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import com.seeaspark.SearchActivity
import fragments.NotesFragment
import kotlinx.android.synthetic.main.item_notes.view.*
import kotlinx.android.synthetic.main.item_progress.view.*
import models.NotesListingModel
import utils.Constants
import utils.Utils

class ReceivedNotesAdapter(mNotesArray: ArrayList<NotesListingModel.ResponseBean>, mContext: Context, mNotesFragment: NotesFragment?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mNotesArray = ArrayList<NotesListingModel.ResponseBean>()
    var mContext: Context? = null
    var mUtils: Utils? = null
    private var mNotesFragment: NotesFragment? = null
    private var mSearchInstance: SearchActivity? = null

    constructor(mNotesArray: ArrayList<NotesListingModel.ResponseBean>, mContext: Context, mNotesFragment: Nothing?,
                mSearchInstance: SearchActivity) : this(mNotesArray, mContext, mNotesFragment) {
        this.mSearchInstance = mSearchInstance
    }

    init {
        this.mNotesArray = mNotesArray
        this.mContext = mContext
        mUtils = Utils(mContext)
        this.mNotesFragment = mNotesFragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            Constants.RECEIVEDNOTES.toInt() -> {
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
            Constants.RECEIVEDNOTES -> {
                (holder as ReceivedNotesAdapter.NotesViewHolder)
                holder.txtTileNotes.text = mNotesArray[position].title
                holder.txtTimeNotes.text = mNotesArray[position].note_title

                holder.txtSentBY.text = mNotesArray[position].full_name

                holder.llClickNotes.setOnClickListener {
                    if (mNotesFragment != null)
                        mNotesFragment!!.moveToDetail(mNotesArray[position])
                    else
                        mSearchInstance!!.moveToNotesDetail(mNotesArray[position], position)
                }
                holder.llClickNotes.setOnLongClickListener {
                    if (mNotesFragment != null)
                        mNotesFragment!!.deleteReceivedNote(mNotesArray[position].id, mNotesArray[position].name)
                    else
                        mSearchInstance!!.deleteReceivedNote(mNotesArray[position])
                    true
                }
            }
            else -> {
                (holder as ReceivedNotesAdapter.LoadMoreViewHolder)
            }
        }
    }

    override fun getItemCount(): Int {
        return mNotesArray.size
    }

    override fun getItemViewType(position: Int): Int {
        when (mNotesArray[position].note_type.toInt()) {
            2 -> return Constants.RECEIVEDNOTES.toInt()
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
        val txtSentBY = itemView.txtSentBY!!
        val llSent = itemView.llSent!!
        val txtSentHint = itemView.txtSentHint!!


        init {
            llSent.visibility = View.VISIBLE
            imgShare.visibility = View.INVISIBLE
            if (mSearchInstance == null) {
                if (mUtils!!.getInt("nightMode", 0) == 1)
                    displayNightMode()
                else
                    displayDayMode()
            }
        }

        private fun displayDayMode() {
            llClickNotes.setBackgroundResource(R.drawable.notes_background)
            imgShare.setImageResource(R.mipmap.ic_share_white)
            txtSentBY.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
            txtSentHint.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
        }

        private fun displayNightMode() {
            llClickNotes.setBackgroundResource(R.drawable.black_notes_background)
            imgShare.setImageResource(R.mipmap.ic_share_no_bg)
            txtSentBY.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))
            txtSentHint.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        }

    }
}