package com.example.medicinesapp.utill

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.example.medicinesapp.CurrentUser
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

class MySerializer() : Serializer<CurrentUser> {

    override fun readFrom(input: InputStream): CurrentUser {
        try {
            return CurrentUser.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }


    override fun writeTo(t: CurrentUser, output: OutputStream) = t.writeTo(output)

}