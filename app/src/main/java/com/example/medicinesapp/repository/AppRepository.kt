package com.example.medicinesapp.repository

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.work.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.medicinesapp.CurrentUser
import com.example.medicinesapp.UserLogin
import com.example.medicinesapp.data.*
import com.example.medicinesapp.db.NoInternetPillDao
import com.example.medicinesapp.db.PillDao
import com.example.medicinesapp.db.PillOrganizerDao
import com.example.medicinesapp.db.PillTimeDao
import com.example.medicinesapp.notYet.firestore.utils.LoadResult
import com.example.medicinesapp.notYet.firestore.utils.MainPartialChange
import com.example.medicinesapp.utill.My2Serializer
import com.example.medicinesapp.utill.MySerializer
import com.example.medicinesapp.utill.workManager.DailyAlarmSetWorker
import com.example.medicinesapp.utill.workManager.FirebaseWorker
import com.example.medicinesapp.utill.workManager.GetCurrentWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class AppRepository(private val pillDao: PillDao,
                    private val noInternetPillDao: NoInternetPillDao,
                    private val pillTimeDao: PillTimeDao ,
                    private val pillOrganizerDao: PillOrganizerDao,
                    val context: Context){


    private val fireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val users = fireStore.collection("users")



    private val dataStore: DataStore<CurrentUser> =
        context.createDataStore(
            fileName = "current_user.pb",
            serializer = MySerializer()
        )

    private val dataStore3: DataStore<UserLogin> =
        context.createDataStore(
            fileName = "current_user_login.pb",
            serializer = My2Serializer()
        )


    //-------

    fun readCurrentUser(): Flow<CurrentUser> {

        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(CurrentUser.getDefaultInstance())
                } else {
                    throw exception
                } }
    }

    private suspend fun updateCurrentUser(user: User) {

        dataStore.updateData {
            it.toBuilder()
                .setUid(user.uid)
                .setName(user.name)
                .setEmail(user.email)
                .setIsDoctor(user.isDoctor)
                .setLastActive((user.lastActive))
                .setIsOnline(user.isOnline).build()
        }
    }


    fun readUserLogin(): Flow<UserLogin> {

        return dataStore3.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(UserLogin.getDefaultInstance())
                } else {
                    throw exception
                } }
    }

    suspend fun updateUserLogin(mail:String,password:String,checked:Boolean) {

        dataStore3.updateData {
            it.toBuilder()
                .setMail(mail)
                .setPassword(password)
                .setIsChecked(checked)
                .build()
        }
    }

    //-------


    suspend fun checkCurrentUser(){
        var id = auth.uid?:return

        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }
        val userSnapshot = users.document(id).get().await()
        val user = userSnapshot.toObject(User::class.java)?:return
        updateCurrentUser(user)
    }

    fun startGetCurrentUserWorker(context: Context){

        val request = OneTimeWorkRequestBuilder<GetCurrentWorker>().
        setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).
            build()).addTag("GET").build()
        WorkManager.getInstance(context).beginUniqueWork("GET_WORK",
            ExistingWorkPolicy.REPLACE,request).enqueue()
    }

    suspend fun markAsOnline(){
        var id = auth.uid?:return

        //CHANGE IN FIREBASE LATER

        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }
        users.document(id)
            .update("online",true,"lastActive",0)
            .await()
    }


    suspend fun markAsOffline(){
        Log.d("1", "ABCD OUT4 ")
        var id = auth.uid?:return
        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }
        val currentTimestamp = System.currentTimeMillis()
        users.document(id)
            .update("online",false,"lastActive",currentTimestamp)
            .await()
    }

    //--------



    suspend fun addUserToFriendList(friend:FriendFirebase) {

        val id = auth.uid ?: return
        users.document(id).collection("friends").document(friend.id).set(friend).await()
        users.document(friend.id).collection("friends").document(id).set(FriendFirebase(id)).await()
    }

    fun getFriendsIDS(): Observable<FriendIDFirebase?> {

        var id = auth.uid!!

        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }

        return Observable.create<QuerySnapshot> { emitter ->
            fireStore
                .collection("users")
                .document(id)
                .collection("friends")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", error)
                    }else {
                        emitter.onNext(value!!)
                    } } }
            .flatMap { Observable.fromIterable(it.documents).subscribeOn(Schedulers.io()) }
            .map{ it.toObject(FriendIDFirebase::class.java)}
            .observeOn(AndroidSchedulers.mainThread())
    }


    suspend fun updateFriendSwitcher(friendID:String,switchOn:Boolean){
        var id = auth.uid?:return

        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }
        users.document(id)
            .collection("friends")
            .document(friendID)
            .update("see",switchOn)
            .await()
    }

    @SuppressLint("CheckResult")
    suspend fun getFriendWithAllowInfo(userID:String) = suspendCoroutine<UserBothChecked?> {cont->

        var id = auth.uid?:return@suspendCoroutine

        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }

        val path =
            fireStore
                .collection("users")
                .document(userID)

        val pathCheckAllow =
            fireStore
                .collection("users")
                .document(userID)
                .collection("friends")
                .document(id)

        fireStore.runTransaction { transaction ->

            val user = transaction.get(path).toObject(UserBothChecked::class.java)

            val allow = transaction.get(pathCheckAllow).toObject(FriendIDFirebase::class.java)

            var isAllow:Boolean = false

            allow?.let {
                isAllow = it.see
            }

            user?.let {
                user.isCheckedTheir = isAllow
            }
            cont.resume(user)
        }
    }

    fun getUserPrescription2(user:String): Observable<PillFirebase?> {

        return Observable.create<QuerySnapshot> { emitter ->
            fireStore.collection("users")
                .document(user)
                .collection("userPills")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", error)
                    } else {
                        emitter.onNext(value!!)
                    }
                } }
            .flatMap { Observable.fromIterable(it.documents).subscribeOn(Schedulers.io()) }
            .map { it.toObject(PillFirebase::class.java) }
            .doOnNext { Log.d("1", "NEW PILL $it ") }
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun getFriendsSearch(query:String): Observable<UserBothChecked?> {

        var id = auth.uid!!

        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }
        return Observable.create<QuerySnapshot> { emitter ->
            fireStore
                .collection("users")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThan("name", "{$query}z")
                //.orderBy("name")
                //.startAt(name.toUpperCase(Locale.ROOT))
                //.endAt(name.toLowerCase(Locale.ROOT)+"\\uf8ff")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", error)
                    }else {
                        emitter.onNext(value!!)
                    } } }
            .flatMap { Observable.fromIterable(it.documents).subscribeOn(Schedulers.io()) }
            .map{ it.toObject(UserBothChecked::class.java)}
            .observeOn(AndroidSchedulers.mainThread())
    }



    //---------------


    suspend fun notifyUser(app:Application,myTopic:String,text:String): Request<JSONObject> = withContext(Dispatchers.IO){

        val id = auth.uid

        val send = "$text&AND&$id"

        Log.d("1", "MAM CALLING $send AND $myTopic ")

        val myKey="AAAAFhLF6HE:APA91bGnQAgl8ag-BVzPAJnutR4ZwABPnIarx2LhCxYe8N6FOLmdxdeCs4vvKWLm5dg2JmSsIqHVomtSh6RmiGPqo1Z5fvfhvMfhLsyVqJ3B3ncFIaKv2E7mLQcnFbNJr0HceGv5u0m4"
        val fcmApi = "https://fcm.googleapis.com/fcm/send"
        val serverKey = "key=$myKey"
        val contentType = "application/json"

        val topic="/topics/$myTopic"
        val notification = JSONObject()
        val notificationBody = JSONObject()


        try {
            notificationBody.put("title", "Message")
            notificationBody.put("message", send)

            notification.put("to", topic)
            notification.put("data", notificationBody)
        } catch (e: JSONException) {
            Log.e("TAG", "JSON EXCEPTION" + e.message)
        }

        val queue = Volley.newRequestQueue(app)
        val jsonObjectRequest = object:
            JsonObjectRequest(fcmApi,notification, Response.Listener{
            }, Response.ErrorListener {
            }){

            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        queue.add(jsonObjectRequest)
    }


    fun subscribeToTopic(){
        val id = auth.uid?:return
        Log.d("1", "MAM START VIDEO-$id ")
        FirebaseMessaging.getInstance().subscribeToTopic("VIDEO-$id")
    }

    fun unsubscribeFromTopic(){
        val id = auth.uid?:return
        Log.d("1", "MAM END VIDEO-$id ")
        FirebaseMessaging.getInstance().unsubscribeFromTopic("VIDEO-$id")
    }

    suspend fun getUserByID(id:String): User? {
        val userSnapshot = users.document(id).get().await()
        return userSnapshot.toObject(User::class.java)
    }

    suspend fun getUserPhotoByID(id:String): Uri? {

        val ref = FirebaseStorage.getInstance().getReference("$id/")
        val result = ref.listAll().await()

        Log.d("1", "gohagoha ${result.items}")
        var uri:Uri?=null

        if(result.items.size>0){
            uri = result.items[0].downloadUrl.await()
        }
        return uri
    }

    suspend fun getUserPhotoByID2(id:String): Uri? {

       return FirebaseStorage.getInstance().getReferenceFromUrl(id).downloadUrl.await()
    }

    suspend fun getUserPhotoByID3(id:String):Uri? = suspendCoroutine {cont->

        FirebaseStorage
            .getInstance()
            .getReferenceFromUrl("gs://medicinesapp-d3c19.appspot.com/$id/jeden.jpg")
            .downloadUrl
            .addOnFailureListener {
                cont.resume(null)

            }.addOnSuccessListener {
                cont.resume(it)
            }
    }



    //-------
    suspend fun addUserToDatabase(user: User){
        val id = auth.uid?:return
        user.uid=id
        users
            .document(id)
            .set(user)
            .await()
    }


    fun enqueueDownload(context: Context){

        val data = Data.Builder().putInt("PUT",22).build()

        val request = OneTimeWorkRequestBuilder<FirebaseWorker>().
        setInputData(data).
        setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).
            build()).addTag("WM").build()
        WorkManager.getInstance(context).beginUniqueWork("WORK",
            ExistingWorkPolicy.REPLACE,request).enqueue()
    }


    //------


    suspend fun insertDatePill(pillTimeDB: PillTimeDB){
        pillTimeDao.insert(pillTimeDB)
    }

    fun getDailyPill(date:String):Observable<List<DailyPill>>{
        return pillTimeDao.getDailyPill(date)
    }

    fun getAllPillsByID(id:String):Observable<List<DailyPill>>{
        return pillTimeDao.getAllPillsByID(id)
    }


    //-------

    fun getPills(): Observable<List<PillDB>> {
        return pillDao.getPills()
    }

    fun getSelectedPills(ids:List<String>):Observable<List<PillDB>> {
        return pillDao.getSelectedPills(ids)
    }


    suspend fun insert(pillDB: PillDB){
        pillDao.insert(pillDB)
    }

    suspend fun updatePillDoseLeft(id:String,doseLeft:Int){
        pillDao.updatePillDoseLeft(id,doseLeft)
    }

    //-------


    suspend fun insertPillOrganizer(pillOrganizerDB: PillOrganizerDB){

        pillOrganizerDao.insertPillOrganizer(pillOrganizerDB)
    }


    fun getPillsToAllPillsFragment():Observable<List<AllPillsFragmentData>>{
        return pillOrganizerDao.getPillsToAllPillsFragment()
    }


    fun getAllLeftDoseByIDS(date:String,time:String): Single<List<DoseLeftData>> {
        return pillTimeDao.getAllLeftDoseByIDS(date,time)
    }

    suspend fun updatePillDoseLeftNow(id:String,doseLeftNow:Int){
        pillDao.updatePillDoseLeftNow(id,doseLeftNow)
    }

    suspend fun updateOrganizerPillDoseLeftNow(id:String,doseLeftNow:Int,doseAll:Int){
        pillOrganizerDao.updateOrganizerPillDoseLeftNow(id,doseLeftNow,doseAll)
    }


    fun checkIfNegativeDoseLeftNow(): Flow<List<PillOrganizerDB>>{
        return pillOrganizerDao.checkIfNegativeDoseLeftNow()
    }

    suspend fun updateOrganizerPillDoseLeftNowNegativeInOther(idPill:String,difference:Int){
        pillOrganizerDao.updateOrganizerPillDoseLeftNowNegativeInOther(idPill,difference)
    }

    suspend fun markAsUsed(id:Int){
        pillOrganizerDao.markAsUsed(id)
    }

    suspend fun markAsBoughtOrNot(id:Int,bought:Boolean){
        pillOrganizerDao.markAsBoughtOrNot(id,bought)
    }


    suspend fun getPillBetweenDates(startOut:String,endOut:String):List<PillWorkManager>{
        return pillTimeDao.getPillBetweenDates(startOut,endOut)
    }

    suspend fun updatePillDone(id:Int){
        return pillTimeDao.updatePillDone(id)
    }

    fun getPreviousPills(): Flow<List<PrevPill>>{
        return pillTimeDao.getPreviousPills()
    }

    fun getPillsOrganizer(): Observable<List<PillOrganizerDB>>{
        return pillOrganizerDao.getPillsOrganizer()
    }

    suspend fun getPillsToChart(pillId:String,date:String,time:String): List<PillChart> {
        return pillTimeDao.getPillsToChart(pillId,date,time)
    }

    suspend fun getClosestPill(date:String):PillWorkManager{
        return pillTimeDao.getClosestPill(date)
    }


    @SuppressLint("CheckResult")
    fun getMedicine(name:String,id:String): Observable<FromInternet>? {

        val url = "https://www.e-zikoapteka.pl/$name,n.html"

        var nameOfPill ="null"
        var fromPrev = false
        var connectedToPill = false
        if(id!="null"){
            nameOfPill = name
            fromPrev = true
            connectedToPill = true
        }

        return Observable
            .fromCallable{Jsoup.connect(url).get()}
            .subscribeOn(Schedulers.io())
            .flatMap {Observable.fromIterable(it.select("article.product-tile")).subscribeOn(Schedulers.io())}
            .observeOn(Schedulers.io())
            .map {
                val picture = it.select("div.image figure span img").attr("src")
                val pictureEnd = "https://www.e-zikoapteka.pl$picture"
                val body = it.select("div.info h3.product-name a").text()
                val price = it.select("footer.product-tile__footer div.price div.price-regular span").text()
                FromInternet(fromPrev,pictureEnd,body,price,null,0.0,1,connectedToPill,nameOfPill,id,false,true)}
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun addPrescriptionToUser(pill:PillFirebase,user:String): Observable<String> {

        var id = auth.uid!!

        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }

        return Observable.create<String> { emitter ->
            users
                .document(id)
                .collection("userPills")
                .document(user)
                .set(pill)
                .addOnSuccessListener { emitter.onNext("a") }
                .addOnFailureListener { emitter.onNext("b") } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun getUserPrescription(user:String?): Observable<PillFirebase?> {

        val user = "SLCUS8ov9eWuqtQfouEaa2TuItt2"

        return Observable.create<QuerySnapshot> { emitter ->
            fireStore.collection("users")
                .document(user)
                .collection("userPills")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", error)
                    } else {
                        emitter.onNext(value!!)
                    }
                } }
            .flatMap { Observable.fromIterable(it.documents).subscribeOn(Schedulers.io()) }
            .map { it.toObject(PillFirebase::class.java) }
            .doOnNext { Log.d("1", "NEW PILL $it ") }
            .observeOn(AndroidSchedulers.mainThread())
    }

    suspend fun getOneUserPrescription(user:String?,idOfPill:String): PillFirebase? {

        val user = "SLCUS8ov9eWuqtQfouEaa2TuItt2"

        val idOfPill = when(idOfPill){
            "bartek-1606400466807"-> "6soX1hVCHz7Pnsv0NNU7"
            "bartek-1606400506087"-> "B9LWPfYSsE8Ri39hURiU"
            "bartek-1606400548719" -> "Q3yOBVv2LGJDKOwti1PR"
            "bartek-1606400430521" -> "aHwa3g5irrKew0oE7Bvc"
            "bartek-1606400350424" -> "gXM1JhYXjoExMqajkaCr"
            else -> ""
        }

        return fireStore
            .collection("users")
            .document(user)
            .collection("userPills")
            .document(idOfPill)
            .get()
            .await()
            .toObject(PillFirebase::class.java)
    }


    suspend fun getCurrentUser(): User? {
        var id = auth.uid?:return null

        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }
        val userSnapshot = users.document(id).get().await()
        return userSnapshot.toObject(User::class.java)?:return null
    }

       private fun setDailyAlarmSetterWorker(app:Context){

        val request = PeriodicWorkRequestBuilder<DailyAlarmSetWorker>(15, TimeUnit.MINUTES)
            .addTag("WmPeriod").build()
        WorkManager.getInstance(app).enqueueUniquePeriodicWork("WmPeriod",ExistingPeriodicWorkPolicy.REPLACE,request)
    }

    private fun getStateOfWork(app:Context): WorkInfo.State {
        return try {
            if (WorkManager.getInstance(app).getWorkInfosForUniqueWork("WmPeriod").get().isNotEmpty()) {
                WorkManager.getInstance(app).getWorkInfosForUniqueWork("WmPeriod")
                    .get()[0].state
            } else {
                WorkInfo.State.CANCELLED
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        } catch (e: InterruptedException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        }
    }

     fun startServerWork(app:Context) {
        if (getStateOfWork(app) !== WorkInfo.State.ENQUEUED && getStateOfWork(app) !== WorkInfo.State.RUNNING) {
            setDailyAlarmSetterWorker(app)
            Log.d("startLocationUpdates", "HERE WE ARE server started")
        } else {
            Log.d("startLocationUpdates", "HERE WE ARE server already working")
        }

    }

    suspend fun deleteOnePillTime(id:Int){
        pillTimeDao.deleteOnePill(id)
    }

    suspend fun deleteOnePill(id:String){
        pillDao.deleteOnePill(id)
    }


    suspend fun deleteMyDocument(idDoc:String) {

        var id = auth.uid?:return

        if(id=="UlqT385yvlMH4aVPFdPigAYDg9I2"){
            id = "tu1vewS57OWGDhjoOJrv"
        }
        fireStore
            .collection("users")
            .document(id)
            .collection("userPills")
            .document(idDoc).delete()
            .await()
    }





    //---------- NOT YET DONE


    fun noGetPills(): Observable<List<WithoutInternetPillDB>> {
        return noInternetPillDao.noGetPills()
    }


    suspend fun noInsert(pillDB: WithoutInternetPillDB){
        noInternetPillDao.noInsert(pillDB)
    }

    suspend fun noDelete(id:Int){
        return noInternetPillDao.noDeleteOnePill(id)
    }

    fun getPillsOrganizerByIDS(ids:List<String>):Observable<List<PillOrganizerDB>>{

        return pillOrganizerDao.getPillsOrganizerByIDS(ids)
    }

    fun getAllUserRecipesFromDoctor ():Observable<PillDB>{

        return Observable
            .create<QuerySnapshot> { emitter ->
                users.get().addOnSuccessListener {
                    emitter.onNext(it)
                }.
                addOnFailureListener {
                    Log.d("1", "getUserRecipe outside: FAILED ")
                } }
            .flatMap { Observable.fromIterable(it).subscribeOn(Schedulers.io()) }
            .observeOn(Schedulers.io())
            .doOnNext {Log.d("1","First ${it.toObject(User::class.java)}") }
            .flatMap {outsider->
                Observable.create<QuerySnapshot>{insider->
                    users.document(outsider.id).collection("userPills")
                        .whereEqualTo("doctor","bartek").get()
                        .addOnSuccessListener {
                            insider.onNext(it)
                        }.addOnFailureListener { Log.d("1", "getUserRecipe inside: FAILED ") } }
                    .subscribeOn(Schedulers.io())}
            .observeOn(Schedulers.io())
            .flatMap { Observable.fromIterable(it).subscribeOn(Schedulers.io()) }
            .observeOn(Schedulers.io()).map { it.toObject(PillDB::class.java) }
            .doOnNext{Log.d("1","Second $it")}
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun getAllUserRecipes():Observable<Pill>{

       return Observable
            .create<QuerySnapshot> { emitter ->
                users.get().addOnSuccessListener {
                    emitter.onNext(it)
                }.
                addOnFailureListener {
                    Log.d("1", "getUserRecipe outside: FAILED ")
                } }
            .flatMap { Observable.fromIterable(it).subscribeOn(Schedulers.io()) }
            .observeOn(Schedulers.io())
            .doOnNext {Log.d("1","First ${it.toObject(User::class.java)}") }
            .flatMap {outsider->
                Observable.create<QuerySnapshot>{insider->
                    users.document(outsider.id).collection("userPills").get()
                        .addOnSuccessListener {
                        insider.onNext(it)
                    }.addOnFailureListener { Log.d("1", "getUserRecipe inside: FAILED ") } }
                    .subscribeOn(Schedulers.io())}
           .observeOn(Schedulers.io())
           .flatMap { Observable.fromIterable(it).subscribeOn(Schedulers.io()) }
           .observeOn(Schedulers.io()).map { it.toObject(Pill::class.java) }
           .doOnNext{Log.d("1","Second $it")}
           .observeOn(AndroidSchedulers.mainThread())
    }


    fun getReview(): Observable<Review> {
        return Observable.create<QuerySnapshot>{emitter ->
            fireStore.collectionGroup("reviews").get()
                .addOnSuccessListener {
                    emitter.onNext(it)
                }.addOnFailureListener {Log.d("1", " FAILED ") } }
            .flatMap { Observable.fromIterable(it).subscribeOn(Schedulers.io()) }
            .observeOn(Schedulers.io())
            .map { it.toObject(Review::class.java) }
            .doOnNext { Log.d("1", "getReview: $it ") }
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun getMyRecipes(): ObservableTransformer<MainPartialChange.Load,LoadResult> {

        Log.d("1", "getMyRecipes: ")

        return ObservableTransformer{action->
            action.flatMap {  Observable.create<QuerySnapshot>{ emitter ->
            fireStore.collection("users")
                .document("1DQ946iXTt4pt2BimFVB")
                .collection("userPills")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", error)
                    } else {
                        emitter.onNext(value!!)
                    } } }
                .flatMap { Observable.fromIterable(it.documents).subscribeOn(Schedulers.io()) }
                .map{ it.toObject(Pill::class.java) }
                .map { LoadResult.SuccessLoad1(it) }
                .cast(LoadResult::class.java)
                .doOnNext {Log.d("1", "NEW $it ")  }
                .observeOn(AndroidSchedulers.mainThread())
            }}
    }



    fun updateRecipe(): Observable<String>? {
        return Observable.create<String> { emitter ->
            fireStore.collection("users")
                .document("1DQ946iXTt4pt2BimFVB")
                .collection("userPills")
                .document("p9v9XvJ75iexL9N6QPP8").update("days", FieldValue.arrayUnion(5))
                .addOnSuccessListener{emitter.onNext("a") }
                .addOnFailureListener {emitter.onNext("b") } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun deleteRecipe(): Observable<String>? {
        return Observable.create<String>{emitter->
        fireStore.collection("users")
            .document("1DQ946iXTt4pt2BimFVB")
            .collection("userPills")
            .document("p9v9XvJ75iexL9N6QPP8").update("days",FieldValue.arrayRemove(5))
            .addOnSuccessListener {emitter.onNext("a")  }
            .addOnFailureListener {emitter.onNext("b") }}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun addWithId(): Observable<String>? {
        return Observable.create<String> {emitter->
        val pill = Pill("Ibuprofen", mutableListOf(22L,32L,42L),"Add after eating","abcde","Maciekk","bartekk")
        fireStore.collection("userPills")
            .document("abcd")
            .set(pill)
            .addOnSuccessListener {emitter.onNext("a")  }
            .addOnFailureListener {emitter.onNext("b")  }}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }




    // Doctor delete patient recipe

    fun deletePatientDocument(){

        val path = fireStore
            .collection("users")
            .document("1DQ946iXTt4pt2BimFVB")
            .collection("userPills")
            .document("S1l03p95u3PCHQjN8Eoc")

        fireStore.runTransaction { transaction ->

            val snapshot = transaction.get(path).data!!

            Log.d("1", "AAAAAA $snapshot ")

            transaction.delete(path)

            transaction.set( fireStore
                .collection("users")
                .document("1DQ946iXTt4pt2BimFVB")
                .collection("userRemoveLocallyLater")
                .document("one"),snapshot)
        }
    }


    fun getIds(): Observable<String>? {

        return Observable.create<QuerySnapshot> { emitter ->
            fireStore
                .collection("users")
                .document("1DQ946iXTt4pt2BimFVB")
                .collection("userRemoveLocallyLater")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", error)
                    }else {
                        emitter.onNext(value!!)
                    } } }
            .flatMap { Observable.fromIterable(it.documents).subscribeOn(Schedulers.io()) }
            .map{ it.id }
            .observeOn(AndroidSchedulers.mainThread())
    }


    fun checkDeletion( id:String){

        val path = fireStore
            .collection("users")
            .document("1DQ946iXTt4pt2BimFVB")
            .collection("userRemoveLocallyLater")

        fireStore.runTransaction {transaction ->
            val idPath = path.document(id)
            val snapshot = transaction.get(idPath).data!!
            Log.d("1", "AAAAAAAAAA $snapshot")
            transaction.delete(idPath)
        }
    }




}