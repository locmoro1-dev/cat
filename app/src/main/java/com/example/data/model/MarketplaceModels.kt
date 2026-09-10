package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Moroccan Construction Marketplace Roles
 * Hybrid Arabic / Darija labeling
 */
enum class UserRole(
    val id: String,
    val arabicName: String,
    val darijaLabel: String,
    val description: String,
    val iconEmoji: String
) {
    OWNER(
        id = "owner",
        arabicName = "مالك المعدات",
        darijaLabel = "مول الماتريال",
        description = "عندك ماجورات وآليات ثقيلة باغي تكريهم",
        iconEmoji = "🚜"
    ),
    RENTER(
        id = "renter",
        arabicName = "مقاول / مستأجر",
        darijaLabel = "كراي / مقاول",
        description = "باغي تكري ماجورة أو ماتريال لشانطي ديالك",
        iconEmoji = "🏗️"
    ),
    OPERATOR(
        id = "operator",
        arabicName = "سائق آليات محترف",
        darijaLabel = "شيفور الماتريال",
        description = "سائق محترف كتقلب على خدمة فالشوانط",
        iconEmoji = "👷"
    ),
    MECHANIC(
        id = "mechanic",
        arabicName = "فني صيانة وديباناج",
        darijaLabel = "ميكانيكي وديباناج",
        description = "صيانة الهيدروليك والمحركات في عين المكان",
        iconEmoji = "🔧"
    )
}

/**
 * Heavy Machinery Categories in Morocco
 * Both MSA and Darija colloquial terms
 */
enum class MachineCategory(
    val id: String,
    val msaName: String,
    val darijaTerm: String,
    val icon: String
) {
    ALL(
        id = "all",
        msaName = "جميع الآليات",
        darijaTerm = "كولشي الماتريال",
        icon = "⚡"
    ),
    EXCAVATOR(
        id = "excavator",
        msaName = "حفارة هيدروليكية",
        darijaTerm = "ماجورة / Poclain",
        icon = "🚜"
    ),
    BULLDOZER(
        id = "bulldozer",
        msaName = "جرافة مسرفة",
        darijaTerm = "بيلدوزر د الشانطي",
        icon = "🚧"
    ),
    LOADER(
        id = "loader",
        msaName = "محمل مدولب",
        darijaTerm = "تراكس / Chargeuse",
        icon = "🚜"
    ),
    CRANE(
        id = "crane",
        msaName = "رافعة شوكية وتلسكوبية",
        darijaTerm = "كروا / Grue",
        icon = "🏗️"
    ),
    DUMP_TRUCK(
        id = "dump_truck",
        msaName = "شاحنة تفريغ ثقيلة",
        darijaTerm = "كاميو د الشوانط 8x4",
        icon = "🚛"
    ),
    COMPACTOR(
        id = "compactor",
        msaName = "مداحل وضواغط التربة",
        darijaTerm = "كومباكتور / Roulleau",
        icon = "⚙️"
    )
}

/**
 * Major Moroccan Cities for construction hubs
 */
object MoroccanCities {
    val ALL = "الكل بالمغرب"
    val CASABLANCA = "الدار البيضاء"
    val KENITRA = "القنيطرة"
    val TANGER = "طنجة"
    val RABAT = "الرباط"
    val MARRAKECH = "مراكش"
    val FES = "فاس"
    val AGADIR = "أكادير"
    val OUJDA = "وجدة"
    val TETOUAN = "تطوان"
    val NADOR = "الناظور"
    val LAAYOUNE = "العيون"

    val list = listOf(
        ALL,
        CASABLANCA,
        KENITRA,
        TANGER,
        RABAT,
        MARRAKECH,
        FES,
        AGADIR,
        OUJDA,
        TETOUAN,
        NADOR,
        LAAYOUNE
    )
}

/**
 * Room Entities matching the Supabase / PostgreSQL Schema
 */

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val phone: String,
    val fullName: String,
    val city: String,
    val role: String, // 'owner', 'renter', 'operator', 'mechanic'
    val email: String? = null,
    val companyName: String? = null,
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MachineSyncStatus {
    SYNCED,
    PENDING_UPLOAD,
    PENDING_UPDATE,
    FAILED
}

@Entity(tableName = "machines")
data class MachineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: String,
    val ownerName: String,
    val ownerPhone: String,
    val title: String,
    val category: String, // excavator, bulldozer, crane, etc.
    val dailyRate: Double, // MAD / Day
    val location: String, // e.g. المنطقة الصناعية، طريق طنجة
    val city: String, // e.g. القنيطرة, الدار البيضاء
    val description: String,
    val status: String = "available", // 'available' (متاح) or 'rented' (مكرية)
    val isFeatured: Boolean = false, // Promoted Listing (إعلان مميز)
    val brandModel: String = "CAT 320D",
    val year: Int = 2021,
    val hasOperator: Boolean = true, // مع شيفور أو بدون
    val imagesJson: String = "[]",
    val viewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val firestoreId: String? = null,
    val syncStatus: String = MachineSyncStatus.SYNCED.name,
    val updatedAt: Long = System.currentTimeMillis(),
    val lastSyncedAt: Long? = null
)

fun MachineEntity.toFirestoreMap(): Map<String, Any?> {
    return mapOf(
        "ownerId" to ownerId,
        "ownerName" to ownerName,
        "ownerPhone" to ownerPhone,
        "title" to title,
        "category" to category,
        "dailyRate" to dailyRate,
        "location" to location,
        "city" to city,
        "description" to description,
        "status" to status,
        "isFeatured" to isFeatured,
        "brandModel" to brandModel,
        "year" to year,
        "hasOperator" to hasOperator,
        "imagesJson" to imagesJson,
        "viewCount" to viewCount,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

@Entity(tableName = "labor_profiles")
data class LaborProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val fullName: String,
    val role: String, // 'operator' or 'mechanic'
    val phone: String,
    val city: String,
    val skills: String, // e.g. سياقة البوكلان والحفر العميق
    val experienceYears: Int,
    val dailyRate: Double, // MAD / Day or Service
    val status: String = "available", // 'available' (متاح للخدمة) or 'busy' (فشانطي)
    val rating: Float = 4.9f,
    val completedJobs: Int = 42,
    val certifiedCaces: Boolean = true, // شهادة قيادة معتمدة CACES
    val emergencyAvailable: Boolean = false, // خدمة طوارئ وديباناج سريع
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val machineId: Long,
    val machineTitle: String,
    val machineCategory: String,
    val renterId: String,
    val renterName: String,
    val renterPhone: String,
    val ownerName: String,
    val ownerPhone: String,
    val city: String,
    val startDate: String,
    val endDate: String,
    val daysCount: Int,
    val dailyRate: Double,
    val totalPrice: Double,
    val status: String = "pending", // 'pending' (قيد المراجعة), 'approved' (تم القبول), 'completed' (مكتمل)
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val machineId: Long? = null,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val text: String,
    val isFromCurrentUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Localized Moroccan B2B Industrial Ads
 */
data class IndustrialAd(
    val id: String,
    val sponsorName: String,
    val title: String,
    val subtitle: String,
    val promoTag: String,
    val actionText: String,
    val phone: String,
    val category: String // "spare_parts", "lubricants", "insurance", "transport"
)
