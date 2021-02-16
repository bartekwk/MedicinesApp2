package com.example.medicinesapp.social

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinesapp.R
import com.example.medicinesapp.adapter.FirebasePillAdapter
import com.example.medicinesapp.adapter.FriendsAdapter
import com.example.medicinesapp.adapter.SpinnerAdapter
import com.example.medicinesapp.adapter.SpinnerAdapterUser
import com.example.medicinesapp.data.PillFirebase
import com.example.medicinesapp.data.PillFriendFirebase
import com.example.medicinesapp.data.UserBothChecked
import com.example.medicinesapp.databinding.FriendsFragmentBinding
import com.example.medicinesapp.utill.Helper
import com.example.medicinesapp.utill.listeners.RecyclerViewItemClickListener
import com.example.medicinesapp.utill.listeners.RecyclerViewItemSwipeListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*



class FriendsFragment:Fragment() {


    private lateinit var sdf:SimpleDateFormat
    private lateinit var newCalendar:Calendar
    private lateinit var stringDate:String
    private lateinit var stringDateYesterday:String
    private lateinit var stringDateTomorrow:String

    private var mapChecked = mutableMapOf<String,Boolean>()
    private var friendIDS = mutableListOf<String>()
    private lateinit var binding: FriendsFragmentBinding
    private lateinit var disposable: Disposable
    private lateinit var adapter: FriendsAdapter
    private val userList = mutableListOf<UserBothChecked>()
    private val viewModel: FriendsViewModel by viewModels { FriendsViewModelFactory(Helper.provideRepository(requireContext()), requireActivity().application) }

    private lateinit var spinnerAdapter: SpinnerAdapter
    private var listSpinnerItem = mutableListOf<String>()
    private var listSpinnerItem2 = mutableListOf<UserBothChecked>()
    private lateinit var spinnerAdapter2: SpinnerAdapterUser

    private val listOfItems = mutableListOf<PillFriendFirebase>()
    private lateinit var adapter2: FirebasePillAdapter

    private val checkedUser = mutableListOf<UserBothChecked>()

    private var currentChoseId = "null"
    private var currentChoseDay = "dzisiaj"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mapChecked.clear()
        userList.clear()
        listOfItems.clear()
        checkedUser.clear()
        listSpinnerItem.clear()
        listSpinnerItem2.clear()
        currentChoseId = "null"
        currentChoseDay = "dzisiaj"

        addData()


        sdf = SimpleDateFormat("yyyy-MM-dd")

        newCalendar = Calendar.getInstance()



        stringDate = sdf.format(newCalendar.time)
        newCalendar.add(Calendar.DATE,1)
        stringDateTomorrow = sdf.format(newCalendar.time)
        newCalendar.add(Calendar.DATE,-2)
        stringDateYesterday = sdf.format(newCalendar.time)


        binding = FriendsFragmentBinding.inflate(inflater,container,false)

        spinnerAdapter = SpinnerAdapter(requireContext(),listSpinnerItem)
        spinnerAdapter2 = SpinnerAdapterUser(requireContext(),listSpinnerItem2)

        binding.spinner.adapter = spinnerAdapter
        binding.spinner.setSelection(0)

