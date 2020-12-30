package com.example.medicinesapp.social.videoChat

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.medicinesapp.R
import com.example.medicinesapp.utill.Helper
import io.skyway.Peer.*
import io.skyway.Peer.Browser.Canvas
import io.skyway.Peer.Browser.MediaConstraints
import io.skyway.Peer.Browser.MediaStream
import io.skyway.Peer.Browser.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.ArrayList

class VideoChatFragment: Fragment() {

    private val apiKey = "71e9e570-b94d-445c-ae30-585c90596f80"
    private val domain = "mymedicineapp"

    private var peer: Peer?=null
    private  var localStream: MediaStream?=null
    private  var remoteStream: MediaStream?=null
    private var mediaConnection: MediaConnection?=null
    private  var ownID : String?=null
    private var isConnected : Boolean = false

    private lateinit var canvas: View
    private lateinit var canvas2: View
    private lateinit var switchCameraAction:ImageView


    private lateinit var handler: Handler


    private val viewModel: VideoChatViewModel by viewModels {
        VideoChatViewModelFactory(
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
        val view = inflater.inflate(R.layout.video_chat_fragment,container,false)
        getAllViews(view)

        checkPermissions()
        handler = Handler(Looper.getMainLooper())
        requireActivity().window.addFlags(Window.FEATURE_NO_TITLE)

        val callSb = arguments?.getString("makeCall")

        val receiveCall = arguments?.getString("calling")

        requireActivity().window.addFlags(Window.FEATURE_NO_TITLE)

        val option = PeerOption()
        option.key = apiKey
        option.domain = domain
        option.debug = Peer.DebugLevelEnum.ALL_LOGS

        peer = Peer(requireContext(),option)

        peer?.on(Peer.PeerEventEnum.OPEN) { p0 ->
            ownID  = p0.toString()
            checkPermissions()
            Log.d("1", "onCallback: $ownID")

            if(callSb!=null){
                ownID?.let {
                    Log.d("1", "MAM W VIDEOCHATFRAGMENT $callSb AND $it ")
                    viewModel.notifyUser("VIDEO-$callSb",it)
                }
            }

            if(receiveCall!=null){
                ownID?.let {
                    Log.d("1", "MAM KOŃCÓWA $receiveCall AND $it ")
                    lifecycleScope.launch {
                        delay(1000)
                        handler.post {
                            it.run {
                                onPeerSelected(receiveCall)
                            }
                        }
                    }
                }
            }

        }

        peer?.on(Peer.PeerEventEnum.ERROR){p0->
            val error: PeerError = p0 as PeerError
            Log.d("1", "ERROR onError: $error ")
        }

        peer?.on(Peer.PeerEventEnum.CLOSE){
            Log.d("1", "ERROR onClose ")
        }

        peer?.on(Peer.PeerEventEnum.DISCONNECTED){
            Log.d("1", "ERROR onDisconnected ")
        }

        peer?.on(Peer.PeerEventEnum.CALL){
            if(it is MediaConnection){

                mediaConnection = it
                setMediaCallbacks()
                mediaConnection?.answer(localStream)
                isConnected = true
            }
        }

        /*
        btnAction.isEnabled = true
        btnAction.setOnClickListener {
            it.isEnabled = false
            if(!isConnected){
                //showPeerIDs()
            }else{
                closeRemoteStream()
                mediaConnection?.close()
            }
            it.isEnabled = true
        }

         */

        switchCameraAction.setOnClickListener {

            localStream?.let {
                localStream?.let {
                    it.switchCamera()
                }
            }
        }

        return view
    }



    private fun getAllViews(view:View){
        canvas = view.findViewById(R.id.canvas)
        canvas2 = view.findViewById(R.id.canvas2)
        switchCameraAction = view.findViewById(R.id.switchCameraAction)
    }



    private fun startLocalStream(){

        val constraints = MediaConstraints()
        constraints.maxWidth = 960
        constraints.maxHeight = 540
        constraints.cameraPosition  = MediaConstraints.CameraPositionEnum.FRONT

        Navigator.initialize(peer)
        localStream = Navigator.getUserMedia(constraints)

        val canvasView  = canvas as Canvas

        localStream?.addVideoRenderer(canvasView,0)

    }


    private fun showPeerIDs(it:String){

        if(peer==null || ownID.isNullOrEmpty()){
            Toast.makeText(requireContext(), "Peer Id zerowe ", Toast.LENGTH_SHORT).show()
            return
        }

        peer?.listAllPeers(){result->

            if(result is JSONArray) {

                var peerId: String
                val listPeerIds = ArrayList<String>()

                for (i in 0..result.length()) {

                    try {
                        peerId = result.getString(i)
                        if (!ownID.equals(peerId)) {
                            listPeerIds.add(peerId)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                onPeerSelected(it)
            }

        }
    }


    private fun onPeerSelected(peerID:String){

        if(peer!=null){

            if(mediaConnection!=null){
                mediaConnection?.close()
            }

            val option = CallOption()
            mediaConnection = peer?.call(peerID,localStream,option)

            mediaConnection?.let {
                setMediaCallbacks()
                isConnected = true
            }
        }
    }


    private fun setMediaCallbacks(){

        mediaConnection?.on(MediaConnection.MediaEventEnum.STREAM){
            remoteStream = it as MediaStream
            val canvasView = canvas2 as Canvas
            remoteStream?.addVideoRenderer(canvasView,0)
        }

        mediaConnection?.on(MediaConnection.MediaEventEnum.CLOSE){
            isConnected = false
            closeRemoteStream()

        }

        mediaConnection?.on(MediaConnection.MediaEventEnum.ERROR){
            val error = it as PeerError
            Log.d("1", "ERROR $error")
        }
    }

    private fun closeRemoteStream(){

        remoteStream?.let {
            val canvasView = canvas2 as Canvas
            remoteStream?.removeVideoRenderer(canvasView,0)
            remoteStream?.close()
        }
    }

    private fun destroyPeer(){
        closeRemoteStream()

        localStream?.let {
            val canvasView = canvas as Canvas
            it.removeVideoRenderer(canvasView,0)
            it.close()
        }

        mediaConnection?.let {
            if(it.isOpen){
                it.close()
            }
            unsetMediaCallbacks()
        }

        Navigator.terminate()
        peer?.let {
            unsetPeerCallback(it)
            if(!it.isDestroyed){
                it.destroy()
            }
            if(!it.isDisconnected){
                it.disconnect()
            }
            peer = null
        }
    }


    private fun unsetPeerCallback(peer:Peer?){
        peer?.let {
            it.on(Peer.PeerEventEnum.OPEN, null)
            it.on(Peer.PeerEventEnum.CONNECTION, null)
            it.on(Peer.PeerEventEnum.CALL, null)
            it.on(Peer.PeerEventEnum.CLOSE, null)
            it.on(Peer.PeerEventEnum.DISCONNECTED, null)
            it.on(Peer.PeerEventEnum.ERROR, null)
        }
    }

    private fun unsetMediaCallbacks(){

        mediaConnection?.let{
            it.on(MediaConnection.MediaEventEnum.STREAM,null)
            it.on(MediaConnection.MediaEventEnum.CLOSE,null)
            it.on(MediaConnection.MediaEventEnum.ERROR,null)
        }
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), 0
            )
        } else {
            startLocalStream()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocalStream()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to access the camera and microphone.\nclick allow when asked for permission.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().volumeControlStream = AudioManager.STREAM_VOICE_CALL
    }

    override fun onPause() {
        super.onPause()
        requireActivity().volumeControlStream = AudioManager.USE_DEFAULT_STREAM_TYPE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPeer()
    }

}
