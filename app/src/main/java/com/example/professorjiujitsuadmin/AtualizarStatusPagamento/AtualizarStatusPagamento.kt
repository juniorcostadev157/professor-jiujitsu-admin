package com.example.professorjiujitsuadmin.AtualizarStatusPagamento

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.activity.FormLogin.TelaPrincipal
import com.example.professorjiujitsuadmin.databinding.ActivityAtualizarStatusPagamentoBinding

class AtualizarStatusPagamento : AppCompatActivity() {
    lateinit var binding: ActivityAtualizarStatusPagamentoBinding
    private var status_pagamento = "Em verificação"
    private var db=DB()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAtualizarStatusPagamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var nome_pagador = intent.extras!!.getString("nome_pagador")
        var preco = intent.extras!!.getString("preco")
        var data = intent.extras!!.getString("data")
        var foto = intent.extras!!.getString("foto")
        var id_mensalidade = intent.extras!!.getString("id_mensalidade")
        var idAluno = intent.extras!!.getString("alunoID")


        Log.d("idAluno", "o id do aluno é: ${id_mensalidade}")


        binding.txtNomePagador.text = "Pagante: ${nome_pagador}"
        binding.txtPrecoPagamento.text = "R$: ${preco}"
        binding.txtDataPagamento.text = "Data/Hora : ${data}"
        Glide.with(this).load(foto).into(binding.imageView2)


        binding.radioButtonAprovado.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                status_pagamento = "Aprovado"
            }
        }
            binding.radioButtonVerificacao.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {
                    status_pagamento = "Em verificação"

            }

            }
        binding.buttonAtualizarStatusPagamento.setOnClickListener {

            db.atualizarStatusPagamento(status_pagamento, idAluno!!, id_mensalidade!!)

            Toast.makeText(this,"status de pagamento atualizado", Toast.LENGTH_LONG).show()
            finish()
            startActivity(Intent(this, TelaPrincipal::class.java))


        }

    }
}