package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.auth.AuthUserState
import com.example.data.model.MoroccanCities
import com.example.data.model.UserRole
import com.example.ui.theme.ConstructionAmberDark
import com.example.ui.theme.ConstructionAmberPrimary
import com.example.ui.theme.IndustrialSlateDark
import com.example.ui.theme.MoroccanGreen

/**
 * Moroccan Construction Marketplace Auth & User Account Dialog
 * Integrates:
 * - Google Sign-In with AndroidX Credential Manager
 * - Firebase Authentication (Email/Password)
 * - Explicit Account Roles: Owners (مالك المعدات), Renters (مقاول/مستأجر), Mechanics (فني صيانة وديباناج), Operators (شيفور)
 * - Quick-Switch 1-Tap Profiles for instantaneous testing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAuthAccountDialog(
    authState: AuthUserState,
    onDismiss: () -> Unit,
    onGoogleSignIn: (Context, UserRole) -> Unit,
    onEmailSignIn: (String, String, UserRole) -> Unit,
    onEmailSignUp: (String, String, String, String, String, UserRole, String?) -> Unit,
    onQuickSwitchRole: (UserRole) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Google & Demo Quick Sign-In, 1: Email Login, 2: New Registration
    var targetRole by remember { mutableStateOf(authState.activeRole) }

    // Form fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+212 6") }
    var selectedCity by remember { mutableStateOf("الدار البيضاء") }
    var cityExpanded by remember { mutableStateOf(false) }
    var companyName by remember { mutableStateOf("") }

    val user = authState.user
    val isAuthenticated = authState.isAuthenticated

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .heightIn(max = 680.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("user_auth_dialog"),
                color = Color.White,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "حساب المستخدم والتوثيق 🔐",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndustrialSlateDark
                            )
                            Text(
                                text = "Firebase Auth + Google Credential Manager",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = Color(0xFF475569),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current Authenticated User Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAuthenticated) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAuthenticated) MoroccanGreen.copy(alpha = 0.4f) else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(if (isAuthenticated) MoroccanGreen else Color(0xFF94A3B8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = authState.activeRole.iconEmoji,
                                        fontSize = 22.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user?.fullName ?: "زائر مؤقت",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isAuthenticated) MoroccanGreen.copy(alpha = 0.15f) else Color(0xFFE2E8F0)
                                        ) {
                                            Text(
                                                text = if (isAuthenticated) "موثق ✓" else "زائر",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isAuthenticated) MoroccanGreen else Color(0xFF64748B),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${authState.activeRole.arabicName} • ${authState.activeRole.darijaLabel}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF475569)
                                    )

                                    if (!user?.companyName.isNullOrBlank()) {
                                        Text(
                                            text = user.companyName,
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }

                            if (isAuthenticated) {
                                OutlinedButton(
                                    onClick = onSignOut,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_sign_out")
                                ) {
                                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("خروج", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Role Selection Chip Group: Owner, Renter, Mechanic, Operator
                    Text(
                        text = "اختر صفتك في منصة آليات المغرب 🏗️:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            val isSelected = targetRole == role
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFFFFFBEB) else Color(0xFFF8FAFC))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) ConstructionAmberDark else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { targetRole = role }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = role.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = role.darijaLabel,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF78350F) else Color(0xFF475569)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tab Selector: Google & Quick Switch / Email Login / New Account
                    TabRow(
                        selectedTabIndex = selectedAuthTab,
                        containerColor = Color(0xFFF1F5F9),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedAuthTab]),
                                color = ConstructionAmberDark
                            )
                        },
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedAuthTab == 0,
                            onClick = { selectedAuthTab = 0 },
                            text = { Text("Google & تجريبي", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedAuthTab == 1,
                            onClick = { selectedAuthTab = 1 },
                            text = { Text("تسجيل الدخول", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedAuthTab == 2,
                            onClick = { selectedAuthTab = 2 },
                            text = { Text("حساب جديد", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // TAB 0: Google Sign-In with Credential Manager + 1-Tap Moroccan Role Profiles
                    if (selectedAuthTab == 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Official Google Sign-In Button
                            Button(
                                onClick = { onGoogleSignIn(context, targetRole) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_google_signin"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !authState.isLoading
                            ) {
                                if (authState.isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Google "G" Badge
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color.White),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "G",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = Color(0xFF4285F4)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "متابعة بواسطة Google كـ (${targetRole.darijaLabel})",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "أو تجربة الحسابات الجاهزة بنقرة واحدة (شوانط المغرب 🇲🇦):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(top = 6.dp)
                            )

                            // Quick Demo Accounts for Moroccan Construction Sector
                            DemoRoleCard(
                                title = "الحاج محمد الناصري (مول الماتريال 🚜)",
                                subtitle = "الشركة الناصرية للآليات الثقيلة • الدار البيضاء / طنجة",
                                role = UserRole.OWNER,
                                onClick = { onQuickSwitchRole(UserRole.OWNER) }
                            )

                            DemoRoleCard(
                                title = "المهندس كريم بنيس (كراي / مقاول 🏗️)",
                                subtitle = "بنيس للأشغال العمومية • طنجة المتوسط",
                                role = UserRole.RENTER,
                                onClick = { onQuickSwitchRole(UserRole.RENTER) }
                            )

                            DemoRoleCard(
                                title = "المعلم حميد (فني صيانة وديباناج 🔧)",
                                subtitle = "خبير هيدروليك الشوانط • القنيطرة / الرباط",
                                role = UserRole.MECHANIC,
                                onClick = { onQuickSwitchRole(UserRole.MECHANIC) }
                            )

                            DemoRoleCard(
                                title = "سي بوشعيب التراكس (شيفور محترف 👷)",
                                subtitle = "سائق آليات ثقيلة معتمد CACES • مراكش",
                                role = UserRole.OPERATOR,
                                onClick = { onQuickSwitchRole(UserRole.OPERATOR) }
                            )
                        }
                    }

                    // TAB 1: Email / Password Sign-In
                    if (selectedAuthTab == 1) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("البريد الإلكتروني") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ConstructionAmberDark) },
                                modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("كلمة المرور") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ConstructionAmberDark) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = { onEmailSignIn(email, password, targetRole) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_email_login"),
                                colors = ButtonDefaults.buttonColors(containerColor = ConstructionAmberDark),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !authState.isLoading
                            ) {
                                if (authState.isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("دخول إلى حساب (${targetRole.darijaLabel})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }

                    // TAB 2: New Account Registration
                    if (selectedAuthTab == 2) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("الاسم الكامل / اسم المقاولة") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ConstructionAmberDark) },
                                modifier = Modifier.fillMaxWidth().testTag("auth_fullname_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("رقم الهاتف المغربي (واتساب)") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MoroccanGreen) },
                                modifier = Modifier.fillMaxWidth().testTag("auth_phone_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // City Dropdown
                            ExposedDropdownMenuBox(
                                expanded = cityExpanded,
                                onExpandedChange = { cityExpanded = !cityExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedCity,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("المدينة / الجهة بالمغرب") },
                                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = ConstructionAmberDark) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = cityExpanded,
                                    onDismissRequest = { cityExpanded = false }
                                ) {
                                    MoroccanCities.list.filter { it != MoroccanCities.ALL }.forEach { city ->
                                        DropdownMenuItem(
                                            text = { Text(city) },
                                            onClick = {
                                                selectedCity = city
                                                cityExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            if (targetRole == UserRole.OWNER || targetRole == UserRole.RENTER || targetRole == UserRole.MECHANIC) {
                                OutlinedTextField(
                                    value = companyName,
                                    onValueChange = { companyName = it },
                                    label = { Text("اسم الشركة / الورشة (اختياري)") },
                                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = ConstructionAmberDark) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("البريد الإلكتروني") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ConstructionAmberDark) },
                                modifier = Modifier.fillMaxWidth().testTag("auth_register_email"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("كلمة المرور") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ConstructionAmberDark) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth().testTag("auth_register_password"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    onEmailSignUp(
                                        email,
                                        password,
                                        fullName,
                                        phone,
                                        selectedCity,
                                        targetRole,
                                        companyName.ifBlank { null }
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_register_submit"),
                                colors = ButtonDefaults.buttonColors(containerColor = MoroccanGreen),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !authState.isLoading
                            ) {
                                if (authState.isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("إنشاء حساب (${targetRole.arabicName}) 🚀", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoRoleCard(
    title: String,
    subtitle: String,
    role: UserRole,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("demo_role_${role.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFFBEB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = role.iconEmoji, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = ConstructionAmberPrimary.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "دخول فوري ⚡",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ConstructionAmberDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
    }
}
