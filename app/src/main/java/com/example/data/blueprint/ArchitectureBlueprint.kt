package com.example.data.blueprint

/**
 * Production-grade Technical Blueprint & Database Creation Scripts (SQL)
 * for Supabase / PostgreSQL & Firebase with Real-time Broadcasting & RLS Policies.
 */
object ArchitectureBlueprint {

    const val POSTGRES_SUPABASE_SQL = """
-- =========================================================================
-- MOROCCAN HEAVY MACHINERY & CONSTRUCTION TWO-SIDED MARKETPLACE
-- PRODUCTION-GRADE POSTGRESQL & SUPABASE REAL-TIME SCHEMA
-- =========================================================================

-- Enable UUID extension & PostGIS (optional for geo-distance)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. ENUMS
CREATE TYPE user_role_enum AS ENUM ('owner', 'renter', 'operator', 'mechanic');
CREATE TYPE machine_status_enum AS ENUM ('available', 'rented', 'maintenance');
CREATE TYPE labor_status_enum AS ENUM ('available', 'busy', 'offline');
CREATE TYPE booking_status_enum AS ENUM ('pending', 'approved', 'rejected', 'completed', 'cancelled');

-- 2. USERS TABLE (Linked to auth.users in Supabase)
CREATE TABLE public.users (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    city VARCHAR(60) NOT NULL, -- e.g., 'Casablanca', 'Kenitra', 'Tanger'
    role user_role_enum NOT NULL DEFAULT 'renter',
    company_name VARCHAR(150),
    avatar_url TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. MACHINES TABLE
CREATE TABLE public.machines (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    title VARCHAR(180) NOT NULL,
    category VARCHAR(60) NOT NULL, -- 'excavator', 'bulldozer', 'crane', 'loader', etc.
    brand_model VARCHAR(80) NOT NULL, -- e.g., 'Caterpillar 320D', 'Komatsu PC210'
    daily_rate NUMERIC(10, 2) NOT NULL CHECK (daily_rate > 0), -- MAD / Day
    location VARCHAR(200) NOT NULL,
    city VARCHAR(60) NOT NULL, -- Indexed for fast Moroccan regional filtering
    images TEXT[] NOT NULL DEFAULT '{}',
    status machine_status_enum NOT NULL DEFAULT 'available',
    is_featured BOOLEAN NOT NULL DEFAULT FALSE, -- Sponsored / Boosted listings
    featured_until TIMESTAMPTZ,
    has_operator BOOLEAN NOT NULL DEFAULT TRUE,
    year_of_make INT CHECK (year_of_make BETWEEN 1990 AND 2030),
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 4. LABOR_PROFILES TABLE (Specialized for Moroccan operators & mechanics)
CREATE TABLE public.labor_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES public.users(id) ON DELETE CASCADE,
    role user_role_enum NOT NULL CHECK (role IN ('operator', 'mechanic')),
    skills TEXT NOT NULL, -- e.g., 'CACES Cat 2 & 4, High precision grading'
    experience_years INT NOT NULL DEFAULT 1 CHECK (experience_years >= 0),
    daily_rate NUMERIC(10, 2) NOT NULL CHECK (daily_rate >= 0), -- MAD / Day
    city VARCHAR(60) NOT NULL,
    status labor_status_enum NOT NULL DEFAULT 'available',
    certified_caces BOOLEAN NOT NULL DEFAULT FALSE,
    emergency_callout BOOLEAN NOT NULL DEFAULT FALSE,
    rating NUMERIC(3, 2) NOT NULL DEFAULT 5.0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 5. BOOKINGS / RENTAL REQUESTS TABLE
CREATE TABLE public.bookings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    machine_id UUID NOT NULL REFERENCES public.machines(id) ON DELETE RESTRICT,
    renter_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_count INT NOT NULL CHECK (days_count > 0),
    daily_rate NUMERIC(10, 2) NOT NULL,
    total_price NUMERIC(12, 2) NOT NULL CHECK (total_price > 0), -- In MAD
    status booking_status_enum NOT NULL DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT valid_date_range CHECK (end_date >= start_date)
);

-- 6. INDUSTRIAL B2B SPONSORED ADS TABLE
CREATE TABLE public.sponsored_ads (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sponsor_name VARCHAR(120) NOT NULL,
    category VARCHAR(60) NOT NULL, -- 'spare_parts', 'lubricants', 'insurance'
    title VARCHAR(200) NOT NULL,
    subtitle VARCHAR(300) NOT NULL,
    cta_text VARCHAR(60) NOT NULL,
    contact_phone VARCHAR(30) NOT NULL,
    banner_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =========================================================================
-- HIGH-PERFORMANCE INDEXES FOR RAPID CITY & CATEGORY FILTERING
-- =========================================================================
CREATE INDEX idx_machines_city_category ON public.machines(city, category);
CREATE INDEX idx_machines_featured ON public.machines(is_featured DESC, created_at DESC);
CREATE INDEX idx_machines_status ON public.machines(status);
CREATE INDEX idx_labor_city_role ON public.labor_profiles(city, role);
CREATE INDEX idx_bookings_renter ON public.bookings(renter_id, created_at DESC);
CREATE INDEX idx_bookings_machine ON public.bookings(machine_id, status);

-- =========================================================================
-- SUPABASE REAL-TIME BROADCAST CONFIGURATION
-- =========================================================================
ALTER PUBLICATION supabase_realtime ADD TABLE public.machines;
ALTER PUBLICATION supabase_realtime ADD TABLE public.labor_profiles;
ALTER PUBLICATION supabase_realtime ADD TABLE public.bookings;

-- =========================================================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- =========================================================================
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.machines ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.labor_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.bookings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.sponsored_ads ENABLE ROW LEVEL SECURITY;

-- 1. Users policies
CREATE POLICY "Public users can view profiles" 
    ON public.users FOR SELECT USING (true);
CREATE POLICY "Users can update their own profile" 
    ON public.users FOR UPDATE USING (auth.uid() = id);

-- 2. Machines policies
CREATE POLICY "Anyone can view available machines" 
    ON public.machines FOR SELECT USING (true);
CREATE POLICY "Owners can insert their machines" 
    ON public.machines FOR INSERT WITH CHECK (auth.uid() = owner_id);
CREATE POLICY "Owners can update their own machines" 
    ON public.machines FOR UPDATE USING (auth.uid() = owner_id);
CREATE POLICY "Owners can delete their own machines" 
    ON public.machines FOR DELETE USING (auth.uid() = owner_id);

-- 3. Labor Profiles policies
CREATE POLICY "Anyone can view labor profiles" 
    ON public.labor_profiles FOR SELECT USING (true);
CREATE POLICY "Laborers can manage their own profile" 
    ON public.labor_profiles FOR ALL USING (auth.uid() = user_id);

-- 4. Bookings policies
CREATE POLICY "Renters can view their bookings" 
    ON public.bookings FOR SELECT USING (auth.uid() = renter_id);
CREATE POLICY "Owners can view bookings for their machines" 
    ON public.bookings FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.machines 
            WHERE public.machines.id = public.bookings.machine_id 
              AND public.machines.owner_id = auth.uid()
        )
    );
CREATE POLICY "Renters can create bookings" 
    ON public.bookings FOR INSERT WITH CHECK (auth.uid() = renter_id);
CREATE POLICY "Owners and renters can update booking status" 
    ON public.bookings FOR UPDATE USING (
        auth.uid() = renter_id OR EXISTS (
            SELECT 1 FROM public.machines 
            WHERE public.machines.id = public.bookings.machine_id 
              AND public.machines.owner_id = auth.uid()
        )
    );

-- 5. Sponsored ads policies (Public Read, Admin Write)
CREATE POLICY "Anyone can view active ads" 
    ON public.sponsored_ads FOR SELECT USING (is_active = true);
"""

