package com.example.medicinesapp.social.barcode

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.medicinesapp.CurrentUser
import com.example.medicinesapp.R
import com.example.medicinesapp.data.FriendFirebase
import com.example.medicinesapp.data.User
import com.example.medicinesapp.data.UserBothChecked
import com.example.medicinesapp.databinding.TakePhotoBinding
import com.example.medicinesapp.utill.Helper
import com.example.medicinesapp.utill.listeners.CameraXListener
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.github.sumimakito.awesomeqr.option.logo.Logo
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.take_photo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class BarcodeReadFragment: Fragment() {

    private lateinit var binding: TakePhotoBinding
    private lateinit var cameraExecutor: ExecutorService
    private val permission = Manifest.permission.CAMERA
    private val requestCode = 2
    private var currentUser:CurrentUser?=null
    private var disposable: Disposable?=null
    private val otherIDs = mutableListOf<String>()
    private val listHint = mutableListOf<UserBothChecked>()

    private val viewModel: BarcodeViewModel by viewModels {
        BarcodeViewModelFactory(
            Helper.provideRepository(
                requireContext()
            ), requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listHint.clear()
        binding = TakePhotoBinding.inflate(inflater, container, false)
        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
        }
        var myID = FirebaseAuth.getInstance().uid
        myID?.let {
            // CHANGE THIS LATER !!!
            if(myID=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
                myID = "tu1vewS57OWGDhjoOJrv"
            }
            otherIDs.add(myID!!)
        }


        val ids =  arguments?.getStringArray("friendsID")?.toList()
        ids?.let {
            otherIDs.addAll(it)
        }




        disposable = viewModel.getFriendsSearch("b").subscribe{

            Log.d("1", "C0TU $it $ids ")
            it?.let{
                listHint.add(it)
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.headerimage.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.add.setOnClickListener {
            findNavController().popBackStack()
        }

      viewModel.currentUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
          currentUser=it
          generateBarcode(currentUserToUser(it))
      })


        binding.defaultSwitch.addStatesFromStrings(listOf("Pokaż", "Skanuj"))
        binding.defaultSwitch.addStateListener { stateIndex, _ ->
           when(stateIndex){
               1->{
                   binding.motion.setTransition(R.id.start, R.id.end)
                   binding.motion.transitionToEnd()
               }
               0 ->{
                   binding.motion.setTransition(R.id.end, R.id.start)
                   binding.motion.transitionToEnd()
               }
           }
        }
        return binding.root
    }


    //GENERATE

    private fun generateBarcode(user:User){

        val color = Color()
        color.light = 0xFFFFFFFF.toInt()
        color.dark = 0xFFFF8C8C.toInt()
        color.background = 0xFFFFFFFF.toInt()
        color.auto = false

        val bitmap = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_heart_beat)!!.toBitmap()

        val logo = Logo()
        logo.bitmap = bitmap
        logo.borderRadius = 10
        logo.borderWidth = 10
        logo.scale = 0.3f
        logo.clippingRect = RectF(0F, 0F, 200F, 200F)

        val renderOption = RenderOption()
        val data = getDataToSend(user)
        renderOption.content = data
        renderOption.size = 400
        renderOption.borderWidth=30
        renderOption.color=color
        renderOption.logo=logo


        AwesomeQrRenderer.renderAsync(renderOption, { result ->
            if (result.bitmap != null) {
                val myResult = result.bitmap!!
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        binding.image.setImageBitmap(myResult)
                    }
                }
            }} , { it.printStackTrace()
            Log.d("1", " EXCEPTION $it")
        })

    }

    private fun getDataToSend(user:User):String{
        return Gson().toJson(user)
    }


    //READ

    private fun getDataFromSend(output:String): User? {

        return Gson().fromJson<User>(output,User::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun startCamera() {

        Log.d("1", "startCamera: ")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.createSurfaceProvider())
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        BarcodeAnalyser(
                            object :
                                CameraXListener<String> {
                                override fun onDoneListener(item: String) {
                                    val data = getDataFromSend(item)

                                    Log.d("1", "ANALYZE4: ${data?.email} ")

                                    data?.let {user->
                                        var id = user.uid

                                        if(user.uid=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
                                            id = "tu1vewS57OWGDhjoOJrv"
                                        }

                                        val friend = FriendFirebase(id!!)
                                        viewModel.addUserToFriendList(friend)

                                        binding.mail.text = user.email
                                        binding.name.text = user.name

                                        binding.mcv.alpha = 1.0f
                                        binding.add.isEnabled = true
                                    }

                                    cameraProvider.unbindAll()
                                }
                            })
                    )
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun allPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        permission
    ) == PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (this.requestCode == requestCode) {
            if (allPermissionGranted()) {
                //startCamera()
            }
            else{
                Toast.makeText(requireContext(),
                    "Permissions not granted",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    private fun currentUserToUser(currentUser:CurrentUser): User {

        return User(currentUser.uid,currentUser.name,currentUser.email,currentUser.isDoctor,currentUser.lastActive,currentUser.isOnline,null,null)
    }

}