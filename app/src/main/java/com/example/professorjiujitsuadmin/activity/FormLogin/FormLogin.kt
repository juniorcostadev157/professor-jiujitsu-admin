package com.example.professorjiujitsuadmin.activity.FormLogin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.professorjiujitsuadmin.ToastPersonalizado.ToastPersonalizado
import com.example.professorjiujitsuadmin.databinding.ActivityFormLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase


class FormLogin : AppCompatActivity() {
    lateinit var binding: ActivityFormLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonEntrar.setOnClickListener {

            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.equals("admin@gmail.com")) {
                autenticacaoLogin(email, senha)

            }

        }
    }





    override fun onResume() {
        super.onResume()
        val email = "admin@gmail.com"
        val usuarioLogado = FirebaseAuth.getInstance().currentUser
        if (usuarioLogado!=null){
            iniciarTelaPrincipalComAdmin()
            addAdminRole(email)
        }
    }

    private fun autenticacaoLogin(email: String, senha: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Após o login bem-sucedido, verifique as claims do token do usuário
                    FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                        ?.addOnCompleteListener { tokenResultTask ->
                            if (tokenResultTask.isSuccessful) {
                                val isAdmin = tokenResultTask.result?.claims?.get("admin") as? Boolean ?: false
                                if (isAdmin) {
                                    // Se o usuário é admin, inicie a TelaPrincipal com privilégios de admin
                                    iniciarTelaPrincipalComAdmin()
                                } else {
                                    // Se não é admin, inicie a TelaPrincipal normalmente
                                    iniciarTelaPrincipal()
                                }
                            } else {
                                // Handle the error
                             //   Toast.makeText(this, "Falha ao verificar privilégios do usuário.", Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    // Se o login não foi bem-sucedido, exiba uma mensagem de erro

                    ToastPersonalizado.showToast(this, "Erro ao fazer Login: ${task.exception?.message}")
                }
            }
    }


    private fun iniciarTelaPrincipalComAdmin() {
        val intent = Intent(this, TelaPrincipal::class.java)
        intent.putExtra("isAdmin", true) // Você pode passar isso como um extra para a TelaPrincipal usar
        startActivity(intent)
        finish()
    }

    private fun iniciarTelaPrincipal() {
        val intent = Intent(this, TelaPrincipal::class.java)
        startActivity(intent)
        finish()
    }

    fun addAdminRole(email: String) {
        // Obter a referência para as funções do Firebase
        val functions = Firebase.functions

        // Prepare os dados a serem passados para a função do Firebase
        val data = hashMapOf("email" to email)

        // Chame a função addAdminRole do Firebase
        functions
            .getHttpsCallable("addAdminRole")
            .call(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Trate o sucesso aqui
                    val result = task.result?.data as Map<String, Any>
                  // Toast.makeText(this, result["message"].toString(), Toast.LENGTH_LONG).show()
                } else {
                    // Trate o erro aqui
                    val exception = task.exception
                   // Toast.makeText(this, "Erro ao atribuir role de admin: ${exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }




}