        var firstTime1 = true
        binding.spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(!firstTime1) {
                    val item = listSpinnerItem[p2]
                    currentChoseDay = item
                    changeRecyclerViewDay(currentChoseDay,currentChoseId)
                }
                firstTime1=false
            }
        }

        binding.spinner2.adapter = spinnerAdapter2
        binding.spinner2.setSelection(0)

        var firstTime2 = true
        binding.spinner2.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(!firstTime2) {
                    val item = listSpinnerItem2[p2]
                    currentChoseId = item.uid!!
                    changeRecyclerViewDay(currentChoseDay,currentChoseId)
                }
                firstTime2 = false
            }
        }


        adapter = FriendsAdapter(object:RecyclerViewItemClickListener<UserBothChecked>{
            override fun onClickListener(item: UserBothChecked) {
                if(item.isOnline){
                    val bundle = bundleOf("makeCall" to item.uid )
                    findNavController().navigate(R.id.action_friendsFragment_to_videoChat,bundle)
                }
            }
        },object:RecyclerViewItemSwipeListener<UserBothChecked>{
            override fun onSwipeListener(item: UserBothChecked, boolean: Boolean) {
                viewModel.updateFriendSwitcher(item.uid!!,boolean)
            }
        },object :RecyclerViewItemClickListener<UserBothChecked>{
            override fun onClickListener(item: UserBothChecked) {
                val bundle = bundleOf("friendID" to item.uid)
                findNavController().navigate(R.id.action_friendsFragment_to_allPills,bundle)
            }
        })
        binding.recycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.recycler.adapter = adapter


        adapter2 = FirebasePillAdapter(listOfItems)
        binding.recycler2.adapter = adapter2


            disposable = viewModel
                .getFriendIDS()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { userInfo ->
                    if (userInfo != null) {
                        mapChecked[userInfo.id] = userInfo.see
                        friendIDS.add(userInfo.id)
                        viewModel.getFriendsWithAllowInfo(userInfo.id)
                    }
                }

            viewModel.userPair.observe(viewLifecycleOwner, Observer {
                val user = it
                val isChecked = mapChecked[it.uid]
                if (isChecked != null) {
                    user.isChecked = isChecked
                }
                val currentUserList = mutableListOf<UserBothChecked>()

                if (!userList.contains(user)) {

                    val ids = userList.map { myMap -> myMap.uid!! }
                    if (ids.contains((user.uid))) {
                        val id = ids.indexOf(user.uid)
                        userList[id] = user
                    } else {
                        userList.add(user)
                        addingPillsFromUsers(user,stringDate,null,true)
                        if(user.isCheckedTheir==true) {
                            listSpinnerItem2.add(user)
                        }
                    }
                    currentUserList.addAll(userList)
                    adapter.submitList(currentUserList)
                }
            })


        binding.floating.setOnClickListener {

            val bundle = bundleOf("friendsID" to friendIDS.toTypedArray())
            findNavController().navigate(R.id.action_friendsFragment_to_barcode_reader,bundle)
        }

        return binding.root
    }

    private fun addingPillsFromUsers(user:UserBothChecked,typeOfDay:String,twoDaysOption:String?,firstTime:Boolean){

        val additionalOption = twoDaysOption?:"null"

        if (user.isCheckedTheir == true) {
            Log.d("1", "$user")
            if(firstTime) {
                checkedUser.add(user)
            }
            disposable = viewModel.getUserPrescription(user.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { pillDB -> Observable.fromIterable(pillDB.listDays).subscribeOn(Schedulers.io())
                    .flatMap { date -> Observable.just(pillFirebaseToPillFriend(pillDB, date)).subscribeOn(Schedulers.io()) } }
                .filter { pill -> pill.temp!!.contains(typeOfDay) || pill.temp!!.contains(additionalOption) }
                .doOnNext { pill ->
                    val split = pill.temp!!.split("&AND&")
                    pill.day = split.first()
                    pill.hour = split.last()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pill ->
                    listOfItems.add(pill)
                    listOfItems.sortWith(compareBy( {it.day },{it.hour}))
                    adapter2.notifyDataSetChanged()
                    Log.d("1", "MAMY $listOfItems")
                }
        }
    }


    private fun changeRecyclerViewDay(choseDay:String,choseId:String){
        Log.d("1", "WITOJ $choseDay $choseId $checkedUser")

        when(choseDay){
            "dzisiaj" -> {changeRecyclerViewId(stringDate,null,choseId)}
            "wczoraj" -> {changeRecyclerViewId(stringDateYesterday,null,choseId)}
            "jutro" -> {changeRecyclerViewId(stringDateTomorrow,null,choseId)}
            "dziś i jutro" -> {changeRecyclerViewId(stringDate,stringDateTomorrow,choseId)}
        }

    }

    private fun changeRecyclerViewId(choseDay:String,additionalDay:String?,choseId:String){

        listOfItems.clear()

        if(choseId=="null"){
            lifecycleScope.launch {
                checkedUser.forEach {
                    lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            addingPillsFromUsers(it, choseDay, additionalDay,false)
                        }
                    }
                }
                }

        }else{
            val user = checkedUser.find { user->user.uid == choseId}
            user?.let {
                addingPillsFromUsers(it,choseDay,additionalDay,false)
            }
        }
    }


    private fun addData(){
        listSpinnerItem.add("dzisiaj")
        listSpinnerItem.add("wczoraj")
        listSpinnerItem.add("jutro")
        listSpinnerItem.add("dziś i jutro")

        listSpinnerItem2.add(UserBothChecked("null","wszyscy","null",false,0L,false,null,null,null))
    }



    override fun onDestroy() {
        super.onDestroy()
        if(!disposable.isDisposed)disposable.dispose()
    }

    override fun onPause() {
        super.onPause()
        if(!disposable.isDisposed)disposable.dispose()
    }

    companion object{

        fun pillFirebaseToPillFriend(pill:PillFirebase,temp:String): PillFriendFirebase {

            return PillFriendFirebase(pill.id,
                pill.name,
                pill.description,
                pill.type,
                pill.doseLeft,
                pill.dayStart,
                pill.dayEnd,
                pill.patient,
                pill.doctor,
                temp,
                null,
                null,
                pill.amount!!)
        }
    }

}