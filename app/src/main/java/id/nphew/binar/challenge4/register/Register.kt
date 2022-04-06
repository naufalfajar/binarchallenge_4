package id.nphew.binar.challenge4.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import id.nphew.binar.challenge4.database.Account
import id.nphew.binar.challenge4.database.AccountDatabase
import id.nphew.binar.challenge4.databinding.ActivityRegisterBinding
import id.nphew.binar.challenge4.login.Login
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var accountDb: AccountDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        accountDb = AccountDatabase.getInstance(this)

        registerAccount()
    }

    private fun registerAccount(){
        binding.btnRegisterDaftar.setOnClickListener {
            val username: String ; val email: String
            val password: String ; val confPassword: String
            binding.apply {
                username = etRegisterUsername.text.toString()
                email = etLoginEmail.text.toString()
                password = etLoginPassword.text.toString()
                confPassword = etLoginConfirmpassword.text.toString()
            }

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confPassword.isEmpty())
                createToast("Pastikan Semua Field Terisi").show()
            else if (password != confPassword)
                createToast("Password Tidak Cocok!").show()
//            else if(isDbEmailExist(email)){
//                createToast("Email sudah terdaftar!").show()
//            }
            else {
                saveToDb(username, email, password)
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }

        }
    }
    private fun saveToDb(username: String, email: String, password: String) {
        val account = Account(null,username, email, password)
        CoroutineScope(Dispatchers.IO).launch {
            val emailDb = accountDb?.accountDao()?.checkEmailAccount(email)
            val isEmailExist = !emailDb.isNullOrEmpty()

            if(isEmailExist){
                CoroutineScope(Dispatchers.Main).launch {
                    createToast("Email sudah terdaftar!").show()
                }
            }
            else {
                val result = accountDb?.accountDao()?.insertAccount(account)
                if (result != 0L) {
                    CoroutineScope(Dispatchers.Main).launch {
                        createToast("Register Success").show()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        createToast("Register Failed").show()
                    }
                }
            }
        }
    }

//    private fun isDbEmailExist(email: String): Boolean {
//        var stat: Boolean
//        CoroutineScope(Dispatchers.IO).launch {
//            val emailDb = accountDb?.accountDao()?.checkEmailAccount(email)
//            stat = !email.isNullOrEmpty()
//        }
//        return stat
//    }

    private fun createToast(msg: String): Toast{
        return Toast.makeText(this, msg, Toast.LENGTH_LONG)
    }
}

