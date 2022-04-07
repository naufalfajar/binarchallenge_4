package id.nphew.binar.challenge4.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface AccountDao {
    @Query("SELECT * FROM Account")
    fun getAllAccount() : List<Account>

    @Query("select * from Account where email = :email")
    fun checkEmailAccount(email: String) : List<Account>

    @Query("select password from Account where email = :email")
    fun getPassword(email: String?) : String

    @Query("select username from Account where email = :email")
    fun getUsername(email: String?) : String

    @Query("select id from Account where email = :email")
    fun getId(email: String?) : Int

    @Insert(onConflict = REPLACE)
    fun insertAccount(account: Account) : Long

    @Update
    fun updateAccount(account: Account) : Int

    @Delete
    fun deleteAccount(account: Account) : Int
}
