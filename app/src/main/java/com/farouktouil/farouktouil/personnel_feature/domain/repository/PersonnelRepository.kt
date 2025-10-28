package com.farouktouil.farouktouil.personnel_feature.domain.repository

import androidx.paging.PagingData
import com.farouktouil.farouktouil.personnel_feature.domain.model.Personnel
import com.farouktouil.farouktouil.personnel_feature.domain.model.PersonnelSearchQuery
import kotlinx.coroutines.flow.Flow

interface PersonnelRepository {

    fun getPersonnel(searchQuery: PersonnelSearchQuery): Flow<PagingData<Personnel>>
}
