package id.nphew.binar.challenge4.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface AccountDao {
    @Query("SELECT * FROM Account")
    fun getAllAccount() : List<Account>

    @Query("select * from Account where email = :email")
    fun checkEmailAccount(email: String) : List<Account>

    @Insert(onConflict = REPLACE)
    fun insertAccount(account: Account) : Long

    @Update
    fun updateAccount(account: Account) : Int

    @Delete
    fun deleteAccount(account: Account) : Int
}
