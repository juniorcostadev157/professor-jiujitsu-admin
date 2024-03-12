package com.example.professorjiujitsuadmin.activity.FormLogin.Fragments.mensalidade

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.professorjiujitsuadmin.Adapter.MensalidadesAdapter
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.ToastPersonalizado.ToastPersonalizado
import com.example.professorjiujitsuadmin.activity.FormLogin.TelaPrincipal
import com.example.professorjiujitsuadmin.databinding.FragmentMensalidadesBinding
import com.example.professorjiujitsuadmin.model.Mensalidades

class MensalidadeFragment : Fragment() {

    lateinit var binding: FragmentMensalidadesBinding
    lateinit var mensalidadesAdapter: MensalidadesAdapter
    private val db = DB()
    private val lista_mensalidade:MutableList<Mensalidades> = mutableListOf()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(MensalidadeViewModel::class.java)

        binding = FragmentMensalidadesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerMensalidades = binding.recyclerMensalidades
        recyclerMensalidades.layoutManager = LinearLayoutManager(context)
        recyclerMensalidades.setHasFixedSize(true)

        mensalidadesAdapter = MensalidadesAdapter(requireContext(), lista_mensalidade)
        recyclerMensalidades.adapter = mensalidadesAdapter

        db.obterListaMensalidade(lista_mensalidade, mensalidadesAdapter){quantidade->
        binding.textQuantidadeMensalidades.text = "Quantidade de mensalidades: " + quantidade.toString()

        }

        (activity as? TelaPrincipal)?.setMensalidadeAdapter(mensalidadesAdapter)

        binding.checkBoxMarcarTodasMensalidades.setOnCheckedChangeListener { _, isChecked ->
            mensalidadesAdapter.selectAll(isChecked)

        }

        binding.buttonExcluirTudo.setOnClickListener {

            val mensalidadesPorAluno = mensalidadesAdapter.getSelectedMensalidadeMap()
            if (mensalidadesPorAluno.isNotEmpty()) {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Excluir cobrança")
            alertDialog.setMessage("Deseja excluir todas as cobrança?")
            alertDialog.setPositiveButton("Sim") { _, _ ->

                    db.deletarMensalidadesTodas(
                        mensalidadesPorAluno = mensalidadesPorAluno,
                        onSuccess = {

                            ToastPersonalizado.showToast(requireContext(),"Cobranças excluídas com sucesso!" )
                             lista_mensalidade.removeAll { mensalidade -> mensalidadesPorAluno[mensalidade.alunoId]?.contains(mensalidade.id) == true }
                            mensalidadesAdapter.notifyDataSetChanged()
                        },
                        onError = { e ->

                            ToastPersonalizado.showToast(requireContext(), "Erro ao excluir cobranças: ${e.message}")
                        }
                    )
                }
                alertDialog.setNegativeButton("Não") {_,_,->}

                alertDialog.show()

            }else {
                  ToastPersonalizado.showToast(requireContext(), "Nenhuma cobrança selecionada")
            }



            }
            }
            }





