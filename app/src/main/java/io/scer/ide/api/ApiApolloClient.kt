package io.scer.ide.api

import com.apollographql.apollo.ApolloClient
import io.scer.ui.BuildConfig
import okhttp3.OkHttpClient

class ApiApolloClient {

    companion object {
//        private val API_URL = if (!BuildConfig.DEBUG) "https://api.ide.scer.io/graphql" else "http://192.168.1.60:8086/graphql"
        private val API_URL = "https://api.ide.scer.io/graphql"

        private var apolloClient: ApolloClient? = null
        fun newInstance(): ApolloClient {
            if (apolloClient !== null) return apolloClient!!
            apolloClient = ApolloClient.builder()
                    .serverUrl(API_URL)
                    .okHttpClient(OkHttpClient.Builder().build())
                    .build()
            return apolloClient!!
        }
    }
}
