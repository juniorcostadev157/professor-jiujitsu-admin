package com.example.professorjiujitsuadmin.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.EditarMensalidade.EditarMensalidade
import com.example.professorjiujitsuadmin.databinding.MensalidadePendenteItemBinding
import com.example.professorjiujitsuadmin.model.Mensalidades

class MensalidadesAdapter(private val context: Context, private val lista_mensalidade:MutableList<Mensalidades>)
    : RecyclerView.Adapter<MensalidadesAdapter.MensalidadesViewHolder>() {

    val selectedMensalidade = HashSet<String>()
    private var originalList = lista_mensalidade // Cria uma cópia não-mutável da lista original

    private var currentList = lista_mensalidade

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensalidadesViewHolder {
        val item_lista = MensalidadePendenteItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MensalidadesViewHolder(item_lista)
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: MensalidadesViewHolder, position: Int) {
        val mensalidade = currentList[position]
        holder.titulo_cobranca.text = mensalidade.titulo_cobranca
        holder.nomeAluno.text = mensalidade.nomeAluno
        holder.preco.text = "R$: ${mensalidade.preco}"
        holder.data.text = mensalidade.data
        Glide.with(context).load(mensalidade.foto).into(holder.fotoCobranca)

        val isAlunoSelected = mensalidade.id?.let { id ->
            selectedMensalidade.contains(id)
        } ?: false

        holder.checkMensalidade.isChecked = isAlunoSelected
        holder.checkMensalidade.setOnCheckedChangeListener { _, isChecked ->
            mensalidade.id?.let { id ->
                if (isChecked) {
                    selectedMensalidade.add(id)
                } else {
                    selectedMensalidade.remove(id)
                }
            }
        }

        holder.imageEditar.setOnClickListener {
            val intent = Intent(context, EditarMensalidade::class.java)
            intent.putExtra("titulo_cobranca", mensalidade.titulo_cobranca )
            intent.putExtra("preco", mensalidade.preco)
            intent.putExtra("data", mensalidade.data)
            intent.putExtra("idMensalidade", mensalidade.id)
            intent.putExtra("alunoID", mensalidade.alunoId)
            intent.putExtra("foto", mensalidade.foto)

            context.startActivity(intent)
           }


        holder.imageDeletar.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Excluir cobrança")
            alertDialog.setMessage("Deseja excluir essa cobrança?")
            alertDialog.setPositiveButton("Sim") { _, _ ->

                val db = DB()
                db.deletarMensalidade(
                    alunoId = mensalidade.alunoId ?:"",  // Certifique-se de que o modelo Mensalidades tem um campo 'alunoId'
                    mensalidadeId = mensalidade.id ?: "",
                    onSuccess = {
                        currentList.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, "Cobrança excluída com sucesso!", Toast.LENGTH_LONG).show()
                    },
                    onError = { e ->


                        Toast.makeText(context,"Erro ao excluir cobrança: ${e.message}", Toast.LENGTH_LONG ).show()
                    }
                )
            }
            alertDialog.setNegativeButton("Não", null)
            alertDialog.show()
        }
    }
    inner class  MensalidadesViewHolder(binding: MensalidadePendenteItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val titulo_cobranca = binding.txtTituloCobranca
        val nomeAluno = binding.txtNomeAluno
        val data = binding.txtMesAno
        val preco = binding.txtPreco
        val checkMensalidade = binding.checkBoxMensalidade
        val imageEditar = binding.imageEditar
        val imageDeletar = binding.imageDeletar
        val fotoCobranca = binding.imageCobranca

    }
    fun selectAll(isChecked: Boolean) {
        if (isChecked) {
            selectedMensalidade.addAll(currentList.mapNotNull { it.id })
        } else {
            selectedMensalidade.clear()
        }
        notifyDataSetChanged()
    }

    fun getSelectedMensalidadeMap(): Map<String, List<String>> {
        // Cria um mapa para associar cada aluno às suas mensalidades selecionadas
        val map = mutableMapOf<String, MutableList<String>>()
        selectedMensalidade.forEach { mensalidadeId ->
            lista_mensalidade.find { it.id == mensalidadeId }?.let { mensalidade ->
                if (mensalidade.alunoId != null) {
                    if (!map.containsKey(mensalidade.alunoId)) {
                        map[mensalidade.alunoId!!] = mutableListOf()
                    }
                    map[mensalidade.alunoId]?.add(mensalidadeId)
                }
            }
        }
        return map
    }


    fun filter(text: String?) {
        Log.d("MensalidadesAdapter", "Tamanho da currentList após filtragem: ${currentList.size}")

        Log.d("MensalidadesAdapter", "Iniciando filtragem para: $text")
        currentList = if (text.isNullOrEmpty()) {
            Log.d("MensalidadesAdapter", "Tamanho da currentList após filtragem: ${currentList.size}")

            Log.d("MensalidadesAdapter", "Texto de busca vazio ou nulo")
            originalList.toMutableList() // Retorna a lista original se o texto de busca for nulo ou vazio
        } else {
            val filteredList = originalList.filter { mensalidade ->
                (mensalidade.data?.contains(text, ignoreCase = true) ?: false) ||
                        (mensalidade.nomeAluno?.contains(text, ignoreCase = true) ?: false)
            }.toMutableList()
            Log.d("MensalidadesAdapter", "Tamanho da currentList após filtragem: ${currentList.size}")
            Log.d("MensalidadeAdapter", "Filtrando por: " + text);
            Log.d("MensalidadesAdapter", "Mensalidades filtradas: ${filteredList.size}")
            filteredList

        }
        notifyDataSetChanged()
    }
}