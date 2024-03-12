package com.example.professorjiujitsuadmin.activity.FormLogin.Fragments.alunos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.professorjiujitsuadmin.Adapter.AlunosAdapter
import com.example.professorjiujitsuadmin.CadastrarMensalidade.CadastrarMensalidade
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.activity.FormLogin.TelaPrincipal
import com.example.professorjiujitsuadmin.databinding.FragmentAlunosBinding
import com.example.professorjiujitsuadmin.model.Alunos

class AlunosFragment : Fragment() {

    private var _binding: FragmentAlunosBinding? = null
    private lateinit var alunosAdapter: AlunosAdapter
    private val db = DB()
    private val lista_alunos:MutableList<Alunos> = mutableListOf()


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(AlunosViewModel::class.java)

        _binding = FragmentAlunosBinding.inflate(inflater, container, false)
        val root: View = binding.root


        homeViewModel.text.observe(viewLifecycleOwner) {

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerAlunos = binding.recyclerAlunos
        recyclerAlunos.layoutManager = LinearLayoutManager(context)
        recyclerAlunos.setHasFixedSize(true)

        alunosAdapter = AlunosAdapter(requireContext(), lista_alunos)
        recyclerAlunos.adapter = alunosAdapter

        // Atualiza o método de obterListaAlunos para incluir o callback de contagem de alunos
        db.obterListaAlunos(lista_alunos, alunosAdapter) { quantidade ->
            Log.d("AlunosFragment", "Quantidade de alunos: $quantidade")
            Log.d("AlunosFragment", "Lista de alunos: $lista_alunos")
            binding.txtQuantidadeAlunos.text = quantidade.toString() // Atualiza o TextView com a quantidade
        }

        (activity as? TelaPrincipal)?.setAlunosAdapter(alunosAdapter)

        binding.checkBoxSelecionarTodos.setOnCheckedChangeListener { _, isChecked ->
            alunosAdapter.selectAll(isChecked)
        }




    }

    fun cadastrarMensalidadeParaAlunosSelecionados() {
        val selectedIds = alunosAdapter.getSelectedAlunos().toList()
        val selectedEmails = alunosAdapter.getSelectedEmails().toList()
        if (selectedIds.isNotEmpty()) {
            val intent = Intent(context, CadastrarMensalidade::class.java).apply {
                putExtra("selectedIds", ArrayList<String>(selectedIds))
                putExtra("selectedEmails", ArrayList<String>(selectedEmails))
            }
            startActivity(intent)
        } else {

            Toast.makeText(requireContext(),"Nenhum aluno selecionado." , Toast.LENGTH_LONG).show()

        }
    }



}