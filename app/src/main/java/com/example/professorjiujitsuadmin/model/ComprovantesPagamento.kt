package com.example.professorjiujitsuadmin.model

data class ComprovantesPagamento(
    var data:String? = null,
    var foto:String? = null,
    var id_mensalidade: String? = null,
    var nome:String? = null,
    var nome_pagador:String? = null,
    var preco:String? = null,
    var refMes:String? = null,
    var status_pagamento:String? = null,
    var titulo_cobranca:String? = null,
    var email_aluno:String? =null,
    var alunoID:String? = null
)