package ru.skillbranch.gameofthrones.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.zys.brokenview.BrokenCallback
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var vb: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentSplashBinding.inflate(inflater, container, false)
        .also { vb = it }
        .root

    var firstImageId: Int = R.drawable.spash
    var secondImageId: Int = R.drawable.stark_coast_of_arms

    val imageIds = listOf(
        R.drawable.stark_coast_of_arms,
        R.drawable.lannister__coast_of_arms,
        R.drawable.baratheon_coast_of_arms,
        R.drawable.greyjoy_coast_of_arms,
        R.drawable.martel_coast_of_arms,
        R.drawable.baratheon_coast_of_arms
    )

    var imagePosition = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.brokenView.setCallback(object : BrokenCallback() {
            override fun onFallingEnd(v: View?) {
                imagePosition = when (imagePosition) {
                    imageIds.size - 1 -> 0
                    else -> imagePosition + 1
                }
                firstImageId = secondImageId
                secondImageId = imageIds[imagePosition]
                vb.imgFirst.setImageDrawable(requireContext().getDrawable(firstImageId))
                vb.imgSecond.setImageDrawable(requireContext().getDrawable(secondImageId))
                vb.imgFirst.visibility = View.VISIBLE
                vb.brokenView.reset()
                vb.brokenView.createAnimator(vb.imgFirst).start()
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        viewModel.navigateToMain.observe(viewLifecycleOwner, Observer {
            vb.brokenView.reset()
            findNavController().navigate(SplashFragmentDirections.actionFromSplashFragmentToMainFragment())
        })
        viewModel.showAnimation.observe(viewLifecycleOwner, Observer {
            vb.brokenView.createAnimator(vb.imgFirst)
                .start()
        })
    }

    override fun onResume() {
        super.onResume()

    }

}