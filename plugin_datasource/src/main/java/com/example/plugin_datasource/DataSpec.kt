package com.example.plugin_datasource

import android.net.Uri
import androidx.annotation.IntDef
import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 定义资源中的数据区域。
 */
class DataSpec(uri: Uri, position: Long, length: Long) {

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(HTTP_METHOD_GET, HTTP_METHOD_POST, HTTP_METHOD_HEAD)
    annotation class HttpMethod {}

    companion object {
        const val HTTP_METHOD_GET = 1

        /** HTTP POST method.  */
        const val HTTP_METHOD_POST = 2

        /** HTTP HEAD method.  */
        const val HTTP_METHOD_HEAD = 3
    }


    /** A [Uri] from which data belonging to the resource can be read.  */
    val uri: Uri? = null
    val uriPositionOffset: Long = 0

    @DataSpec.HttpMethod
    val httpMethod = 0


    val httpBody: ByteArray? = null


    val httpRequestHeaders: Map<String, String>? = null


    val absoluteStreamPosition: Long = 0

    val position: Long = 0

    val length: Long = 0

    val key: String? = null

    //    @DataSpec.Flags
    val flags = 0

    val customData: Any? = null


}