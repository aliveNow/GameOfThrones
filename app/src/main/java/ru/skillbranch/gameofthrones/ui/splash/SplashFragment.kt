package ru.skillbranch.gameofthrones.ui.splash

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zys.brokenview.BrokenCallback
import com.zys.brokenview.BrokenConfig
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.koin.getViewModel
import ru.skillbranch.gameofthrones.databinding.FragmentSplashBinding
import ru.skillbranch.gameofthrones.ui.splash.SplashViewModel.AnimationState
import ru.skillbranch.gameofthrones.utils.ui.addOneTimeOnGlobalLayoutListener
import ru.skillbranch.gameofthrones.utils.ui.getDrawableById
import ru.skillbranch.gameofthrones.utils.ui.observeState


class SplashFragment : Fragment() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var vb: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentSplashBinding.inflate(inflater, container, false)
        .also { vb = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.brokenView.setCallback(object : BrokenCallback() {
            override fun onFallingEnd(v: View?) {
                onAnimationEnd()
            }
        })
        vb.imgForeground.addOneTimeOnGlobalLayoutListener {
            viewModel.animation.value?.let { startAnimation(it) }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getKoin().getViewModel(this, SplashViewModel::class)
        viewModel.animation.observeState(this) {
            startAnimation(it)
        }
    }

    private fun onAnimationEnd() {
        viewModel.animationEnded()
    }

    private fun startAnimation(animation: AnimationState) {
        val animView = vb.imgForeground
        if (requireView().isAttachedToWindow && animView.height > 0 && vb.brokenView.getAnimator(animView) == null) {
            resetBrokenView(animation)
            if (viewModel.needToNavigateToMain) {
                findNavController().navigate(
                    SplashFragmentDirections.actionFromSplashFragmentToMainFragment(splashImageId = animation.animatingImageId)
                )
            } else {
                animateBrokenView(animation)
            }
        }
    }

    private fun resetBrokenView(animation: AnimationState) {
        vb.imgBackground.setImageDrawable(getDrawableById(animation.backgroundImageId))
        vb.imgForeground.apply {
            setImageDrawable(getDrawableById(animation.animatingImageId))
            visibility = View.VISIBLE
        }
        vb.brokenView.reset()
    }

    private fun animateBrokenView(animation: AnimationState) {
        with(vb.imgForeground) {
            val point = Point(width / 2, height / 2)
            val config = BrokenConfig(width, height).apply {
                breakDuration = (animation.duration * 0.28).toInt()
                fallDuration = (animation.duration * 0.72).toInt()
            }
            vb.brokenView.createAnimator(this, point, config).start()
        }
    }

}