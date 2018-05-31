package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_bio.*


class BioFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_bio, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun initListener() {
        imgBackBio.setOnClickListener(this)
        txtNextBio.setOnClickListener(this)
    }

    private fun onCreateStuff() {
        txtCountCharacter

        edBioProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length > 0)
                    txtCountCharacter.text = "${320 - p0!!.length} Characters Left"
                else
                    txtCountCharacter.text = "320 Characters Left"
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    override fun onClick(view: View) {
        when (view) {
            imgBackBio -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
            txtNextBio -> {
                mCreateProfileInstance!!.moveToNext()
            }
        }
    }
}
