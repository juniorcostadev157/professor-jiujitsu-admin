package com.example.professorjiujitsuadmin.EditarMensalidade

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.ToastPersonalizado.ToastPersonalizado
import com.example.professorjiujitsuadmin.databinding.ActivityEditarMensalidadeBinding
import com.google.android.material.snackbar.Snackbar

class EditarMensalidade : AppCompatActivity() {

    lateinit var binding: ActivityEditarMensalidadeBinding
    private var fotoMensalidade: Uri? =null
    private var db = DB()



    private val selecionarFotoGaleria =registerForActivityResult(ActivityResultContracts.GetContent()) setOnClickListener@{ uri->
        if (uri!= null){
            fotoMensalidade = uri
            binding.imageProdutoEditar.setImageURI(uri)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarMensalidadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titulo_cobranca = intent.extras!!.getString("titulo_cobranca")
        val preco = intent.extras!!.getString("preco")
        val data = intent.extras!!.getString("data")
        val id = intent.extras!!.getString("idMensalidade")
        val alunoID = intent.extras!!.getString("alunoID")
        val foto = intent.extras!!.getString("foto")

        binding.editTituloCobrancaEditar.setText(titulo_cobranca)
        binding.editPrecoMensalidadeEditar.setText(preco)
        binding.editDataMensalidadeEditar.setText(data)
        Glide.with(this).load(foto).into(binding.imageProdutoEditar)

        binding.imageGaleriaEditar.setOnClickListener {
            selecionarFotoGaleria.launch("image/*")
        }

        binding.buttonEditar.setOnClickListener {
            binding.buttonEditar.isEnabled = false

            val tituloEditado = binding.editTituloCobrancaEditar.text.toString()
            val precoEditado = binding.editPrecoMensalidadeEditar.text.toString()
            val dataEditada = binding.editDataMensalidadeEditar.text.toString()



            if(tituloEditado.isNotEmpty() && precoEditado.isNotEmpty() && dataEditada.isNotEmpty() && fotoMensalidade!= null){

                if (alunoID != null && id != null) {
                    db.atualizarMensalidadeComFoto(alunoID, id, tituloEditado, precoEditado, dataEditada, fotoMensalidade!!)
                    Toast.makeText(this, "Atualizado com sucesso", Toast.LENGTH_LONG).show()
                    finish()



                }

            }else if(tituloEditado.isNotEmpty() && precoEditado.isNotEmpty() && dataEditada.isNotEmpty()){
                db.atualizarMensalidadeSemFoto(alunoID!!, id!!, tituloEditado, precoEditado, dataEditada)
                ToastPersonalizado.showToast(this,"Atualizado com sucesso")
                finish()

            }else{
                val snackbar = Snackbar.make(it, "Preencha todos os campos", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()
            }

        }

    }
}