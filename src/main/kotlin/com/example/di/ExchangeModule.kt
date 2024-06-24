package com.example.di

import com.example.data.impl.BybitExchangeRepositoryImpl
import com.example.data.impl.ExchangeServiceImpl
import com.example.domain.interfaces.ExchangeRepository
import com.example.domain.interfaces.ExchangeService
import org.koin.dsl.module

val exchangeModule = module {
    single<ExchangeRepository> { BybitExchangeRepositoryImpl(get(), get()) }
    single<ExchangeService> { ExchangeServiceImpl(get()) }
}