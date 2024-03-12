package com.example.professorjiujitsuadmin.activity.FormLogin.Fragments.comprovantes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.professorjiujitsuadmin.Adapter.MensalidadePagaAdapter
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.activity.FormLogin.TelaPrincipal
import com.example.professorjiujitsuadmin.databinding.FragmentComprovanteBinding
import com.example.professorjiujitsuadmin.model.ComprovantesPagamento


class ComprovanteFragment : Fragment() {

    private var _binding: FragmentComprovanteBinding? = null
    lateinit var mensalidadePagaAdapter: MensalidadePagaAdapter
    private val db = DB()
    private val lista_comprovantes:MutableList<ComprovantesPagamento> = mutableListOf()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val comprovanteViewModel =
            ViewModelProvider(this).get(ComprovanteViewModel::class.java)

        _binding = FragmentComprovanteBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var recyclerComprovantes = binding.recyclerComprovantes
        recyclerComprovantes.layoutManager = LinearLayoutManager(context)
        recyclerComprovantes.setHasFixedSize(true)

        mensalidadePagaAdapter = MensalidadePagaAdapter(requireContext(), lista_comprovantes)
        recyclerComprovantes.adapter = mensalidadePagaAdapter

        Log.d("Nao listou", "nao listado ${lista_comprovantes}")
        db.obterDocumentosColecaoSimples()
        db.obterListaMensalidadePaga(lista_comprovantes, mensalidadePagaAdapter){quantidade, totalPago ->
            binding.txtQuantComprovantes.text = "Quantidade de comprovantes: $quantidade"
            binding.txtValorTotal.text = "Total pago R$ : $totalPago"
            Log.d("Nao listou", "nao listado ${lista_comprovantes}")

        }

        (activity as TelaPrincipal)?.setMensalidadePagaAdapter(mensalidadePagaAdapter)




    }


}