    const val FIRESTORE_RULES = """
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Helper functions
    function isAuthenticated() {
      return request.auth != null;
    }
    function isOwner(userId) {
      return isAuthenticated() && request.auth.uid == userId;
    }

    // 1. Users collection
    match /users/{userId} {
      allow read: if true;
      allow write: if isOwner(userId);
    }

    // 2. Machines collection (Engins Maroc listings)
    match /machines/{machineId} {
      // Anyone can search and view machinery in Morocco
      allow read: if true;
      
      // Verified machinery owners can publish new listings
      allow create: if isAuthenticated() 
        && request.resource.data.dailyRate is number 
        && request.resource.data.dailyRate > 0
        && request.resource.data.city is string;

      // Only the listing owner can update or delete their machine
      allow update, delete: if isAuthenticated() 
        && (resource.data.ownerId == request.auth.uid || request.resource.data.ownerId == request.auth.uid);
    }

    // 3. Bookings collection
    match /bookings/{bookingId} {
      allow read: if isAuthenticated() && (
        request.auth.uid == resource.data.renterId || 
        request.auth.uid == resource.data.ownerId
      );
      allow create: if isAuthenticated() && request.auth.uid == request.resource.data.renterId;
      allow update: if isAuthenticated() && (
        request.auth.uid == resource.data.renterId || 
        request.auth.uid == resource.data.ownerId
      );
    }
  }
}
"""

