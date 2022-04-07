package id.nphew.binar.challenge4.splashscreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import id.nphew.binar.challenge4.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.delay

class SplashScreenFragment : Fragment() {
    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val splashTime: Long = 3000

        lifecycleScope.launchWhenCreated {
            splashToLogin(splashTime)
        }
    }
    private suspend fun splashToLogin(time: Long){
        delay(time)
        findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragment3ToLoginFragment())
    }
}