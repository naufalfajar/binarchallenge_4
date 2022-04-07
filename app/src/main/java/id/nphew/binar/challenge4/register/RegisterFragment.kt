package id.nphew.binar.challenge4.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import id.nphew.binar.challenge4.database.Account
import id.nphew.binar.challenge4.database.AccountDatabase
import id.nphew.binar.challenge4.databinding.FragmentRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var accountDb: AccountDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountDb = AccountDatabase.getInstance(requireContext())

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
            else {
                CoroutineScope(Dispatchers.Main).launch {
                    val stat = isDbEmailExist(email)
                    if(stat){
                        if(isAdded)
                            createToast("Email sudah terdaftar!").show()
                    }
                    else{
                        saveToDb(username, email, password)
                        if(isAdded)
                            it.findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                    }
                }
            }
        }
    }

    private fun saveToDb(username: String, email: String, password: String) {
        val account = Account(null,username, email, password)
        CoroutineScope(Dispatchers.IO).launch {
            val result = accountDb?.accountDao()?.insertAccount(account)
            if (result != 0L) {
                CoroutineScope(Dispatchers.Main).launch {
                    if(isAdded)
                        createToast("Register Success").show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    if(isAdded)
                        createToast("Register Failed").show()
                }
            }
        }
    }

    private suspend fun isDbEmailExist(email: String): Boolean = withContext(Dispatchers.IO) {
        val emailDb = accountDb?.accountDao()?.checkEmailAccount(email)
        return@withContext !emailDb.isNullOrEmpty()
    }

    private fun createToast(msg: String): Toast {
        return Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG)
    }

}