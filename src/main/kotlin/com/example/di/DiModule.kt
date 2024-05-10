package com.example.di

import com.example.data.BybitServiceImpl
import com.example.data.objects.HttpClient
import com.example.domain.interfaces.BybitServiceInterface
import org.koin.dsl.module

val exchangeModule = module {
    single { HttpClient.client }
    single<BybitServiceInterface> { BybitServiceImpl(get()) }
}