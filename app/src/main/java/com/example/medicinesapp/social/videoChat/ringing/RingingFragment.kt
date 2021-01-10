package com.example.medicinesapp.social.videoChat.ringing

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.agik.AGIKSwipeButton.Controller.OnSwipeCompleteListener
import com.agik.AGIKSwipeButton.View.Swipe_Button_View
import com.bumptech.glide.Glide
import com.example.medicinesapp.R
import com.example.medicinesapp.utill.Helper


class RingingFragment: DialogFragment() {

    private val viewModel: RingingViewModel by viewModels {
        RingingViewModelFactory(
            Helper.provideRepository(
                requireContext()
            ), requireActivity().application
        )
    }

    private lateinit var image: ImageView
    private lateinit var stopButton : Swipe_Button_View
    private lateinit var goButton : Swipe_Button_View
    private lateinit var text: TextView
    private lateinit var vibrator:Vibrator


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.ringing_dialog,container,false)
        getElements(view)

        val ids = arguments?.getString("id")


        ids?.let {
            val divided = it.split("&AND&")
            val videoID = divided.first()
            val firebaseID = divided.last()

            Log.d("1", "MAM MAM $videoID AND $firebaseID")

            viewModel.getUserById(firebaseID)
            viewModel.getUserPhotoById(firebaseID)

            vibratePhone()


            stopButton.setOnSwipeCompleteListener_forward_reverse(object : OnSwipeCompleteListener {
                override fun onSwipe_Forward(swipe_button_view: Swipe_Button_View?) {
                }

                override fun onSwipe_Reverse(swipeView: Swipe_Button_View) {
                    dismiss()
                }
            })

            goButton.setOnSwipeCompleteListener_forward_reverse(object : OnSwipeCompleteListener {
                override fun onSwipe_Forward(swipe_button_view: Swipe_Button_View?) {
                    vibrator.cancel()
                    val bundle = bundleOf("calling" to ids)
                    findNavController().navigate(R.id.action_ringing_to_videoChat, bundle)
                }

                override fun onSwipe_Reverse(swipe_button_view: Swipe_Button_View?) {
                }
            })
        }


        viewModel.user.observe(viewLifecycleOwner, Observer {
            it?.let {user->
                Log.d("1", "MAM USERA $user")
                text.text = user.name

                val path = user.photoPath
                path?.let {myPath->
                    viewModel.getUserPhotoById(myPath)
                }
            }
        })


        viewModel.userPhoto.observe(viewLifecycleOwner, Observer {
            it?.let {
                Glide.with(requireActivity())
                    .load(it)
                    .into(image)
            }
        })


        return view
    }

    private fun Fragment.vibratePhone() {

         vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mVibratePattern =
                    longArrayOf(0, 400, 800, 600, 800, 800, 800, 1000)
                val mAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
                vibrator.vibrate(VibrationEffect.createWaveform(mVibratePattern,mAmplitudes,-1))
            } else {
                vibrator.vibrate(5000)
            }
        }
    }

    private fun getElements(view: View){
        image = view.findViewById(R.id.image)
        stopButton = view.findViewById(R.id.stop)
        goButton = view.findViewById(R.id.start)
        text = view.findViewById(R.id.text2)
    }

}