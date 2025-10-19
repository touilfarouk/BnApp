package com.farouktouil.farouktouil.export_feature.data.converter.csv

import com.farouktouil.farouktouil.core.util.Resource
import com.farouktouil.farouktouil.export_feature.data.converter.DataConverter
import com.farouktouil.farouktouil.export_feature.data.converter.GenerateInfo
import com.farouktouil.farouktouil.export_feature.domain.model.ExportModel
import com.farouktouil.farouktouil.order_feature.domain.model.BoughtProduct
import com.farouktouil.farouktouil.order_feature.domain.model.Order
import com.opencsv.CSVWriter
import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVWriter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.StringWriter
import java.io.Writer
import kotlin.math.exp
class DataConverterCSV : DataConverter {

    private fun getCSVWriter(writer: Writer): ICSVWriter {
        return CSVWriterBuilder(writer)
            .withSeparator(SEPARATOR)
            .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
            .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
            .withLineEnd(CSVWriter.DEFAULT_LINE_END)
            .build()
    }

    override fun convertSensorData(
        exportDataList: List<Order>
    ): Flow<Resource<GenerateInfo>> = flow {
        emit(Resource.Loading(GenerateInfo()))
        val writer = StringWriter()
        val csvWriter = getCSVWriter(writer)
        val valuesForOnePercent = (exportDataList.size / 100) + 1
        var alreadyConvertedValues = 0

        // Adjusted headers
        csvWriter.writeNext(HEADER_DATA)

        exportDataList.forEach { exportModel ->
            val productsString = exportModel.products.joinToString(separator = "|") { product ->
                "${product.name}:${product.label}:${product.amount}:${product.pricePerAmount}"
            }

            csvWriter.writeNext(
                arrayOf(
                    exportModel.date,
                    exportModel.delivererName,
                    exportModel.delivererTime,
                    productsString  // ✅ Now formatted properly
                )
            )

            alreadyConvertedValues += 1
            if (alreadyConvertedValues % valuesForOnePercent == 0) {
                emit(Resource.Loading(GenerateInfo(
                    progressPercentage = alreadyConvertedValues / valuesForOnePercent
                )))
            }
        }
        emit(Resource.Success(
            GenerateInfo(
                byteArray = String(writer.buffer).toByteArray(),
                progressPercentage = 100
            )
        ))
        csvWriter.close()
        writer.close()
    }

    companion object {
        const val SEPARATOR = ';'
        val HEADER_DATA = arrayOf("Date", "Deliverer Name", "Deliverer Time", "Products (Name:Label:Amount:Price)")
    }
}
