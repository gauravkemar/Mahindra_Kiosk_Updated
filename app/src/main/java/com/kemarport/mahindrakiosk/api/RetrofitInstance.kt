package com.kemarport.mahindrakiosk.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import javax.net.ssl.SSLSocketFactory
class RetrofitInstance {
    companion object {

        fun create(baseUrl: String): Retrofit {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        fun api(baseUrl: String): KioskAPI {
            val retrofit = create(baseUrl)
            return retrofit.create(KioskAPI::class.java)
        }
        /*private fun createSSLSocketFactory(): SSLSocketFactory {
            try {
                // Load your custom certificate from the resources (e.g., res/raw)
                val certificateInputStream: InputStream = context.resources.openRawResource(R.raw.server_certificate)

                val cf = CertificateFactory.getInstance("X.509")
                val ca = cf.generateCertificate(certificateInputStream) as X509Certificate

                // Create a KeyStore containing the trusted certificate
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null, null)
                keyStore.setCertificateEntry("ca", ca)

                // Create a TrustManager that trusts the CAs in our KeyStore
                val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                tmf.init(keyStore)

                // Create an SSLContext that uses the TrustManager
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, tmf.trustManagers, null)

                return sslContext.socketFactory
            } catch (e: Exception) {
                throw RuntimeException("Error creating SSLSocketFactory", e)
            }
        }

        private fun createTrustManager(): X509TrustManager {
            // This TrustManager is used to perform additional checks on SSL certificates
            // You can customize it to match your SSL pinning requirements
            val trustManager = object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    // Implement client certificate validation logic if needed
                }

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    // Implement server certificate validation logic here for pinning
                    if (chain == null || chain.isEmpty()) {
                        throw IllegalArgumentException("Certificate chain is null or empty")
                    }

                    // Compare the server's certificate with the one you trust (e.g., the one loaded above)
                    val trustedCertificate: X509Certificate = *//* Load your trusted certificate here *//*

                        for (cert in chain) {
                            if (cert.equals(trustedCertificate)) {
                                return  // The certificate is trusted
                            }
                        }

                    // If the loop completes without finding a match, the certificate is not trusted
                    throw java.security.cert.CertificateException("Server certificate is not trusted")
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf() // You can return an empty array or customize it if needed
                }
            }

            return trustManager
        }*/
    }

  /*  companion object {
        private var baseUrl = ""
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .build()
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        *//*val api by lazy {
            retrofit.create(DtmsEtmsAPI::class.java)
        }*//*
        fun api(baseUrl: String): KioskAPI {
            this.baseUrl = baseUrl
            return retrofit.create(KioskAPI::class.java)
        }
    }*/
}