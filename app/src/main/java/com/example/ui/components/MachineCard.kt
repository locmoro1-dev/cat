package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MachineCategory
import com.example.data.model.MachineEntity
import com.example.ui.theme.ConstructionAmberDark
import com.example.ui.theme.ConstructionAmberLight
import com.example.ui.theme.ConstructionAmberPrimary
import com.example.ui.theme.MoroccanGreen

@Composable
fun MachineCard(
    machine: MachineEntity,
    onBookClick: () -> Unit,
    onChatClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit,
    onToggleBoost: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFeatured = machine.isFeatured
    val cardBorderColor = if (isFeatured) ConstructionAmberPrimary else Color(0xFFE2E8F0)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("machine_card_${machine.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFeatured) 4.dp else 1.5.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isFeatured) 1.8.dp else 1.dp,
            color = cardBorderColor
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Promoted / Featured Header Banner (Monetization pillar)
            if (isFeatured) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    ConstructionAmberPrimary,
                                    ConstructionAmberDark
                                )
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "مميز",
                                tint = Color(0xFF1E242B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "إعلان مميز فالمقدمة (Sponsorisé)",
                                color = Color(0xFF1E242B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "أعلى المشاهدات 🚀",
                            color = Color(0xFF1E242B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Main Content Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top row: Category tag & Availability status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category & Model Chip
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val categoryTerm = when (machine.category) {
                            "excavator" -> "ماجورة / Poclain 🚜"
                            "bulldozer" -> "بيلدوزر 🚧"
                            "crane" -> "رافعة كروا 🏗️"
                            "loader" -> "تراكس مدولب 🚜"
                            "dump_truck" -> "كاميو شوانط 🚛"
                            "compactor" -> "كومباكتور ⚙️"
                            else -> "آلية ثقيلة ⚡"
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ConstructionAmberLight
                        ) {
                            Text(
                                text = categoryTerm,
                                color = Color(0xFF92400E),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = machine.brandModel,
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Status Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val (syncLabel, syncBg, syncTextColor) = when (machine.syncStatus) {
                            "SYNCED" -> Triple("Firestore ✓", Color(0xFFEFF6FF), Color(0xFF2563EB))
                            "PENDING_UPLOAD", "PENDING_UPDATE" -> Triple("سحابي...", Color(0xFFFEF3C7), Color(0xFFD97706))
                            else -> Triple("محلي", Color(0xFFF1F5F9), Color(0xFF64748B))
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = syncBg
                        ) {
                            Text(
                                text = syncLabel,
                                color = syncTextColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFECFDF5))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(MoroccanGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "متاح فورا للشانطي",
                                color = MoroccanGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Machine Title
                Text(
                    text = machine.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Location & Moroccan City
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "المدينة",
                        tint = ConstructionAmberDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${machine.city} • ${machine.location}",
                        color = Color(0xFF475569),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Description in Moroccan Darija / MSA
                Text(
                    text = machine.description,
                    color = Color(0xFF334155),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Operator included badge & Owner info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "شيفور معتمد",
                            tint = MoroccanGreen,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (machine.hasOperator) "مع شيفور محترف مجرب" else "بدون شيفور",
                            color = Color(0xFF047857),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "الممول: ${machine.ownerName}",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pricing Row (MAD Moroccan Dirham)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "سومة الكراء (السعر)",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${machine.dailyRate.toInt()}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "د.م / نهار",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ConstructionAmberDark
                            )
                        }
                    }

                    // Boost Toggle for owners (simulation)
                    OutlinedButton(
                        onClick = onToggleBoost,
                        modifier = Modifier
                            .testTag("boost_btn_${machine.id}")
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isFeatured) ConstructionAmberDark else Color(0xFF64748B)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isFeatured) ConstructionAmberPrimary else Color(0xFFCBD5E1)
                        )
                    ) {
                        Icon(
                            imageVector = if (isFeatured) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "تمييز",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFeatured) "مميز ⭐" else "رقّي الإعلان",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Primary CTA Buttons: Rent Now, WhatsApp, Call, In-app Chat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Main Action: Rent Machine Now (اكري آلة دابا)
                    Button(
                        onClick = onBookClick,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("rent_machine_btn_${machine.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ConstructionAmberDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "طلب كراء",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "اكري الآلة دابا",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Direct WhatsApp CTA
                    Button(
                        onClick = onWhatsAppClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("whatsapp_btn_${machine.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "واتساب",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Direct Phone Call
                    IconButton(
                        onClick = onCallClick,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0284C7))
                            .testTag("call_btn_${machine.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "اتصال هاتفي",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // In-app direct message
                    IconButton(
                        onClick = onChatClick,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF1F5F9))
                            .testTag("chat_btn_${machine.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "مراسلة",
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
