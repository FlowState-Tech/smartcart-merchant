package com.smartcart_merchant.features.store.domain.usecase

import com.smartcart_merchant.core.common.Resource
import com.smartcart_merchant.features.store.domain.model.Store
import com.smartcart_merchant.features.store.domain.repository.StoreRepository
import javax.inject.Inject

class CreateStoreUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(store: Store): Resource<Store> {
        return repository.createStore(store)
    }
}