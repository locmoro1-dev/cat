package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MachineCategory
import com.example.data.model.MachineEntity
import com.example.data.model.MoroccanCities
import com.example.data.model.UserRole
import com.example.ui.components.LaborCard
import com.example.ui.components.MachineCard
import com.example.ui.components.MoroccanHeader
import com.example.ui.components.SponsoredBannerCard
import com.example.ui.components.UserAuthAccountDialog
import com.example.ui.dialogs.AddMachineDialog
import com.example.ui.dialogs.BookingRequestDialog
import com.example.ui.dialogs.DirectChatDialog
import com.example.ui.dialogs.RoleSelectionDialog
import com.example.ui.theme.ConstructionAmberDark
import com.example.ui.theme.ConstructionAmberLight
import com.example.ui.theme.ConstructionAmberPrimary
import com.example.ui.theme.IndustrialSlateDark
import com.example.ui.theme.MoroccanGreen
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun MainMarketplaceScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // RTL Enforcement for Moroccan Arabic / Darija Localization
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
        val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
        val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
        val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
        val laborRoleFilter by viewModel.laborRoleFilter.collectAsStateWithLifecycle()

        val machines by viewModel.machines.collectAsStateWithLifecycle()
        val laborProfiles by viewModel.laborProfiles.collectAsStateWithLifecycle()
        val bookings by viewModel.bookings.collectAsStateWithLifecycle()
        val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
        val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
        val syncState by viewModel.syncState.collectAsStateWithLifecycle()
        val authState by viewModel.authState.collectAsStateWithLifecycle()
        val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()

        val showAddMachineDialog by viewModel.showAddMachineDialog.collectAsStateWithLifecycle()
        val selectedMachineForBooking by viewModel.selectedMachineForBooking.collectAsStateWithLifecycle()
        val selectedTargetForChat by viewModel.selectedTargetForChat.collectAsStateWithLifecycle()

        var showRoleDialog by remember { mutableStateOf(false) }

        // Snackbar observer
        LaunchedEffect(snackbarMessage) {
            snackbarMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                viewModel.clearSnackbar()
            }
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                MoroccanHeader(
                    currentRole = currentRole,
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onOpenRoleSelector = { viewModel.openAuthDialog() },
                    onOpenBlueprint = { viewModel.setSelectedTab(3) },
                    isAuthenticated = authState.isAuthenticated
                )
            },
            floatingActionButton = {
                // Floating Action Button to add machine instantly (FAB)
                if (selectedTab == 0) {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.openAddMachineDialog() },
                        containerColor = ConstructionAmberDark,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(6.dp),
                        modifier = Modifier
                            .testTag("fab_add_machine")
                            .navigationBarsPadding(),
                        icon = {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "زيد ماجورة")
                        },
                        text = {
                            Text(
                                text = "زيد ماجورة ديالك 🚜",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FAFC))
            ) {
                // Navigation Tabs
                val tabTitles = listOf(
                    "آليات ومعدات 🚜",
                    "كفاءات وشوافر 👷",
                    "طلبات الكراء 📋",
                    "المخطط والـ SQL 📐"
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = ConstructionAmberDark,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = ConstructionAmberDark,
                            height = 3.dp
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { viewModel.setSelectedTab(index) },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == index) ConstructionAmberDark else Color(0xFF64748B)
                                )
                            },
                            modifier = Modifier.testTag("tab_$index")
                        )
                    }
                }

                // Tab Content Rendering
                when (selectedTab) {
                    0 -> MachinesFeedTab(
                        machines = machines,
                        industrialAds = viewModel.industrialAds,
                        selectedCity = selectedCity,
                        selectedCategory = selectedCategory,
                        syncState = syncState,
                        onTriggerSync = { viewModel.triggerSync() },
                        onSelectCity = { viewModel.setCity(it) },
                        onSelectCategory = { viewModel.setCategory(it) },
                        onBookMachine = { viewModel.openBookingDialog(it) },
                        onChatMachine = { viewModel.openChatDialog(it) },
                        onWhatsApp = { phone, msg -> viewModel.launchWhatsApp(context, phone, msg) },
                        onCall = { phone -> viewModel.dialPhone(context, phone) },
                        onToggleBoost = { viewModel.toggleBoostMachine(it) }
                    )
                    1 -> LaborFeedTab(
                        laborProfiles = laborProfiles,
                        selectedCity = selectedCity,
                        laborRoleFilter = laborRoleFilter,
                        onSelectCity = { viewModel.setCity(it) },
                        onSelectRoleFilter = { viewModel.setLaborRoleFilter(it) },
                        onContactLabor = { viewModel.openChatDialog(it) },
                        onWhatsApp = { phone, msg -> viewModel.launchWhatsApp(context, phone, msg) },
                        onCall = { phone -> viewModel.dialPhone(context, phone) }
                    )
                    2 -> BookingsTab(
                        bookings = bookings,
                        onWhatsApp = { phone, msg -> viewModel.launchWhatsApp(context, phone, msg) },
                        onCall = { phone -> viewModel.dialPhone(context, phone) }
                    )
                    3 -> ArchitectureBlueprintScreen(
                        syncState = syncState,
                        onTriggerSync = { viewModel.triggerSync() }
                    )
                }
            }
        }

        // Dialogs
        if (showAuthDialog) {
            UserAuthAccountDialog(
                authState = authState,
                onDismiss = { viewModel.closeAuthDialog() },
                onGoogleSignIn = { ctx, role -> viewModel.signInWithGoogle(ctx, role) },
                onEmailSignIn = { em, pass, role -> viewModel.signInWithEmail(em, pass, role) },
                onEmailSignUp = { em, pass, name, ph, city, role, comp ->
                    viewModel.signUpWithEmail(em, pass, name, ph, city, role, comp)
                },
                onQuickSwitchRole = { role -> viewModel.quickSwitchUserRole(role) },
                onSignOut = { viewModel.signOut() }
            )
        }

        if (showRoleDialog) {
            RoleSelectionDialog(
                currentRole = currentRole,
                onSelectRole = { viewModel.setRole(it) },
                onDismiss = { showRoleDialog = false }
            )
        }

        if (showAddMachineDialog) {
            AddMachineDialog(
                onDismiss = { viewModel.closeAddMachineDialog() },
                onAddMachine = { title, cat, rate, city, loc, desc, brand, isFeatured ->
                    viewModel.addMachine(title, cat, rate, city, loc, desc, brand, isFeatured)
                }
            )
        }

        selectedMachineForBooking?.let { machine ->
            BookingRequestDialog(
                machine = machine,
                onDismiss = { viewModel.closeBookingDialog() },
                onConfirmBooking = { name, phone, start, end, days ->
                    viewModel.confirmBooking(machine, name, phone, start, end, days)
                }
            )
        }

        selectedTargetForChat?.let { target ->
            DirectChatDialog(
                target = target,
                messages = chatMessages,
                onSendMessage = { text, recipient ->
                    viewModel.sendChatMessage(
                        (target as? MachineEntity)?.id,
                        text,
                        recipient
                    )
                },
                onWhatsAppClick = { phone, text ->
                    viewModel.launchWhatsApp(context, phone, text)
                },
                onCallClick = { phone ->
                    viewModel.dialPhone(context, phone)
                },
                onDismiss = { viewModel.closeChatDialog() }
            )
        }
    }
}

