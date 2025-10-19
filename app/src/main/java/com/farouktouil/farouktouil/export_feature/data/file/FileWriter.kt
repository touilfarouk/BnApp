package com.farouktouil.farouktouil.export_feature.data.file

import com.farouktouil.farouktouil.core.util.Resource


interface FileWriter {

    suspend fun writeFile(byteArray: ByteArray): Resource<String>

    companion object{
        const val FILE_NAME = "FileExportApp"
    }

}