    const val ROOM_FIRESTORE_SYNC_GUIDE = """
🔄 ROOM-TO-FIRESTORE SYNCHRONIZATION PATTERN (OFFLINE-FIRST SSOT)

1. SINGLE SOURCE OF TRUTH (SSOT):
   - Room Database is the sole source of truth for the Compose UI.
   - The UI observes Room via reactive Kotlin Flows (e.g. `dao.getAllMachinesFlow()`).
   - Mutations (add, edit, promote) occur instantaneously in Room, providing sub-16ms UI updates even in remote Moroccan construction sites with zero network coverage.

2. BIDIRECTIONAL SYNC ENGINE:
   - Push (Local -> Cloud):
     • When a machine is created, Room sets `syncStatus = PENDING_UPLOAD`.
     • `FirestoreMachineSyncManager` pushes the document to `/machines/{id}`.
     • On Firestore acknowledgement, Room updates `firestoreId`, `syncStatus = SYNCED`, and `lastSyncedAt = now()`.
   - Pull & Real-time Broadcasting (Cloud -> Local):
     • A Firestore `SnapshotListener` listens to the `/machines` collection.
     • Incoming snapshot events are reconciled into Room via `dao.updateMachine()` or `dao.insertMachine()`.
     • Any new equipment listed in Casablanca, Tangier, or Agadir reflects immediately on all connected devices.

3. CONFLICT RESOLUTION:
   - Uses Last-Write-Wins (LWW) based on `updatedAt` timestamps.
   - If local state has `PENDING_UPDATE` with a newer timestamp than the incoming cloud snapshot, local changes take precedence and will re-push to Firestore.
"""

    const val ARCHITECTURE_OVERVIEW = """
🏗️ MOROCCAN CONSTRUCTION TWO-SIDED MARKETPLACE ARCHITECTURE

1. LOCALIZED MOROCCAN HYBRID LANGUAGE MODEL:
   - Modern Standard Arabic (MSA / الفصحى): For legal conditions, formal field labels, financial receipts, navigation bar labels, and error messages.
   - Moroccan Darija (الدارجة المغربية): For primary CTAs, micro-copy, status chips, and vernacular terminology ('شيفور', 'ماجورة / آلة حفر', 'بيلدوزر', 'اكري آلة', 'طلب الخدمة', 'مول الماتريال', 'كراي', 'عيّط دابا').
   - Full Right-to-Left (RTL) Layout Direction across all layouts and motion choreography.

2. REAL-TIME SYNCHRONIZATION ENGINE:
   - Local Layer: Room Database providing reactive Flow streams on Android devices with immediate optimistic mutations.
   - Cloud Layer: Supabase Realtime (PostgreSQL CDC via WebSockets) or Firebase Firestore real-time listeners.
   - Broadcasting: When any owner publishes a machine, PostgreSQL triggers emit to the 'supabase_realtime' publication, broadcasting to all subscribed Android clients in < 80ms.

3. MONETIZATION & B2B SPONSORED ADS:
   - Boosted / Promoted Listings (الإعلانات المميزة): Machinery owners pay a small daily fee (e.g. 50 MAD / day) to pin their equipment to the top of search results with a gold badge.
   - B2B Industrial Ads: High-value non-intrusive native cards for spare parts providers (CAT, Komatsu, JCB), fuel & heavy lubricants (Shell, TotalEnergies), heavy transport (porte-char), and machinery insurance (RMA, Wafa Assurance).

4. DIRECT LOCAL MOROCCAN COMMUNICATION:
   - Deep integration with WhatsApp API (Morocco's de facto B2B communication channel) pre-filled with equipment details and Darija greetings.
   - Direct GSM phone dialer integration for urgent job site coordination.
"""
}