/**
 * Tab 0: Machines Feed with Real-time broadcast, City filters, Category filters, B2B Ads
 */
@Composable
private fun MachinesFeedTab(
    machines: List<MachineEntity>,
    industrialAds: List<com.example.data.model.IndustrialAd>,
    selectedCity: String,
    selectedCategory: MachineCategory,
    syncState: com.example.data.sync.SyncState,
    onTriggerSync: () -> Unit,
    onSelectCity: (String) -> Unit,
    onSelectCategory: (MachineCategory) -> Unit,
    onBookMachine: (MachineEntity) -> Unit,
    onChatMachine: (MachineEntity) -> Unit,
    onWhatsApp: (phone: String, message: String) -> Unit,
    onCall: (phone: String) -> Unit,
    onToggleBoost: (MachineEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Room ⟷ Firestore Cloud Sync Status Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("firestore_sync_banner"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "مزامنة سحابية (Firestore)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (syncState.isFirebaseConfigured) Color(0xFFECFDF5) else Color(0xFFF1F5F9)
                                ) {
                                    Text(
                                        text = if (syncState.isFirebaseConfigured) "سحابي نشط ✓" else "تخزين محلي مؤقت 📱",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (syncState.isFirebaseConfigured) MoroccanGreen else Color(0xFF64748B),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${syncState.syncedCount} آليات متزامنة • ${syncState.statusMessage}",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Button(
                        onClick = onTriggerSync,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        enabled = !syncState.isSyncing,
                        modifier = Modifier.testTag("btn_sync_now")
                    ) {
                        if (syncState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "مزامنة",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (syncState.isSyncing) "مزامنة..." else "مزامنة",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Moroccan Regional Filter Chips
        item {
            Column {
                Text(
                    text = "تصفية حسب المدن والجهات بالمغرب 📍",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MoroccanCities.list) { city ->
                        val isSelected = city == selectedCity
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) ConstructionAmberDark else Color.White)
                                .border(
                                    1.dp,
                                    if (isSelected) ConstructionAmberDark else Color(0xFFCBD5E1),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { onSelectCity(city) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("city_filter_$city")
                        ) {
                            Text(
                                text = city,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }

        // Machinery Category Chips (ماجورة، بيلدوزر، تراكس...)
        item {
            Column {
                Text(
                    text = "نوع الآلية والمعدة الثقيلة 🚜",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MachineCategory.entries) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF1E242B) else Color.White)
                                .border(
                                    1.dp,
                                    if (isSelected) ConstructionAmberPrimary else Color(0xFFE2E8F0),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectCategory(cat) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                .testTag("category_filter_${cat.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = cat.icon, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = cat.darijaTerm,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ConstructionAmberPrimary else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Machinery Count status
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الآليات المتاحة فورا (${machines.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "⚡ تحديث مباشر فوري",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MoroccanGreen
                )
            }
        }

        // Machines List (Promoted boosted to top)
        if (machines.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🚜", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "ما لقينا حتى ماجورة بهاد المعايير",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "جرب تبدل المدينة أو اختار 'كولشي الماتريال' باش تشوف جميع الآليات.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        items(machines) { machine ->
            MachineCard(
                machine = machine,
                onBookClick = { onBookMachine(machine) },
                onChatClick = { onChatMachine(machine) },
                onWhatsAppClick = {
                    onWhatsApp(
                        machine.ownerPhone,
                        "السلام عليكم، شفت الماجورة ديالك (${machine.title}) بـ ${machine.city} فـ Engins Maroc وباغي نكريها للشانطي."
                    )
                },
                onCallClick = { onCall(machine.ownerPhone) },
                onToggleBoost = { onToggleBoost(machine) }
            )
        }

        // Sponsored Industrial B2B Banner Insertion
        if (industrialAds.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                SponsoredBannerCard(
                    ad = industrialAds.first(),
                    onActionClick = {
                        onWhatsApp(
                            industrialAds.first().phone,
                            "السلام عليكم، أنا مقاول شوانط مهتم بالعرض التجاري ديالكم فـ Engins Maroc: ${industrialAds.first().title}"
                        )
                    }
                )
            }
        }
    }
}

/**
 * Tab 1: Labor & Operators & Mechanics Feed
 */
@Composable
private fun LaborFeedTab(
    laborProfiles: List<com.example.data.model.LaborProfileEntity>,
    selectedCity: String,
    laborRoleFilter: String,
    onSelectCity: (String) -> Unit,
    onSelectRoleFilter: (String) -> Unit,
    onContactLabor: (com.example.data.model.LaborProfileEntity) -> Unit,
    onWhatsApp: (phone: String, message: String) -> Unit,
    onCall: (phone: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Role filter toggle (All / Operators / Mechanics)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    Triple("all", "كولشي المهنيين", "👷🔧"),
                    Triple("operator", "شوافر الآليات فقط", "👷"),
                    Triple("mechanic", "ميكانيك وديباناج فقط", "🔧")
                )

                filters.forEach { (roleKey, label, emoji) ->
                    val isSelected = laborRoleFilter == roleKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ConstructionAmberDark else Color.White)
                            .border(
                                1.dp,
                                if (isSelected) ConstructionAmberDark else Color(0xFFCBD5E1),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectRoleFilter(roleKey) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$emoji $label",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF334155)
                        )
                    }
                }
            }
        }

        // City Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MoroccanCities.list) { city ->
                    val isSelected = city == selectedCity
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFF1E242B) else Color.White)
                            .border(
                                1.dp,
                                if (isSelected) ConstructionAmberPrimary else Color(0xFFE2E8F0),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onSelectCity(city) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = city,
                            fontSize = 11.sp,
                            color = if (isSelected) ConstructionAmberPrimary else Color(0xFF334155),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "الكفاءات والمهنيين المتاحين (${laborProfiles.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        items(laborProfiles) { profile ->
            LaborCard(
                profile = profile,
                onContactClick = { onContactLabor(profile) },
                onWhatsAppClick = {
                    onWhatsApp(
                        profile.phone,
                        "السلام عليكم أخي ${profile.fullName}، شفت البروفايل ديالك فـ Engins Maroc ومحتاجين خدمتك فشانطي بـ ${profile.city}."
                    )
                },
                onCallClick = { onCall(profile.phone) }
            )
        }
    }
}

