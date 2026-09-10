package com.example.data.repository

import android.content.Context
import com.example.data.auth.AuthUserState
import com.example.data.auth.FirebaseAuthManager
import com.example.data.local.MarketplaceDao
import com.example.data.model.BookingEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.IndustrialAd
import com.example.data.model.LaborProfileEntity
import com.example.data.model.MachineEntity
import com.example.data.model.MachineSyncStatus
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.sync.FirestoreMachineSyncManager
import com.example.data.sync.SyncState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class MarketplaceRepository(
    private val dao: MarketplaceDao,
    val syncManager: FirestoreMachineSyncManager? = null,
    val authManager: FirebaseAuthManager? = null
) {

    val allMachines: Flow<List<MachineEntity>> = dao.getAllMachinesFlow()
    val allLabor: Flow<List<LaborProfileEntity>> = dao.getAllLaborProfilesFlow()
    val allBookings: Flow<List<BookingEntity>> = dao.getAllBookingsFlow()
    val allMessages: Flow<List<ChatMessageEntity>> = dao.getAllMessagesFlow()
    val syncState: StateFlow<SyncState>? = syncManager?.syncState
    val authState: StateFlow<AuthUserState>? = authManager?.authState

    fun getFilteredMachines(city: String, category: String): Flow<List<MachineEntity>> {
        return dao.getMachinesFiltered(city, category)
    }

    fun getFilteredLabor(city: String, role: String): Flow<List<LaborProfileEntity>> {
        return dao.getLaborFiltered(city, role)
    }

    suspend fun triggerSync() {
        syncManager?.triggerFullSync()
    }

    suspend fun addMachine(machine: MachineEntity): Long = withContext(Dispatchers.IO) {
        val insertedId = dao.insertMachine(machine.copy(syncStatus = MachineSyncStatus.PENDING_UPLOAD.name))
        syncManager?.syncSingleMachine(insertedId)
        insertedId
    }

    suspend fun promoteMachine(machineId: Long, isFeatured: Boolean) = withContext(Dispatchers.IO) {
        dao.setMachineFeatured(machineId, isFeatured)
        dao.markMachinePending(machineId, MachineSyncStatus.PENDING_UPDATE.name)
        syncManager?.syncSingleMachine(machineId)
    }

    // --- Authentication Actions (Firebase Auth & Credential Manager) ---
    suspend fun signInWithGoogle(context: Context, role: UserRole, serverClientId: String? = null) =
        authManager?.signInWithGoogle(context, role, serverClientId)

    suspend fun signInWithEmail(email: String, pass: String, role: UserRole) =
        authManager?.signInWithEmail(email, pass, role)

    suspend fun signUpWithEmail(email: String, pass: String, name: String, phone: String, city: String, role: UserRole, company: String?) =
        authManager?.signUpWithEmail(email, pass, name, phone, city, role, company)

    suspend fun quickSwitchRole(role: UserRole, name: String? = null, phone: String? = null, city: String? = null, email: String? = null, company: String? = null) =
        authManager?.quickSwitchRole(role, name, phone, city, email, company)

    suspend fun switchActiveRole(role: UserRole) =
        authManager?.switchActiveRole(role)

    suspend fun signOut() =
        authManager?.signOut()

    suspend fun addBooking(booking: BookingEntity): Long = withContext(Dispatchers.IO) {
        dao.insertBooking(booking)
    }

    suspend fun updateBookingStatus(id: Long, status: String) = withContext(Dispatchers.IO) {
        dao.updateBookingStatus(id, status)
    }

    suspend fun sendMessage(message: ChatMessageEntity): Long = withContext(Dispatchers.IO) {
        dao.insertMessage(message)
    }

    suspend fun addLaborProfile(profile: LaborProfileEntity): Long = withContext(Dispatchers.IO) {
        dao.insertLaborProfile(profile)
    }

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        if (dao.getMachinesCount() == 0) {
            seedInitialData()
        }
    }

    private suspend fun seedInitialData() {
        val defaultUser = UserEntity(
            id = "user_morocco_demo",
            phone = "+212661234567",
            fullName = "الحاج ادريس التازي",
            city = "الدار البيضاء",
            role = "owner",
            companyName = "شركة أطلس للآليات والمقاولات"
        )
        dao.insertUser(defaultUser)

        val seedMachines = listOf(
            MachineEntity(
                ownerId = "owner_1",
                ownerName = "المقاولات المغربية الكبرى",
                ownerPhone = "+212661889900",
                title = "ماجورة حفر كوماتسو Komatsu PC210",
                category = "excavator",
                dailyRate = 2200.0,
                location = "المنطقة الصناعية بئر الرامي، طريق طنجة",
                city = "القنيطرة",
                description = "ماجورة بحالة ممتازة موديل 2022، مجهزة بذراع طويل ودلو حفر عميق 1.2 متر مكعب. كراء يومي أو أسبوعي مع شيفور محترف.",
                status = "available",
                isFeatured = true,
                brandModel = "Komatsu PC210-10M0",
                year = 2022,
                hasOperator = true
            ),
            MachineEntity(
                ownerId = "owner_2",
                ownerName = "شركة أطلس للآليات والعتاد",
                ownerPhone = "+212662334455",
                title = "بيلدوزر كاترپيلار CAT D6R للخدمة الشاقة",
                category = "bulldozer",
                dailyRate = 3500.0,
                location = "تيط مليل، قرب الطريق السيار المداري",
                city = "الدار البيضاء",
                description = "بيلدوزر ديزل قوي جدا لتسوية الأراضي وتكسير الصخور والأشغال الكبرى. متاح فورا مع عقد كراء رسمي وضمان التشغيل.",
                status = "available",
                isFeatured = true,
                brandModel = "Caterpillar D6R Series III",
                year = 2021,
                hasOperator = true
            ),
            MachineEntity(
                ownerId = "owner_3",
                ownerName = "رافعات البوغاز والشمال",
                ownerPhone = "+212663445566",
                title = "رافعة كروا تلسكوبية Liebherr 50T",
                category = "crane",
                dailyRate = 5800.0,
                location = "طريق الميناء المتوسطي",
                city = "طنجة",
                description = "رافعة هيدروليكية حمولة 50 طن مع بوم 40 متر. شهادة فحص معتمدة من مكتب المراقبة التقنية، سائق ذو خبرة عالية في الرفع الثقيل.",
                status = "available",
                isFeatured = true,
                brandModel = "Liebherr LTM 1050-3.1",
                year = 2020,
                hasOperator = true
            ),
            MachineEntity(
                ownerId = "owner_4",
                ownerName = "عتاد سوس ماسة",
                ownerPhone = "+212664556677",
                title = "تراكس مدولب كاترپيلار CAT 950H",
                category = "loader",
                dailyRate = 1800.0,
                location = "تيكوين، حي الصناعي الجديد",
                city = "أكادير",
                description = "تراكس سريع واقتصادي في استهلاك المازوت، دلو 3.3 متر مكعب لشحن الرمال والركام والأتربة، صيانة دورية لدى الوكيل المعتمد.",
                status = "available",
                isFeatured = false,
                brandModel = "Caterpillar 950H",
                year = 2019,
                hasOperator = true
            ),
            MachineEntity(
                ownerId = "owner_5",
                ownerName = "أشغال النخيل الحديثة",
                ownerPhone = "+212665667788",
                title = "ماجورة حفر كاترپيلار CAT 320D",
                category = "excavator",
                dailyRate = 2400.0,
                location = "طريق تاحناوت، كلم 5",
                city = "مراكش",
                description = "ماجورة متينة مع بريز-روش (Brise-roche) لتكسير الصخور وحفر الأساسات والمسابح. استجابة سريعة وتوصيل للموقع.",
                status = "available",
                isFeatured = false,
                brandModel = "Caterpillar 320D2",
                year = 2021,
                hasOperator = true
            ),
            MachineEntity(
                ownerId = "owner_6",
                ownerName = "نقل وتوريد المقالع الفاسية",
                ownerPhone = "+212666778899",
                title = "شاحنة شوانط 8x4 Mercedes Actros",
                category = "dump_truck",
                dailyRate = 1500.0,
                location = "طريق صفرو، المنطقة اللوجستية",
                city = "فاس",
                description = "كاميو بينة كبيرة 20 متر مكعب لنقل الأتربة والحصى والركام من وإلى الشانطي، سائق ملتزم بالمواقيت.",
                status = "available",
                isFeatured = false,
                brandModel = "Mercedes-Benz Actros 4140",
                year = 2022,
                hasOperator = true
            ),
            MachineEntity(
                ownerId = "owner_7",
                ownerName = "أشغال تامسنا والصخيرات",
                ownerPhone = "+212667889900",
                title = "كومباكتور مدحلة تراب Bomag BW213",
                category = "compactor",
                dailyRate = 1700.0,
                location = "تامسنا الجديدة، قرب المحول المداري",
                city = "الرباط",
                description = "كومباكتور 13 طن بذبذبات اهتزازية قوية لدمك التربة وطبقات الطرقات والمشاريع الكبرى. بحالة الوكالة.",
                status = "available",
                isFeatured = false,
                brandModel = "Bomag BW213 D-5",
                year = 2023,
                hasOperator = true
            )
        )
        dao.insertMachines(seedMachines)

        val seedLabor = listOf(
            LaborProfileEntity(
                userId = "operator_1",
                fullName = "سي محمد العلمي",
                role = "operator",
                phone = "+212661998877",
                city = "القنيطرة",
                skills = "سياقة الحفارات الثقيلة (ماجورات CAT و Komatsu)، حاصل على شهادة CACES، تسوية الأراضي بدقة عالية وحفر قنوات الصرف الصحي والأساسات العميقة.",
                experienceYears = 14,
                dailyRate = 350.0,
                status = "available",
                rating = 4.96f,
                completedJobs = 128,
                certifiedCaces = true,
                emergencyAvailable = false
            ),
            LaborProfileEntity(
                userId = "mechanic_1",
                fullName = "المعلم حسن بنجلون",
                role = "mechanic",
                phone = "+212662112233",
                city = "الدار البيضاء",
                skills = "ميكانيكي هيدروليك معتمد، تشخيص أعطال الديزل والحاسوب، استبدال ليات الضغط العالي والمضخات الهيدروليكية، خدمة ديباناج 24/7 لأوراش البناء.",
                experienceYears = 18,
                dailyRate = 550.0,
                status = "available",
                rating = 4.98f,
                completedJobs = 210,
                certifiedCaces = false,
                emergencyAvailable = true
            ),
            LaborProfileEntity(
                userId = "operator_2",
                fullName = "رشيد الزايدي",
                role = "operator",
                phone = "+212663223344",
                city = "طنجة",
                skills = "شيفور بيلدوزر وتراكس، خبرة متخصصة في شوانط الطرق السيارة والسدود ومقالع الحجارة بالشمال، دقة والتزام صارم بإجراءات السلامة.",
                experienceYears = 9,
                dailyRate = 320.0,
                status = "available",
                rating = 4.90f,
                completedJobs = 85,
                certifiedCaces = true,
                emergencyAvailable = false
            ),
            LaborProfileEntity(
                userId = "mechanic_2",
                fullName = "المعلم كريم العمراني",
                role = "mechanic",
                phone = "+212664334455",
                city = "مراكش",
                skills = "كهرباء وصيانة الماتريال الثقيل، برمجة حساسات كوماتسو وكاتربيلار، صيانة مغير السرعة (Boite de vitesse) والفرامل الهوائية.",
                experienceYears = 12,
                dailyRate = 480.0,
                status = "available",
                rating = 4.92f,
                completedJobs = 115,
                certifiedCaces = false,
                emergencyAvailable = true
            ),
            LaborProfileEntity(
                userId = "operator_3",
                fullName = "يوسف برادة",
                role = "operator",
                phone = "+212665445566",
                city = "فاس",
                skills = "شيفور رافعة كروا تلسكوبية وشوكية، متخصص في رفع الهياكل الحديدية والخرسانية الجاهزة، شهادة أهلية مهنية دولية.",
                experienceYears = 11,
                dailyRate = 420.0,
                status = "busy",
                rating = 4.94f,
                completedJobs = 94,
                certifiedCaces = true,
                emergencyAvailable = false
            )
        )
        dao.insertLaborProfiles(seedLabor)

        // Seed sample booking
        val seedBooking = BookingEntity(
            machineId = 1,
            machineTitle = "ماجورة حفر كوماتسو Komatsu PC210",
            machineCategory = "excavator",
            renterId = "renter_demo",
            renterName = "مقاولة بناني للأشغال",
            renterPhone = "+212661001122",
            ownerName = "المقاولات المغربية الكبرى",
            ownerPhone = "+212661889900",
            city = "القنيطرة",
            startDate = "2026-09-15",
            endDate = "2026-09-20",
            daysCount = 5,
            dailyRate = 2200.0,
            totalPrice = 11000.0,
            status = "approved"
        )
        dao.insertBooking(seedBooking)

        // Seed welcome chat message
        val seedMessage = ChatMessageEntity(
            machineId = 1,
            senderId = "owner_1",
            senderName = "المقاولات المغربية الكبرى",
            senderRole = "owner",
            text = "السلام عليكم، مرحبا بيك. الماجورة Komatsu PC210 واجدة بالقنيطرة مع الشيفور ومجهزة بالبريز روش إلا كنتي كتحتاجو.",
            isFromCurrentUser = false
        )
        dao.insertMessage(seedMessage)
    }

    fun getIndustrialAds(): List<IndustrialAd> {
        return listOf(
            IndustrialAd(
                id = "ad_tractafric",
                sponsorName = "Tractafric Equipment Maroc",
                title = "قطع الغيار الأصلية CAT مع توصيل مباشر للشانطي",
                subtitle = "تخفيض 15% على الفلاتر الأصلية، أسنان الدلو، وزيوت الهيدروليك لجميع مقاولي البناء بالمغرب.",
                promoTag = "شريك رسمي معتمد",
                actionText = "طلب الكاتالوج والأسعار",
                phone = "+212522001122",
                category = "spare_parts"
            ),
            IndustrialAd(
                id = "ad_totalenergies",
                sponsorName = "TotalEnergies Maroc Lubrifiants",
                title = "زيوت محركات الشوانط الثقيلة Rubia TIR 7400",
                subtitle = "حماية فائقة للمحركات الهيدروليكية من الحرارة والضغط العالي. براميل 208 لتر مع تسليم مجاني للأوراش.",
                promoTag = "جودة أوروبية مصادق عليها",
                actionText = "تواصل مع الموزع الإقليمي",
                phone = "+212522334455",
                category = "lubricants"
            ),
            IndustrialAd(
                id = "ad_rma",
                sponsorName = "تأمين الآليات والأوراش RMA Watanya",
                title = "تأمين شامل للمعدات ومسؤولية المقاولات (Tous Risques Chantier)",
                subtitle = "عقد مخصص يحمي الماتريال ضد انقلاب الآلية، الأعطال الفجائية، والسرقة مع تعويض سريع خلال 48 ساعة.",
                promoTag = "تسوية سريعة بالمغرب",
                actionText = "احسب كلفة التأمين",
                phone = "+212522778899",
                category = "insurance"
            ),
            IndustrialAd(
                id = "ad_portechar",
                sponsorName = "ديباناج ونقل الآليات الثقيلة Porte-Char 24/7",
                title = "خدمة نقل الماجورات والبيلدوزرات بين المدن والشوانط",
                subtitle = "شاحنات Porte-char حمولة حتى 60 طن جاهزة بالدار البيضاء، القنيطرة، طنجة، ومراكش بأفضل سعر للكيلومتر.",
                promoTag = "تدخل سريع 24/7",
                actionText = "طلب Porte-Char فورا",
                phone = "+212661445566",
                category = "transport"
            )
        )
    }
}
