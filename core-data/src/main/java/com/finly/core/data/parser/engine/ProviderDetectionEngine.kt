package com.finly.core.data.parser.engine

import java.util.Locale

object ProviderDetectionEngine {

    fun detectProvider(packageName: String, senderId: String, text: String): String {
        val searchSpace = "$packageName $senderId $text".lowercase(Locale.ROOT)

        return when {
            searchSpace.contains("hdfc") || searchSpace.contains("hdfcbk") -> "HDFC"
            searchSpace.contains("icici") || searchSpace.contains("icicib") -> "ICICI"
            searchSpace.contains("sbi") || searchSpace.contains("sbin") || searchSpace.contains("state bank") -> "SBI"
            searchSpace.contains("axis") || searchSpace.contains("axisbk") -> "Axis"
            searchSpace.contains("kotak") || searchSpace.contains("kbank") -> "Kotak"
            searchSpace.contains("canara") || searchSpace.contains("cnrb") -> "Canara"
            searchSpace.contains("bob") || searchSpace.contains("baroda") -> "Bank of Baroda"
            searchSpace.contains("pnb") || searchSpace.contains("punjab national") -> "PNB"
            searchSpace.contains("idfc") || searchSpace.contains("idfcfirst") -> "IDFC"
            searchSpace.contains("indusind") || searchSpace.contains("indus") -> "IndusInd"
            searchSpace.contains("federal") || searchSpace.contains("fedbk") -> "Federal Bank"
            searchSpace.contains("yes bank") || searchSpace.contains("yesbk") -> "Yes Bank"
            searchSpace.contains("au bank") || searchSpace.contains("aubank") -> "AU Bank"
            searchSpace.contains("rbl") || searchSpace.contains("rblbk") -> "RBL"
            searchSpace.contains("phonepe") -> "PhonePe"
            searchSpace.contains("gpay") || searchSpace.contains("google pay") -> "Google Pay"
            searchSpace.contains("paytm") -> "Paytm"
            searchSpace.contains("cred") -> "Cred"
            searchSpace.contains("amazon pay") -> "Amazon Pay"
            searchSpace.contains("axio") || searchSpace.contains("walnut") -> "Axio"
            else -> "Bank"
        }
    }
}