/**
 * Tab 2: Rental Bookings and Requests Overview
 */
@Composable
private fun BookingsTab(
    bookings: List<com.example.data.model.BookingEntity>,
    onWhatsApp: (phone: String, message: String) -> Unit,
    onCall: (phone: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ConstructionAmberLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, ConstructionAmberPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = ConstructionAmberDark,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "سجل طلبات الكراء والأشغال",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "تتبع فوري لمواعيد تشغيل الآليات وأسعار العقود المبرمة",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }
        }

        if (bookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📋", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ما كاين حتى طلب كراء حاليا",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "اختر أي ماجورة من الصفحة الأولى واضغط على 'اكري الآلة دابا'.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        items(bookings) { booking ->
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
                        Text(
                            text = booking.machineTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (booking.status == "approved") Color(0xFFECFDF5) else Color(0xFFFFFBEB)
                        ) {
                            Text(
                                text = if (booking.status == "approved") "تم التأكيد ✓" else "قيد المراجعة ⏳",
                                color = if (booking.status == "approved") MoroccanGreen else Color(0xFFB45309),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "المستأجر: ${booking.renterName} (${booking.renterPhone})",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "المالك: ${booking.ownerName} • المدينة: ${booking.city}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الفترة: ${booking.startDate} إلى ${booking.endDate} (${booking.daysCount} أيام)",
                            fontSize = 11.sp,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "${booking.totalPrice.toInt()} د.م",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ConstructionAmberDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF25D366),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onWhatsApp(
                                        booking.ownerPhone,
                                        "السلام عليكم، بخصوص حجز الماجورة #${booking.id} (${booking.machineTitle}) للفترة ${booking.startDate}."
                                    )
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("تواصل بالواتساب", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0284C7),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onCall(booking.ownerPhone) }
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("اتصال هاتفي", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
