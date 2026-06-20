package com.smartcart_merchant.features.store.domain.repository

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.store.domain.model.Store

interface StoreRepository {
    suspend fun createStore(store: Store): Resource<Store>
    suspend fun getStore(storeId: Long): Resource<Store>
}