package com.example.professorjiujitsuadmin.GraduarAluno

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.databinding.ActivityGraduarAlunoBinding

class GraduarAluno : AppCompatActivity() {
    lateinit var binding: ActivityGraduarAlunoBinding
    private var db = DB()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraduarAlunoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val faixa = intent.extras!!.getString("faixa")
        val grau = intent.extras!!.getString("graus")
        val alunoID = intent.extras!!.getString("alunoID")
        val idade = intent.extras!!.getString("idade")



        binding.editFaixaCadastro.setText(faixa)
        binding.editGrauCadastro.setText(grau)


        if (idade != null) {
            atualizarFaixasComBaseNaIdade(idade)
        }




        var sugestaoGrau = arrayOf("0", "1" , "2" , "3", "4")
        var adapterGrau =  object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            sugestaoGrau
        ) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        return FilterResults().apply {
                            values = sugestaoGrau
                            count = sugestaoGrau.size
                        }
                    }

                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        if (results != null && results.count > 0) {
                            notifyDataSetChanged()
                        } else {
                            notifyDataSetInvalidated()
                        }
                    }
                }
            }
        }

        binding.editGrauCadastro.apply {
            setAdapter(adapterGrau)
            threshold = 0
            onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->

            }
            setOnClickListener {
                requestFocus()
                showDropDown()
            }

            inputType = InputType.TYPE_NULL
        }

        binding.buttonSalvarAlteracoes.setOnClickListener {
             val novaFaixa = binding.editFaixaCadastro.text.toString()
            val novoGrau = binding.editGrauCadastro.text.toString()

            if (novaFaixa.isNotEmpty() && novoGrau.isNotEmpty()){

                db.graduarAluno(novaFaixa!!, novoGrau!!, alunoID!!)

                Toast.makeText(this, "Aluno graduado com sucesso", Toast.LENGTH_LONG).show()
                finish()


            }else{

                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun atualizarFaixasComBaseNaIdade(idade: String) {
        val faixas = when (idade) {
            "04-10 anos" -> arrayOf("Branca", "Cinza", "Cinza-Branca", "Cinza-Preta", "Amarela", "Amarela-Branca", "Amarela-Preto")
            "10-15 anos" -> arrayOf("Branca", "Laranja", "Laranja-Branca", "Laranja-Preta", "Verde", "Verde-Branca", "Verde-Preto")
            "15-18 anos", "Adulto" -> arrayOf("Branca", "Azul", "Roxa", "Marrom", "Preta")
            else -> arrayOf() // Opção padrão vazia para lidar com valores inesperados
        }
        val adapterFaixa = ArrayAdapter(this, android.R.layout.simple_list_item_1, faixas)
        configurarAutoCompleteTextView(binding.editFaixaCadastro, adapterFaixa)
        configurarAdapterFaixa(faixas)
    }
    private fun configurarAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView, adapter: ArrayAdapter<String>) {


        autoCompleteTextView.apply {
            setAdapter(adapter)
            threshold = 0
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showDropDown()
            }
            setOnClickListener {
                if (!isPopupShowing) {
                    requestFocus()
                    showDropDown()
                }
            }
            inputType = InputType.TYPE_NULL
        }

    }
    private fun configurarAdapterFaixa(faixas: Array<String>) {
        val adapterFaixa = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            faixas
        ) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        return FilterResults().apply {
                            values = faixas // Retorne a lista completa de faixas
                            count = faixas.size
                        }
                    }


                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        if (results != null && results.count > 0) {
                            clear()
                            val faixasFiltradas = results.values as? Array<String>
                            if (faixasFiltradas != null) {
                                for (faixa in faixasFiltradas) {
                                    add(faixa)
                                }
                            }
                            notifyDataSetChanged()
                        } else {
                            notifyDataSetInvalidated()
                        }
                    }


                }
            }
        }

        binding.editFaixaCadastro.setAdapter(adapterFaixa)
    }

}