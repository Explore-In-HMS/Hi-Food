/* Copyright 2020. Explore in HMS. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0

 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hms.referenceapp.hifood.utils

import android.text.TextUtils
import android.util.Log
import java.io.UnsupportedEncodingException
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec


object CipherUtil {
    private const val TAG = "HMS_LOG_CipherUtil"
    private const val SIGN_ALGORITHMS = "SHA256WithRSA"

    fun doCheck(
        content: String,
        sign: String?,
        publicKey: String?
    ): Boolean {
        if (TextUtils.isEmpty(publicKey)) {
            Log.e(TAG, "publicKey is null")
            return false
        }
        try {
            val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
            val encodedKey: ByteArray =
                android.util.Base64.decode(sign, android.util.Base64.DEFAULT)
            val pubKey: PublicKey = keyFactory.generatePublic(X509EncodedKeySpec(encodedKey))
            val signature =
                Signature.getInstance(SIGN_ALGORITHMS)
            signature.initVerify(pubKey)
            signature.update(content.toByteArray(charset("utf-8")))
            return signature.verify(android.util.Base64.decode(sign, android.util.Base64.DEFAULT))
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "doCheck NoSuchAlgorithmException$e")
        } catch (e: InvalidKeySpecException) {
            Log.e(TAG, "doCheck InvalidKeySpecException$e")
        } catch (e: InvalidKeyException) {
            Log.e(TAG, "doCheck InvalidKeyException$e")
        } catch (e: SignatureException) {
            Log.e(TAG, "doCheck SignatureException$e")
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "doCheck UnsupportedEncodingException$e")
        }
        return false
    }
}