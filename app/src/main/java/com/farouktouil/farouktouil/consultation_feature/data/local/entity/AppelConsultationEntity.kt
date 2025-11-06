package com.farouktouil.farouktouil.consultation_feature.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.farouktouil.farouktouil.consultation_feature.domain.model.AppelConsultation

@Entity(
    tableName = "appel_consultation",
    indices = [
        Index(value = ["title"]),
        Index(value = ["depositDate"]),
        Index(value = ["id"], unique = true)
    ]
)
data class AppelConsultationEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val depositDate: String,
    val dayOfWeek: String,
    val tenderNumber: Int,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
        
        fun fromDomain(domain: AppelConsultation): AppelConsultationEntity {
            return AppelConsultationEntity(
                id = domain.id,
                title = domain.title,
                depositDate = domain.depositDate,
                dayOfWeek = domain.dayOfWeek,
                tenderNumber = domain.tenderNumber
            )
        }
    }
    
    fun toDomain(documents: List<AppelConsultation.Document> = emptyList()): AppelConsultation {
        return AppelConsultation(
            id = id,
            title = title,
            depositDate = depositDate,
            dayOfWeek = dayOfWeek,
            tenderNumber = tenderNumber,
            documents = documents
        )
    }
    
    fun isExpired(expirationTimeInMillis: Long): Boolean {
        return (System.currentTimeMillis() - lastUpdated) > expirationTimeInMillis
    }
}
