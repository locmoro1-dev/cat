package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthUserState
import com.example.data.model.BookingEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.IndustrialAd
import com.example.data.model.LaborProfileEntity
import com.example.data.model.MachineCategory
import com.example.data.model.MachineEntity
import com.example.data.model.MoroccanCities
import com.example.data.model.UserRole
import com.example.data.repository.MarketplaceRepository
import com.example.data.sync.SyncState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MarketplaceViewModel(
    private val repository: MarketplaceRepository
) : ViewModel() {

    // Active User Role
    private val _currentRole = MutableStateFlow(UserRole.RENTER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Navigation & Feed Tabs
    // 0: Machines, 1: Labor/Operators, 2: Bookings, 3: Architecture Blueprint & SQL
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Moroccan Regional & Category Filters
    private val _selectedCity = MutableStateFlow(MoroccanCities.ALL)
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedCategory = MutableStateFlow(MachineCategory.ALL)
    val selectedCategory: StateFlow<MachineCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _laborRoleFilter = MutableStateFlow("all")
    val laborRoleFilter: StateFlow<String> = _laborRoleFilter.asStateFlow()

    // Dialog & Flow States
    private val _showAddMachineDialog = MutableStateFlow(false)
    val showAddMachineDialog: StateFlow<Boolean> = _showAddMachineDialog.asStateFlow()

    private val _selectedMachineForBooking = MutableStateFlow<MachineEntity?>(null)
    val selectedMachineForBooking: StateFlow<MachineEntity?> = _selectedMachineForBooking.asStateFlow()

    private val _selectedTargetForChat = MutableStateFlow<Any?>(null)
    val selectedTargetForChat: StateFlow<Any?> = _selectedTargetForChat.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // User Account & Authentication State (Firebase Auth + Credential Manager)
    val authState: StateFlow<AuthUserState> = repository.authState ?: MutableStateFlow(AuthUserState()).asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    fun openAuthDialog() {
        _showAuthDialog.value = true
    }

    fun closeAuthDialog() {
        _showAuthDialog.value = false
    }

    // Room <-> Firestore Synchronization State
    val syncState: StateFlow<SyncState> = repository.syncState ?: MutableStateFlow(SyncState()).asStateFlow()

    fun triggerSync() {
        viewModelScope.launch {
            _snackbarMessage.value = "جاري الاتصال بـ Firestore ومزامنة الآليات... 🔄"
            repository.triggerSync()
        }
    }

    // B2B Industrial Ads
    val industrialAds: List<IndustrialAd> = repository.getIndustrialAds()

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
        // Synchronize active role whenever auth state changes
        viewModelScope.launch {
            repository.authState?.collect { auth ->
                _currentRole.value = auth.activeRole
            }
        }
    }

    // Reactive Filtered Machines (Broadcasting in Real Time)
    val machines: StateFlow<List<MachineEntity>> = combine(
        repository.allMachines,
        _selectedCity,
        _selectedCategory,
        _searchQuery
    ) { allMachines, city, category, query ->
        allMachines.filter { machine ->
            val matchesCity = city == MoroccanCities.ALL || machine.city.equals(city, ignoreCase = true)
            val matchesCategory = category == MachineCategory.ALL || machine.category.equals(category.id, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    machine.title.contains(query, ignoreCase = true) ||
                    machine.description.contains(query, ignoreCase = true) ||
                    machine.location.contains(query, ignoreCase = true) ||
                    machine.city.contains(query, ignoreCase = true)
            matchesCity && matchesCategory && matchesQuery
        }.sortedWith(
            compareByDescending<MachineEntity> { it.isFeatured }
                .thenByDescending { it.createdAt }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Reactive Filtered Labor Profiles (Operators & Mechanics)
    val laborProfiles: StateFlow<List<LaborProfileEntity>> = combine(
        repository.allLabor,
        _selectedCity,
        _laborRoleFilter,
        _searchQuery
    ) { allLabor, city, role, query ->
        allLabor.filter { profile ->
            val matchesCity = city == MoroccanCities.ALL || profile.city.equals(city, ignoreCase = true)
            val matchesRole = role == "all" || profile.role.equals(role, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    profile.fullName.contains(query, ignoreCase = true) ||
                    profile.skills.contains(query, ignoreCase = true) ||
                    profile.city.contains(query, ignoreCase = true)
            matchesCity && matchesRole && matchesQuery
        }.sortedByDescending { it.rating }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Reactive Bookings
    val bookings: StateFlow<List<BookingEntity>> = repository.allBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Reactive Messages
    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // User Actions
    fun setRole(role: UserRole) {
        _currentRole.value = role
        _snackbarMessage.value = "تم التبديل إلى: ${role.darijaLabel} (${role.arabicName})"
    }

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setCity(city: String) {
        _selectedCity.value = city
    }

    fun setCategory(category: MachineCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLaborRoleFilter(role: String) {
        _laborRoleFilter.value = role
    }

    fun openAddMachineDialog() {
        _showAddMachineDialog.value = true
    }

    fun closeAddMachineDialog() {
        _showAddMachineDialog.value = false
    }

    fun openBookingDialog(machine: MachineEntity) {
        _selectedMachineForBooking.value = machine
    }

    fun closeBookingDialog() {
        _selectedMachineForBooking.value = null
    }

    fun openChatDialog(target: Any) {
        _selectedTargetForChat.value = target
    }

    fun closeChatDialog() {
        _selectedTargetForChat.value = null
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Add Machine (Instant broadcasting to Room Flow)
    fun addMachine(
        title: String,
        category: String,
        dailyRate: Double,
        city: String,
        location: String,
        description: String,
        brandModel: String,
        isFeatured: Boolean
    ) {
        viewModelScope.launch {
            val newMachine = MachineEntity(
                ownerId = "owner_current",
                ownerName = if (_currentRole.value == UserRole.OWNER) "أنا (مول الماتريال)" else "مقاولة الأشغال السريعة",
                ownerPhone = "+212661009988",
                title = title.ifBlank { "آلة حفر وشوانط" },
                category = category,
                dailyRate = if (dailyRate > 0) dailyRate else 2000.0,
                location = location.ifBlank { "المنطقة الصناعية" },
                city = city,
                description = description.ifBlank { "ماتريال بحالة ممتازة متاح للكراء المباشر مع شيفور" },
                status = "available",
                isFeatured = isFeatured,
                brandModel = brandModel.ifBlank { "Caterpillar CAT 320" },
                year = 2022,
                hasOperator = true
            )

            repository.addMachine(newMachine)
            _showAddMachineDialog.value = false
            _snackbarMessage.value = "تمت إضافة الماجورة ونشرها فورا فالسوق! 🚜"
        }
    }

    // Boost / Feature machine listing (Monetization simulation)
    fun toggleBoostMachine(machine: MachineEntity) {
        viewModelScope.launch {
            val newFeatured = !machine.isFeatured
            repository.promoteMachine(machine.id, newFeatured)
            _snackbarMessage.value = if (newFeatured) {
                "تمت ترقية الإعلان إلى 'إعلان مميز' فالمقدمة! ⭐"
            } else {
                "تم إلغاء تمييز الإعلان"
            }
        }
    }

    // Create Booking
    fun confirmBooking(
        machine: MachineEntity,
        renterName: String,
        renterPhone: String,
        startDate: String,
        endDate: String,
        days: Int
    ) {
        viewModelScope.launch {
            val calculatedDays = if (days > 0) days else 3
            val totalPrice = calculatedDays * machine.dailyRate
            val booking = BookingEntity(
                machineId = machine.id,
                machineTitle = machine.title,
                machineCategory = machine.category,
                renterId = "renter_current",
                renterName = renterName.ifBlank { "مقاول الشانطي" },
                renterPhone = renterPhone.ifBlank { "+212665001122" },
                ownerName = machine.ownerName,
                ownerPhone = machine.ownerPhone,
                city = machine.city,
                startDate = startDate.ifBlank { "2026-09-20" },
                endDate = endDate.ifBlank { "2026-09-25" },
                daysCount = calculatedDays,
                dailyRate = machine.dailyRate,
                totalPrice = totalPrice,
                status = "approved"
            )

            repository.addBooking(booking)
            _selectedMachineForBooking.value = null
            _snackbarMessage.value = "تم تأكيد طلب الكراء بنجاح! المبلغ الإجمالي: ${totalPrice.toInt()} درهم"
        }
    }

    // Send Message
    fun sendChatMessage(targetMachineId: Long?, text: String, recipientName: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val msg = ChatMessageEntity(
                machineId = targetMachineId,
                senderId = "me",
                senderName = "أنا (${_currentRole.value.darijaLabel})",
                senderRole = _currentRole.value.id,
                text = text,
                isFromCurrentUser = true
            )
            repository.sendMessage(msg)

            // Auto simulated response in Darija from the other party
            kotlinx.coroutines.delay(1200)
            val replyText = when {
                text.contains("ثمن", ignoreCase = true) || text.contains("سعر", ignoreCase = true) ->
                    "وعليكم السلام خويا. الثمن قابل للنقاش شوية إلا كانت المدة طويلة (أكثر من سيمانة). تواصل معايا فالواتساب دابا باش نتفاهمو."
                text.contains("شيفور", ignoreCase = true) ->
                    "نعم، كاين شيفور ناضي مع الماجورة عندو رخصة CACES وخبرة 10 سنين فالشوانط."
                else ->
                    "مرحبا بك أسيدي! الماتريال راه واجد وخدام مزيان. واش بغيتيه يبدا الخدمة فهاد السيمانة؟"
            }

            val reply = ChatMessageEntity(
                machineId = targetMachineId,
                senderId = "other",
                senderName = recipientName,
                senderRole = "owner",
                text = replyText,
                isFromCurrentUser = false
            )
            repository.sendMessage(reply)
        }
    }

    // Direct WhatsApp B2B Launcher
    fun launchWhatsApp(context: Context, phone: String, messageText: String) {
        try {
            // Clean phone number (e.g. +212661234567 -> 212661234567)
            val cleaned = phone.replace("+", "").replace(" ", "").replace("-", "")
            val url = "https://api.whatsapp.com/send?phone=$cleaned&text=${Uri.encode(messageText)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "لم نتمكن من فتح تطبيق واتساب: $phone", Toast.LENGTH_SHORT).show()
        }
    }

    // Direct GSM Phone Call Intent
    fun dialPhone(context: Context, phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phone")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر الاتصال بالرقم: $phone", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Authentication Actions (Firebase Auth & Credential Manager) ---

    fun signInWithGoogle(context: Context, role: UserRole, serverClientId: String? = null) {
        viewModelScope.launch {
            _snackbarMessage.value = "جاري الاتصال بـ Google Credential Manager..."
            val result = repository.signInWithGoogle(context, role, serverClientId)
            result?.onSuccess {
                _snackbarMessage.value = "مرحبا بك ${it.fullName}! تم تسجيل الدخول بحساب (${role.darijaLabel})"
                _showAuthDialog.value = false
            }?.onFailure {
                _snackbarMessage.value = it.localizedMessage ?: "فشل تسجيل الدخول عبر Google"
            }
        }
    }

    fun signInWithEmail(email: String, pass: String, role: UserRole) {
        if (email.isBlank() || pass.isBlank()) {
            _snackbarMessage.value = "يرجى ملء البريد الإلكتروني وكلمة المرور"
            return
        }
        viewModelScope.launch {
            _snackbarMessage.value = "جاري التحقق من الحساب..."
            val result = repository.signInWithEmail(email, pass, role)
            result?.onSuccess {
                _snackbarMessage.value = "تم تسجيل الدخول بنجاح! مرحبا ${it.fullName}"
                _showAuthDialog.value = false
            }?.onFailure {
                _snackbarMessage.value = it.localizedMessage ?: "خطأ في البريد أو كلمة المرور"
            }
        }
    }

    fun signUpWithEmail(
        email: String,
        pass: String,
        name: String,
        phone: String,
        city: String,
        role: UserRole,
        company: String?
    ) {
        if (email.isBlank() || pass.isBlank() || name.isBlank() || phone.isBlank()) {
            _snackbarMessage.value = "يرجى تعبئة جميع المعلومات المطلوبة"
            return
        }
        viewModelScope.launch {
            _snackbarMessage.value = "جاري إنشاء الحساب في Firebase..."
            val result = repository.signUpWithEmail(email, pass, name, phone, city, role, company)
            result?.onSuccess {
                _snackbarMessage.value = "تم تسجيل حساب جديد بنجاح! مرحبا ${it.fullName} (${role.darijaLabel})"
                _showAuthDialog.value = false
            }?.onFailure {
                _snackbarMessage.value = it.localizedMessage ?: "تعذر إنشاء الحساب"
            }
        }
    }

    fun quickSwitchUserRole(
        role: UserRole,
        name: String? = null,
        phone: String? = null,
        city: String? = null,
        email: String? = null,
        company: String? = null
    ) {
        viewModelScope.launch {
            val user = repository.quickSwitchRole(role, name, phone, city, email, company)
            _snackbarMessage.value = "تم التبديل الفوري إلى: ${role.arabicName} (${role.darijaLabel})"
            _showAuthDialog.value = false
        }
    }

    fun switchActiveRole(role: UserRole) {
        viewModelScope.launch {
            repository.switchActiveRole(role)
            _snackbarMessage.value = "تم تبديل صفتك النشطة إلى: ${role.darijaLabel}"
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _snackbarMessage.value = "تم تسجيل الخروج بنجاح"
            _showAuthDialog.value = false
        }
    }
}

class MarketplaceViewModelFactory(
    private val repository: MarketplaceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MarketplaceViewModel::class.java)) {
            return MarketplaceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
