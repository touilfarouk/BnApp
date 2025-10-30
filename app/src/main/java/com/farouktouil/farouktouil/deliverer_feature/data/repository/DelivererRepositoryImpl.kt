package com.farouktouil.farouktouil.deliverer_feature.data.repository

import com.farouktouil.farouktouil.core.data.local.DelivererDao
import com.farouktouil.farouktouil.core.data.local.ProductDao
import com.farouktouil.farouktouil.core.domain.model.Deliverer
import com.farouktouil.farouktouil.core.domain.model.Product
import com.farouktouil.farouktouil.deliverer_feature.data.mapper.toDeliverer
import com.farouktouil.farouktouil.deliverer_feature.data.mapper.toDelivererEntity
import com.farouktouil.farouktouil.product_feature.data.mapper.toProductEntity
import com.farouktouil.farouktouil.deliverer_feature.domain.repository.DelivererRepository
import com.farouktouil.farouktouil.deliverer_feature.data.remote.DelivererApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DelivererRepositoryImpl @Inject constructor(
    private val delivererDao: DelivererDao,
    private val productDao: ProductDao,
    private val delivererApiService: DelivererApiService
): DelivererRepository {

    override suspend fun insertDeliverers(list: List<Deliverer>) {
        list.forEach { deliverer ->
            delivererDao.insertDeliverer(deliverer.toDelivererEntity())
            insertProducts(deliverer.products, deliverer.delivererId)
        }
    }

    override suspend fun insertProducts(list: List<Product>, delivererId:Int) {
        list.forEach { product ->
            productDao.insertProduct(product.toProductEntity(delivererId))
        }
    }

    override suspend fun insert(deliverer: Deliverer) {
        delivererDao.insertDeliverer(deliverer.toDelivererEntity())
    }

    override suspend fun update(deliverer: Deliverer) {
        delivererDao.updateDeliverer(deliverer.toDelivererEntity())
    }

    override suspend fun delete(deliverer: Deliverer) {
        delivererDao.deleteDeliverer(deliverer.toDelivererEntity())
    }

    override fun getAllDeliverers(): Flow<List<Deliverer>> {
        return delivererDao.getAllDeliverers()
            .map { delivererEntities ->
                delivererEntities.map { it.toDeliverer() }
            }
    }

    override suspend fun refreshDeliverers() {
        try {
            // Check if the database is empty
            val currentDeliverers = delivererDao.getAllDeliverers()
                .firstOrNull()
                ?.map { it.toDeliverer() }
            
            if (currentDeliverers.isNullOrEmpty()) {
                // Add default deliverers if the database is empty
                val defaultDeliverers = listOf(
                    Deliverer(name = "B.L Centre (Blida)"),
                    Deliverer(name = "B.L Est (Constantine)"),
                    Deliverer(name = "B.L Ouest (Oran)"),
                    Deliverer(name = "B.L Région Hauts Plateaux (Sétif)"),
                    Deliverer(name = "B.L Steppes (Djelfa)"),
                    Deliverer(name = "B.L Sud (Ouargla)"),
                    Deliverer(name = "Départ. Doc.et Banq.de Données"),
                    Deliverer(name = "Départ. S.I.G"),
                    Deliverer(name = "Départ.Finances.et Comptabilité"),
                    Deliverer(name = "Départ.Génie Logiciel"),
                    Deliverer(name = "Départ.Laboratoire"),
                    Deliverer(name = "Départ.Patrimoine.et Moy.Généraux"),
                    Deliverer(name = "Départ.Ress Humain.et Aff.Sociales"),
                    Deliverer(name = "Direction des Contrats et du Soutien"),
                    Deliverer(name = "Direction des Etudes Form.et Progr"),
                    Deliverer(name = "Direction Générale"),
                    Deliverer(name = "Direction.Admin.et des Finances"),
                    Deliverer(name = "S/Direc E.A.D.A"),
                    Deliverer(name = "S/Direc E.A.D.Rural"),
                    Deliverer(name = "S/Direc.Progr.et Moy.Téchnique"),
                    Deliverer(name = "S/Direction de la Formation")
                )
                
                // Insert default deliverers
                defaultDeliverers.forEach { deliverer ->
                    delivererDao.insertDeliverer(deliverer.toDelivererEntity())
                }
            } else {
                // Existing remote fetch logic
                val remoteDeliverers = delivererApiService.getDeliverers().deliverers
                val deliverers = remoteDeliverers.map { it.toDeliverer() }
                deliverers.forEach { deliverer ->
                    delivererDao.insertDeliverer(deliverer.toDelivererEntity())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // If there's an error with the API, we'll still have the default values
        }
    }

    override fun getAllDelivererIds(): Flow<List<Int>> {
        return delivererDao.getAllDelivererIds()
    }
}