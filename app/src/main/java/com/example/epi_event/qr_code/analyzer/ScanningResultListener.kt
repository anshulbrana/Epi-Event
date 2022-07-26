package com.example.epi_event.qr_code.analyzer

interface ScanningResultListener {
    fun onScanned(result: String)
}