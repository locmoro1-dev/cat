package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.blueprint.ArchitectureBlueprint
import com.example.data.sync.SyncState
import com.example.ui.theme.ConstructionAmberDark
import com.example.ui.theme.ConstructionAmberLight
import com.example.ui.theme.ConstructionAmberPrimary
import com.example.ui.theme.IndustrialSlateDark
import com.example.ui.theme.MoroccanGreen

@Composable
fun ArchitectureBlueprintScreen(
    syncState: SyncState? = null,
    onTriggerSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSlateDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, ConstructionAmberPrimary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ConstructionAmberPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Architecture,
                                contentDescription = null,
                                tint = Color(0xFF1E242B),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "المخطط المعماري والتقني للمنصة",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Room ⟷ Firebase Firestore Real-Time Architecture",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "تصميم هندسي متكامل يجمع بين التخزين المحلي السريع في Room Database والمزامنة السحابية اللحظية مع Firebase Firestore، مما يضمن عمل التطبيق بنمط Offline-First فائق السرعة في ورشات وشوانط البناء بالمغرب.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Live Room ⟷ Firestore Synchronization Controller
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF3B82F6))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "محرك المزامنة (Room ⟷ Firestore Sync)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = syncState?.statusMessage ?: "جاهز للمزامنة اللحظية",
                                    fontSize = 11.sp,
                                    color = if (syncState?.isFirebaseConfigured == true) MoroccanGreen else Color(0xFF64748B)
                                )
                            }
                        }

                        Button(
                            onClick = onTriggerSync,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(8.dp),
                            enabled = syncState?.isSyncing != true,
                            modifier = Modifier.testTag("sync_now_blueprint_btn")
                        ) {
                            if (syncState?.isSyncing == true) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "مزامنة",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (syncState?.isSyncing == true) "جاري المزامنة..." else "مزامنة الآن",
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "حالة الاتصال", fontSize = 10.sp, color = Color(0xFF64748B))
                                Text(
                                    text = if (syncState?.isFirebaseConfigured == true) "متصل بـ Firestore ☁️" else "تخزين محلي مؤقت 📱",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = "آليات متزامنة", fontSize = 10.sp, color = Color(0xFF64748B))
                                Text(
                                    text = "${syncState?.syncedCount ?: 6} آليات نشطة",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Room ⟷ Firestore Architecture Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = ConstructionAmberDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "نمط المزامنة: Offline-First Single Source of Truth",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "هيكلية إدارة بيانات الآليات بين Room و Firestore:",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val syncSteps = listOf(
                        Triple("1. المصدر الوحيد للحقيقة (SSOT)", "واجهة Compose تستمع لـ Kotlin Flows من Room فقط، مما يلغي التقطيع ويعمل بسرعة خيالية.", "Compose ← Room"),
                        Triple("2. الكتابة المتفائلة (Optimistic Write)", "عند إضافة آلة جديدة أو ترقية إعلان، تُحفظ في Room فورا مع وسم PENDING_UPLOAD.", "Room ← UI"),
                        Triple("3. الرفع التلقائي (Cloud Push)", "يقوم FirestoreMachineSyncManager برفع الوثائق إلى /machines في Firestore وتحديث Room إلى SYNCED.", "Room → Firestore"),
                        Triple("4. الاستماع اللحظي (Snapshot Listener)", "يقوم Firestore ببث أي آليات يضيفها مقاولون آخرون في الدار البيضاء أو طنجة لتنعكس لحظيا في Room.", "Firestore → Room"),
                        Triple("5. فض النزاعات (Conflict Resolution)", "تطبيق قاعدة Last-Write-Wins بالاعتماد على حقل updatedAt لمنع الكتابة فوق التعديلات الأحدث.", "LWW (updatedAt)")
                    )

                    syncSteps.forEach { (title, desc, flow) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ConstructionAmberDark
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFE2E8F0)
                                ) {
                                    Text(
                                        text = flow,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = Color(0xFF475569),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Firebase Auth & AndroidX Credential Manager Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MoroccanGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "توثيق المستخدمين: Firebase Auth & Credential Manager",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "هيكلية إدارة الحسابات وصلاحيات الأدوار (مالك، مقاول، فني صيانة، شيفور):",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val authRoles = listOf(
                        Triple("مول الماتريال (OWNER)", "نشر وإدارة الآليات الثقيلة، تتبع حجوزات الكراء، وترقية الإعلانات المميزة.", "🚜 مالك المعدات"),
                        Triple("كراي الشانطي (RENTER)", "استعراض الكتالوج المغربي، حجز الآليات، والتواصل المباشر عبر واتساب وهاتفياً.", "🏗️ المقاول والمستأجر"),
                        Triple("ميكانيكي الشوانط (MECHANIC)", "إصلاح وصيانة الآليات المتوقفة بالموقع، ديباناج سريع، وفحص الهيدروليك.", "🔧 فني صيانة وديباناج"),
                        Triple("شيفور الماتريال (OPERATOR)", "عرض رخص القيادة CACES وتوفير اليد العاملة المؤهلة للشركات والمقاولات.", "👷 سائق محترف")
                    )

                    authRoles.forEach { (roleTitle, desc, badge) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF0FDF4))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = roleTitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MoroccanGreen
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White
                                ) {
                                    Text(
                                        text = badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = Color(0xFF166534),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🔐 ميزات التوثيق المطبقة:\n" +
                               "• تسجيل دخول Google بنقرة واحدة عبر AndroidX Credential Manager\n" +
                               "• مصادقة Firebase Auth بالبريد الإلكتروني وكلمة المرور المشفرة\n" +
                               "• مزامنة ملف المستخدم تلقائياً بين Room محلياً و Firestore على مسار /users/{uid}\n" +
                               "• تبديل سريع للأدوار للاختبار والمحاكاة الفورية في الشوانط",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(10.dp)
                    )
                }
            }
        }

        // Firestore Security Rules Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFFE11D48),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "قواعد أمان Firestore (Security Rules)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Firestore Rules", ArchitectureBlueprint.FIRESTORE_RULES)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ قواعد أمان Firestore إلى الحافظة! 📋", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E242B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("copy_firestore_rules_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ",
                                tint = ConstructionAmberPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ Rules", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "قواعد أمان سحابية تضمن أن مالك الآلية فقط هو من يستطيع تعديل أو حذف معداته، مع إتاحة التصفح للعموم بالمغرب:",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .padding(12.dp)
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    text = ArchitectureBlueprint.FIRESTORE_RULES.trimIndent(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = Color(0xFFF43F5E),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pillar 1: Localized UI/UX & Language Architecture
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = ConstructionAmberDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. الهندسة اللغوية وتجربة المستخدم المغربية (RTL & Darija)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "تطبيق نموذج لغوي هجين يراعي ثقافة ورشات البناء والشوانط بالمغرب:",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val glossaryItems = listOf(
                        Triple("شيفور", "سائق آليات محترف CACES", "زر طلب الخدمة وبطاقات الملفات الشخصية"),
                        Triple("ماجورة / آلة حفر", "حفارة هيدروليكية (Excavator / Poclain)", "فلاتر التصنيفات والبحث"),
                        Triple("اكري آلة دابا", "طلب استئجار المعدة فورا", "الزر الرئيسي CTA في بطاقات الآليات"),
                        Triple("بيلدوزر د الشانطي", "جرافة مسرفة (Bulldozer)", "فلاتر وتصنيف الأشغال الكبرى"),
                        Triple("مول الماتريال", "مالك المعدات والأسطول (Owner)", "محدد الدور وتبديل الحسابات"),
                        Triple("كراي ومقاول", "مستأجر / مقاول بناء (Renter)", "محدد الدور والحجوزات"),
                        Triple("عيّط دابا / تواصل فالواتساب", "اتصال هاتفي مباشر وتنسيق عبر واتساب", "أزرار التواصل المباشر السريع")
                    )

                    glossaryItems.forEach { (darija, msa, usage) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = darija,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ConstructionAmberDark
                                )
                                Text(
                                    text = msa,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Text(
                                text = usage,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Pillar 2: Database Schema & Realtime SQL
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = ConstructionAmberDark,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. مخطط قاعدة البيانات (Supabase & PostgreSQL DDL)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("SQL Blueprint", ArchitectureBlueprint.POSTGRES_SUPABASE_SQL)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ كود SQL كاملا إلى الحافظة! 📋", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E242B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("copy_sql_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ",
                                tint = ConstructionAmberPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نسخ SQL", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "يتضمن الجداول الرئيسية (Users, Machines, Labor_Profiles, Bookings, Sponsored_Ads)، والمفاتيح الخارجية (Foreign Keys)، والفهارس السريعة (Indexes)، وسياسات أمان البيانات على مستوى الصفوف (Row Level Security - RLS):",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Code Display Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .padding(12.dp)
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    text = ArchitectureBlueprint.POSTGRES_SUPABASE_SQL.trimIndent(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = Color(0xFF38BDF8),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pillar 3: Real-Time Sync & Component Architecture
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = ConstructionAmberDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. هندسة التزامن اللحظي (Real-Time Broadcasting Engine)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• المحرك المحلي: استخدام Room Database مع تدفقات Kotlin Flow التفاعلية. أي ماجورة أو طلب حجز جديد يتم بثه في أجزاء من الثانية إلى واجهات جميع المستخدمين.\n\n• التزامن السحابي: التكامل عبر Supabase Realtime Channels (PostgreSQL WAL CDC over WebSockets) حيث تُبث التغييرات من جدول 'machines' إلى كافة المشتركين.\n\n• الربط المباشر مع واتساب والهاتف: حل مشكلة التنسيق السريع في قطاع البناء المغربي دون الحاجة لوسطاء بيروقراطيين.",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Pillar 4: Monetization & Sponsored Ads
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = ConstructionAmberDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "4. استراتيجية تحقيق الدخل والإعلانات الصناعية (B2B)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. الإعلانات المميزة (Featured Listings ⭐):\nيدفع مالك الماتريال رسما رمزيا (50 درهم / يوم) لتثبيت آلته في صدارة نتائج البحث في مدينته مع الشارة الذهبية، مما يرفع معدل الاتصالات بـ 4 أضعاف.\n\n2. مساحات الإعلانات الصناعية B2B:\nمساحات بطاقات ناتجة غير مزعجة مخصصة لكبار موردي قطاع البناء:\n• وكلاء قطع الغيار المعتمدين (Tractafric CAT, Komatsu Maroc)\n• شركات زيوت المحركات والمحروقات (TotalEnergies, Shell)\n• شركات تأمين ورشات البناء والمعدات (RMA, Wafa Assurance)\n• خدمات النقل الثقيل (Porte-char 24/7)",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        lineHeight = 19.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
