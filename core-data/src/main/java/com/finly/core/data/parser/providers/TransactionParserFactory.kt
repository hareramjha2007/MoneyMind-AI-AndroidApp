package com.finly.core.data.parser.providers

class TransactionParserFactory {

    private val providerParsers: List<ProviderParser> = listOf(
        AxioParser(),
        PhonePeParser(),
        GooglePayParser(),
        PaytmParser(),
        CredParser(),
        HdfcParser(),
        SbiParser(),
        IciciParser(),
        AxisParser(),
        KotakParser()
    )

    private val fallbackParser = UniversalFallbackParser()

    fun getParser(packageName: String, senderId: String, text: String): ProviderParser {
        return providerParsers.firstOrNull { it.canParse(packageName, senderId, text) }
            ?: fallbackParser
    }
}
