package com.example.medicinesapp.utill

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.example.medicinesapp.CurrentUser
import com.example.medicinesapp.UserLogin
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

class My2Serializer() : Serializer<UserLogin> {

    override fun readFrom(input: InputStream): UserLogin {
        try {
            return UserLogin.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }


    override fun writeTo(t: UserLogin, output: OutputStream) = t.writeTo(output)

}