package com.example.professorjiujitsuadmin.DetalhesAluno

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.GraduarAluno.GraduarAluno
import com.example.professorjiujitsuadmin.R
import com.example.professorjiujitsuadmin.ToastPersonalizado.ToastPersonalizado
import com.example.professorjiujitsuadmin.databinding.ActivityDetalhesAlunoBinding
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class DetalhesAluno : AppCompatActivity() {

    lateinit var binding: ActivityDetalhesAlunoBinding
    private var db = DB()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesAlunoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val nome = intent.extras!!.getString("nome")
        val faixa = intent.extras!!.getString("faixa").toString()
        val graus = intent.extras!!.getString("grau").toString()
        val foto = intent.extras!!.getString("foto")
        val restricao_medica = intent.extras!!.getString("descricao_medica")
        val alunoID = intent.extras!!.getString("alunoID")
        val idade = intent.extras!!.getString("idade")
        Glide.with(this).load(foto).into(binding.imagePerfil)

        getGaixa(faixa, graus)
        binding.txtNomeAluno.setText(nome)
        binding.txtFaixaPerfil.text = "Faixa: ${faixa}"
        binding.txtGraus.text = "Graus:${graus}"
        binding.txtidade.setText(idade)
        binding.txtDescricaoMedica.setText(restricao_medica)


        binding.buttonGraduar.setOnClickListener {

            val intent = Intent(this, GraduarAluno::class.java)
            intent.putExtra("faixa", faixa)
            intent.putExtra("graus", graus)
            intent.putExtra("alunoID", alunoID)
            intent.putExtra("idade", idade)

            startActivity(intent)
            finish()

        }


        binding.imageButtonDeletar.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Deletar Aluno")
            alertDialog.setMessage("Deseja excluir aluno ? ")
            alertDialog.setPositiveButton("Sim"){_,_,->
                val functions = Firebase.functions
                val data = hashMapOf("uid" to alunoID)

                functions.getHttpsCallable("deleteUserAndData")
                    .call(data)
                    .addOnSuccessListener {
                        ToastPersonalizado.showToast(this, "Aluno deletado com sucesso")
                        finish()
                        Log.d("Admin", "Usuário deletado com sucesso.")
                        }
                    .addOnFailureListener {
                        Log.e("Admin", "Erro ao deletar usuário: ${it.message}")
                    }

            }
            alertDialog.setNegativeButton("Não", null)
            alertDialog.show()


        }



    }

    private fun getGaixa(faixa:String,grau:String){
        when (faixa) {
            "Branca" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_branca)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_branca_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_branca_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_branca_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_branca_4graus)
                }
            }

            "Azul" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_azul)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_azul_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_azul_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_azul_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_branca_4graus)
                }
            }

            "Roxa" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_roxa)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_roxa_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_roxa_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_roxa_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_roxa_4graus)
                }
            }

            "Marrom" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_marrom)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_marrom_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_marrom_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_marrom_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_marrom_4graus)
                }
            }

            "Preta" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_preta_jiujitsu)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_preta_jiujitsu)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_preta_jiujitsu)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_preta_jiujitsu)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_preta_jiujitsu)
                }

            }
            "Cinza" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_4graus)
                }

            }
            "Cinza-Branca" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_branca)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_branca_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_branca_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_branca_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_branca_4graus)
                }

            }
            "Cinza-Preta" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_preta)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_preta_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_preta_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_preta_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_cinza_preta_4graus)
                }

            }

            "Amarela" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_4graus)
                }

            }
            "Amarela-Branca" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_branca)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_branca_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_branca_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_branca_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_branca_4graus)
                }

            }
            "Amarela-Preta" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_preta)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_preta_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_preta_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_preta_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_amarela_preta_4graus)
                }

            }
            "Laranja" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_4graus)
                }

            }
            "Laranja-Branca" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_branca)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_branca_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_branca_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_branca_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranka_branca_4graus)
                }

            }
            "Laranja-Preta" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_preta)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_preta_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_preta_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_preta_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_laranja_preta_4graus)
                }

            }
            "Verde" -> {
            when (grau) {
                "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde)
                "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_1grau)
                "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_2graus)
                "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_3graus)
                "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_4graus)
            }

        }
            "Verde-Branca" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_branca)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_branca_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_branca_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_branca_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_branca_4graus)
                }

            }
            "Verde-Preta" -> {
                when (grau) {
                    "0" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_preta)
                    "1" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_preta_1grau)
                    "2" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_preta_2graus)
                    "3" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_preta_3graus)
                    "4" -> binding.imageFaixa.setImageResource(R.drawable.faixa_verde_preta_4graus)
                }

            }
        }
    }
}