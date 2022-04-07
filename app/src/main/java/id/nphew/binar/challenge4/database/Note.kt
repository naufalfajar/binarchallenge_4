package id.nphew.binar.challenge4.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "account_id") val accountId: Int?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "note") val note: String
)
