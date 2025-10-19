package com.farouktouil.farouktouil.contact_feature.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farouktouil.farouktouil.contact_feature.domain.model.ContactMessage
import com.farouktouil.farouktouil.contact_feature.domain.use_case.GetContactInfoUseCase
import com.farouktouil.farouktouil.contact_feature.domain.use_case.SubmitContactMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val submitContactMessageUseCase: SubmitContactMessageUseCase,
    private val getContactInfoUseCase: GetContactInfoUseCase
) : ViewModel() {

    private val _state = mutableStateOf(ContactState())
    val state: State<ContactState> = _state

    init {
        loadContactInfo()
    }

    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.NameChanged -> {
                _state.value = _state.value.copy(
                    name = event.name,
                    nameError = null
                )
            }
            is ContactEvent.EmailChanged -> {
                _state.value = _state.value.copy(
                    email = event.email,
                    emailError = null
                )
            }
            is ContactEvent.PhoneChanged -> {
                _state.value = _state.value.copy(
                    phone = event.phone,
                    phoneError = null
                )
            }
            is ContactEvent.SubjectChanged -> {
                _state.value = _state.value.copy(
                    subject = event.subject,
                    subjectError = null
                )
            }
            is ContactEvent.MessageChanged -> {
                _state.value = _state.value.copy(
                    message = event.message,
                    messageError = null
                )
            }
            is ContactEvent.SubmitMessage -> {
                submitMessage()
            }
        }
    }

    private fun submitMessage() {
        val currentState = _state.value

        // Validate fields
        val nameError = if (currentState.name.isBlank()) "Name is required" else null
        val emailError = if (currentState.email.isBlank()) "Email is required" else null
        val messageError = if (currentState.message.isBlank()) "Message is required" else null

        if (nameError != null || emailError != null || messageError != null) {
            _state.value = currentState.copy(
                nameError = nameError,
                emailError = emailError,
                messageError = messageError
            )
            return
        }

        _state.value = currentState.copy(isLoading = true)

        viewModelScope.launch {
            val contactMessage = ContactMessage(
                name = currentState.name,
                email = currentState.email,
                phone = currentState.phone,
                subject = currentState.subject,
                message = currentState.message
            )

            submitContactMessageUseCase(contactMessage)
                .onSuccess {
                    _state.value = ContactState(
                        isSuccess = true,
                        successMessage = "Message sent successfully!"
                    )
                }
                .onFailure { error ->
                    _state.value = currentState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to send message"
                    )
                }
        }
    }

    private fun loadContactInfo() {
        viewModelScope.launch {
            getContactInfoUseCase().let { contactInfo ->
                _state.value = _state.value.copy(contactInfo = contactInfo)
            }
        }
    }
}

data class ContactState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val subject: String = "",
    val message: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val subjectError: String? = null,
    val messageError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val contactInfo: com.farouktouil.farouktouil.contact_feature.domain.repository.ContactInfo = com.farouktouil.farouktouil.contact_feature.domain.repository.ContactInfo()
)

sealed class ContactEvent {
    data class NameChanged(val name: String) : ContactEvent()
    data class EmailChanged(val email: String) : ContactEvent()
    data class PhoneChanged(val phone: String) : ContactEvent()
    data class SubjectChanged(val subject: String) : ContactEvent()
    data class MessageChanged(val message: String) : ContactEvent()
    object SubmitMessage : ContactEvent()
}
