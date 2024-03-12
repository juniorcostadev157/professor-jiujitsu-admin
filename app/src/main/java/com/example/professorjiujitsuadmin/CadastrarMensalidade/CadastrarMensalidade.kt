package com.example.professorjiujitsuadmin.CadastrarMensalidade

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.R
import com.example.professorjiujitsuadmin.databinding.ActivityCadastrarMensalidadeBinding

class CadastrarMensalidade : AppCompatActivity() {

    lateinit var binding: ActivityCadastrarMensalidadeBinding
    private var fotoMensalidade:Uri? =null
    private var db = DB()

    private val selecionarFotoGaleria =registerForActivityResult(ActivityResultContracts.GetContent()) setOnClickListener@{ uri->
        if (uri!= null){
            fotoMensalidade = uri
            binding.imageProduto.setImageURI(uri)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastrarMensalidadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedIds = intent.getStringArrayListExtra("selectedIds") ?: arrayListOf()
        val selectedEmails = intent.getStringArrayListExtra("selectedEmails") ?: arrayListOf<String>()

        binding.imageGaleria.setOnClickListener {
            selecionarFotoGaleria.launch("image/*")
        }


        binding.buttonCadastrar.setOnClickListener {
            binding.buttonCadastrar.isEnabled = false
            val preco = binding.editPrecoMensalidade.text.toString()
            val data2 = binding.editDataMensalidade.text.toString()
            val titulo = binding.editTituloCobranca.text.toString()

            if(fotoMensalidade == null) {
                Toast.makeText(this,"Por favor, selecione uma foto para a mensalidade.", Toast.LENGTH_LONG ).show()
                binding.buttonCadastrar.isEnabled = true
                return@setOnClickListener

            }

            if (preco.isNotEmpty() && data2.isNotEmpty() && titulo.isNotEmpty() && selectedIds.isNotEmpty()) {
                selectedIds.forEach { alunoId ->
                    db.cadastrarMensalidadeParaAluno(alunoId, fotoMensalidade!!, preco, data2, titulo, { mensalidadeId ->
                           Toast.makeText(this, "Mensalidade cadastrada com sucesso", Toast.LENGTH_LONG).show()

                        if (selectedEmails.isNotEmpty()) {
                            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {

                                data = Uri.parse("mailto:")

                                putExtra(Intent.EXTRA_EMAIL, selectedEmails.toTypedArray())
                                putExtra(Intent.EXTRA_SUBJECT, "Cobrança da Mensalidade")
                                putExtra(Intent.EXTRA_TEXT, "Mensalidade disponivel para pagamento\n\nno valor de R$: ${preco} \n\nreferente ao mês :${data2} ")

                            }
                            if (emailIntent.resolveActivity(packageManager) != null) {
                                startActivity(Intent.createChooser(emailIntent, "Escolha um aplicativo de e-mail:"))
                            } else {
                                 Toast.makeText(this, "Nenhum aplicativo de email encontrado", Toast.LENGTH_LONG).show()
                            }
                        }
                        finish()




                    }) { exception ->

                        Toast.makeText(this, "Erro ao cadastrar mensalidade", Toast.LENGTH_LONG).show()
                        binding.buttonCadastrar.isEnabled = true
                    }
                }


            } else {

                Toast.makeText(this, "Preencha todos os campos e selecione ao menos um aluno.", Toast.LENGTH_LONG).show()
                binding.buttonCadastrar.isEnabled = true
            }
        }

    }


    private fun toastSucesso(mensagem_toast: String) {
        val view = layoutInflater.inflate(R.layout.toast_customizado_sucesso, null)

        // Encontra o TextView dentro da view inflada
        val mensagem: TextView = view.findViewById(R.id.txtMensagem)
        mensagem.text = mensagem_toast

        val toast = Toast(this)
        toast.view = view
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }





}
