package com.shimao.mybugly.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.shimao.mybugly.R
import com.shimao.mybuglylib.core.JJBugReport
import com.shimao.mybuglylib.data.model.FragmentEvent

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        pageViewModel.text.observe(this, Observer<String> {
            textView.text = it
        })
        textView.setOnClickListener {
            val array = arrayOf(1,2,3)
            print(array[3])
        }
        return root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            JJBugReport.getInstance().addFragmentRecord(
                FragmentEvent(
                    System.currentTimeMillis(), this::class.java.name+"_"+parentFragmentManager.fragments.indexOf(this), "show"
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if(isVisible) {
            JJBugReport.getInstance().addFragmentRecord(
                FragmentEvent(
                    System.currentTimeMillis(), this::class.java.name+"_"+parentFragmentManager.fragments.indexOf(this), "show"
                )
            )
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            JJBugReport.getInstance().addFragmentRecord(
                FragmentEvent(
                    System.currentTimeMillis(), this::class.java.name+"_"+parentFragmentManager.fragments.indexOf(this), "show"
                )
            )
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}