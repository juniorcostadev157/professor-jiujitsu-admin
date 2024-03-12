package com.example.professorjiujitsuadmin.model

data class Mensalidades(
    val foto: String? = null,
    val data:String? = null,
    val preco:String? = null,
    val status_pagamento:String? = null,
    val titulo_cobranca:String? = null,
    var id:String? = null,
    var alunoId: String? = null,
    var nomeAluno :String? = null
)