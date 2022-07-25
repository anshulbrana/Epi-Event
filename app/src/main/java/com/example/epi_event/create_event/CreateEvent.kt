package com.example.epi_event.create_event

data class CreateEvent(
    val eventName: String? = null,
    val eventOrganiser: String? = null,
    val eventDate: String? = null,
    val eventTime: String? = null,
    val eventDescription: String? = null,
    val eventLocation: String? = null,
    val eventType: String? = null,
    val eventPreRegister: String? = null,
)
