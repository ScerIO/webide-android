package io.scer.ide.api

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

class ApiApolloClient {

    companion object {
        private var apolloClient: ApolloClient? = null
        fun newInstance(): ApolloClient {
            if (apolloClient !== null) return apolloClient!!
            apolloClient = ApolloClient.builder()
                    .serverUrl("http://192.168.1.60/api")
                    .okHttpClient(OkHttpClient.Builder().build())
                    .build()
            return apolloClient!!
        }
    }
}
