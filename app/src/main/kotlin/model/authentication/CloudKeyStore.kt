/*
 * Copyright © 7/19/2022, Pexers (https://github.com/Pexers)
 */

package model.authentication

import model.Utils.RESOURCES
import model.Utils.readResFile
import model.Utils.readResFileAsStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.spec.SecretKeySpec

object CloudKeyStore {

    private val keyStoreStream = readResFileAsStream("keystore/key.store")
    private val keyPwdContent = readResFile("keystore/key.password").toCharArray()
    private val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())

    fun loadKeyStore() = keyStore.load(keyStoreStream, keyPwdContent)

    fun getEntry(entryKey: String): String {
        val entryValue = keyStore.getEntry(entryKey, KeyStore.PasswordProtection(keyPwdContent))
        return String((entryValue as KeyStore.SecretKeyEntry).secretKey.encoded)
    }

    fun setEntry(entryKey: String, entryValue: String) {
        val encoded = SecretKeySpec(entryValue.toByteArray(), "AES")
        keyStore.setEntry(entryKey, KeyStore.SecretKeyEntry(encoded), KeyStore.PasswordProtection(keyPwdContent))
        val stream = FileOutputStream("$RESOURCES/keystore/key.store")
        stream.use { keyStore.store(it, keyPwdContent) }
    }

}