package com.example.professorjiujitsuadmin.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.professorjiujitsuadmin.AtualizarStatusPagamento.AtualizarStatusPagamento
import com.example.professorjiujitsuadmin.activity.FormLogin.FormLogin
import com.example.professorjiujitsuadmin.databinding.ComprovanteItemBinding
import com.example.professorjiujitsuadmin.model.ComprovantesPagamento

class MensalidadePagaAdapter(private val context: Context, private val lista_comprovantes:MutableList<ComprovantesPagamento>):
    RecyclerView.Adapter<MensalidadePagaAdapter.MensalidadePagaViewHolder>() {

   private var originalLista = lista_comprovantes
    private var currentList = lista_comprovantes


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensalidadePagaViewHolder {
       val lista_item = ComprovanteItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MensalidadePagaViewHolder(lista_item)
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: MensalidadePagaViewHolder, position: Int) {
        val mensalidade_paga = currentList[position]
        holder.titulo_cobranca.text = mensalidade_paga.titulo_cobranca
        holder.nome_aluno.text = mensalidade_paga.nome
        holder.email_aluno.text = mensalidade_paga.email_aluno
        holder.preco.text = "R$: ${mensalidade_paga.preco}"
        holder.status_pagamento.text = mensalidade_paga.status_pagamento
        holder.status_pagamento.text = mensalidade_paga.status_pagamento
        holder.refMes.text = mensalidade_paga.refMes

        if(holder.status_pagamento.text.equals("Aprovado")){
            holder.status_pagamento.setTextColor(Color.parseColor("#023C05"))
        }
        holder.itemView.setOnClickListener {
            val intent =  Intent(context, AtualizarStatusPagamento::class.java)
            intent.putExtra("id_mensalidade", mensalidade_paga.id_mensalidade)
            intent.putExtra("alunoID", mensalidade_paga.alunoID)
            intent.putExtra("nome_pagador", mensalidade_paga.nome_pagador)
            intent.putExtra("foto", mensalidade_paga.foto)
            intent.putExtra("preco", mensalidade_paga.preco)
            intent.putExtra("data", mensalidade_paga.data)

            context.startActivity(intent)

        }

    }

    inner class MensalidadePagaViewHolder(binding: ComprovanteItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val titulo_cobranca = binding.txtTituloCobrancaComprovante
        val nome_aluno = binding.txtNomeAlunoComprovante
        val email_aluno = binding.txtEmailComprovante
        val preco = binding.txtPrecoComprovante
        val status_pagamento = binding.txtStatusPagamento
        val refMes = binding.txtRefMes

    }

    fun filtro(texto:String?){
        currentList = if(texto.isNullOrEmpty()){
            originalLista.toMutableList()
        }else{
            val listaFiltrada = originalLista.filter {comprovante->
                (comprovante.status_pagamento?.contains(texto, ignoreCase = true) ?: false) ||
                (comprovante.email_aluno?.contains(texto, ignoreCase = true) ?:false) ||
                (comprovante.preco?.contains(texto, ignoreCase = true) ?:false)
            }.toMutableList()

            listaFiltrada

        }
        notifyDataSetChanged()
    }
}