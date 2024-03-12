package com.example.professorjiujitsuadmin.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.professorjiujitsuadmin.DetalhesAluno.DetalhesAluno
import com.example.professorjiujitsuadmin.databinding.AlunosItemBinding
import com.example.professorjiujitsuadmin.model.Alunos

class AlunosAdapter(val context: Context, val lista_alunos:MutableList<Alunos>):
    RecyclerView.Adapter<AlunosAdapter.AlunosViewHolder>() {

    private var originalList = lista_alunos // Cria uma cópia não-mutável da lista original
    private var currentList = lista_alunos

    val selectedAlunos = HashSet<String>()
    val selectedEmails = HashSet<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlunosViewHolder {
        val item_lista = AlunosItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return AlunosViewHolder(item_lista)
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: AlunosViewHolder, position: Int) {
        val aluno = currentList[position]
        Log.d("AlunosAdapter", "Binding aluno: ${aluno.nome}")

        Glide.with(context).load(aluno.profileImageUrl).into(holder.foto)
        holder.nome.text = "Nome: ${aluno.nome}"
        holder.faixa.text = "Faixa: ${aluno.faixa}"
        holder.grau.text = "Graus: ${aluno.grau}"

        holder.itemView.setOnClickListener {

            val intent = Intent(context, DetalhesAluno::class.java)
            intent.putExtra("foto", aluno.profileImageUrl)
            intent.putExtra("alunoID", aluno.authID)
            intent.putExtra("nome", aluno.nome)
            intent.putExtra("faixa", aluno.faixa)
            intent.putExtra("grau", aluno.grau)
            intent.putExtra("descricao_medica", aluno.descricao_medica)
            intent.putExtra("idade", aluno.idade)
            intent.putExtra("email", aluno.email)

            context.startActivity(intent)





        }

        val isAlunoSelected = aluno.authID?.let { id ->
            selectedAlunos.contains(id)
        } ?: false

        holder.checkBoxAluno.isChecked = isAlunoSelected
        holder.checkBoxAluno.setOnCheckedChangeListener { _, isChecked ->
            aluno.authID?.let { id ->
                if (isChecked) {
                    selectedAlunos.add(id)
                    aluno.email?.let { email -> // Adiciona o e-mail à lista se estiver disponível
                        selectedEmails.add(email)
                    }
                } else {
                    selectedAlunos.remove(id)
                    aluno.email?.let { email -> // Remove o e-mail da lista
                        selectedEmails.remove(email)
                    }
                }
            }
        }
      }

    inner class AlunosViewHolder(binding: AlunosItemBinding): RecyclerView.ViewHolder(binding.root){
        val foto = binding.imageAluno
        val nome = binding.textNome
        val faixa = binding.textFaixa
        val grau = binding.txtGrau
        val checkBoxAluno = binding.checkBoxAluno
    }

    fun filter(text: String?) {
        currentList = if (text.isNullOrEmpty()) {
            originalList.toMutableList() // Cria uma cópia mutável da lista original
        } else {
            originalList.filter { aluno ->
                (aluno.nome?.contains(text, ignoreCase = true) ?: false) ||
                        (aluno.faixa?.contains(text, ignoreCase = true) ?: false)
            }.toMutableList() // Converte a lista filtrada para MutableList
        }
        notifyDataSetChanged()
    }

    fun selectAll(isChecked: Boolean) {
        if (isChecked) {
            selectedAlunos.addAll(currentList.mapNotNull { it.authID })
        } else {
            selectedAlunos.clear()
        }
        notifyDataSetChanged()
    }

    fun getSelectedAlunos(): Set<String> {
        return selectedAlunos
    }
    fun getSelectedEmails(): Set<String> {
        return selectedEmails
    }



}