package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookingEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.LaborProfileEntity
import com.example.data.model.MachineEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketplaceDao {

    // --- Machines ---
    @Query("SELECT * FROM machines ORDER BY isFeatured DESC, createdAt DESC")
    fun getAllMachinesFlow(): Flow<List<MachineEntity>>

    @Query("""
        SELECT * FROM machines 
        WHERE (:city = 'الكل بالمغرب' OR city = :city)
          AND (:category = 'all' OR category = :category)
        ORDER BY isFeatured DESC, createdAt DESC
    """)
    fun getMachinesFiltered(city: String, category: String): Flow<List<MachineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMachine(machine: MachineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMachines(machines: List<MachineEntity>)

    @Update
    suspend fun updateMachine(machine: MachineEntity)

    @Query("UPDATE machines SET isFeatured = :isFeatured WHERE id = :id")
    suspend fun setMachineFeatured(id: Long, isFeatured: Boolean)

    @Query("SELECT COUNT(*) FROM machines")
    suspend fun getMachinesCount(): Int

    @Query("SELECT * FROM machines WHERE id = :id LIMIT 1")
    suspend fun getMachineById(id: Long): MachineEntity?

    @Query("SELECT * FROM machines WHERE firestoreId = :firestoreId LIMIT 1")
    suspend fun getMachineByFirestoreId(firestoreId: String): MachineEntity?

    @Query("SELECT * FROM machines WHERE syncStatus != 'SYNCED'")
    suspend fun getUnsyncedMachines(): List<MachineEntity>

    @Query("UPDATE machines SET firestoreId = :firestoreId, syncStatus = :syncStatus, lastSyncedAt = :syncedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateMachineSyncInfo(id: Long, firestoreId: String, syncStatus: String, syncedAt: Long, updatedAt: Long)

    @Query("UPDATE machines SET syncStatus = :syncStatus, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markMachinePending(id: Long, syncStatus: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM machines WHERE firestoreId = :firestoreId")
    suspend fun deleteMachineByFirestoreId(firestoreId: String)

    // --- Labor Profiles (Operators & Mechanics) ---
    @Query("SELECT * FROM labor_profiles ORDER BY rating DESC, completedJobs DESC")
    fun getAllLaborProfilesFlow(): Flow<List<LaborProfileEntity>>

    @Query("""
        SELECT * FROM labor_profiles 
        WHERE (:city = 'الكل بالمغرب' OR city = :city)
          AND (:role = 'all' OR role = :role)
        ORDER BY rating DESC, completedJobs DESC
    """)
    fun getLaborFiltered(city: String, role: String): Flow<List<LaborProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaborProfiles(profiles: List<LaborProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLaborProfile(profile: LaborProfileEntity): Long

    @Query("SELECT COUNT(*) FROM labor_profiles")
    suspend fun getLaborCount(): Int

    // --- Bookings / Rental Requests ---
    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookingsFlow(): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Long, status: String)

    // --- Chat & Direct Messaging ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE machineId = :machineId ORDER BY timestamp ASC")
    fun getMessagesForMachine(machineId: Long): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    // --- Users ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    fun getActiveUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: String)

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}
