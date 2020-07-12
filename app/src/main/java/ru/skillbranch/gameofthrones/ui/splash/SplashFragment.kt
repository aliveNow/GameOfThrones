package ru.skillbranch.gameofthrones.ui.splash

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var vb: FragmentSplashBinding
    //private lateinit var brokenView: BrokenView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentSplashBinding.inflate(inflater, container, false)
        .also { vb = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //brokenView = BrokenView.add2Window(requireActivity())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        viewModel.navigateToMain.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.actionFromSplashFragmentToMainFragment)
        })
        viewModel.showAnimation.observe(viewLifecycleOwner, Observer {

            vb.brokenView.createAnimator(view?.findViewById(R.id.imgFirst), Point(150, 150), null)
                .start()
        })
    }

    override fun onResume() {
        super.onResume()

    }

}