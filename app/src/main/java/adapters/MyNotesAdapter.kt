package adapters

import android.content.Context
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

class MyNotesAdapter(mNotesArray: ArrayList<NotesListingModel.ResponseBean>, mContext: Context, mNotesFragment: NotesFragment?)
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
                (holder as MyNotesAdapter.NotesViewHolder)
                holder.txtTileNotes.text = mNotesArray[position].title
                holder.txtTimeNotes.text = mNotesArray[position].note_title

                holder.llClickNotes.setOnClickListener {
                    if (mNotesFragment != null)
                        mNotesFragment!!.moveToDetail(mNotesArray[position])
                    else
                        mSearchInstance!!.moveToNotesDetail(mNotesArray[position], position)
                }

                holder.llClickNotes.setOnLongClickListener {
                    if (mNotesFragment != null)
                        mNotesFragment!!.deleteNote(mNotesArray[position].id)
                    else
                        mSearchInstance!!.deleteNote(mNotesArray[position])
                    true
                }
                holder.imgShare.setOnClickListener {
                    if (mNotesFragment != null)
                        mNotesFragment!!.moveToShare(mNotesArray[position])
                    else
                        mSearchInstance!!.moveToNoteShare(mNotesArray[position])
                }
            }
            else -> {
                (holder as MyNotesAdapter.LoadMoreViewHolder)
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
            llClickNotes.setBackgroundResource(R.drawable.notes_background)
            imgShare.setImageResource(R.mipmap.ic_share_black)
        }

        private fun displayNightMode() {
            llClickNotes.setBackgroundResource(R.drawable.black_notes_background)
            imgShare.setImageResource(R.mipmap.ic_share_black)
        }
    }
}