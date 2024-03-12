    package com.example.professorjiujitsuadmin.DB

    import android.net.Uri
    import android.util.Log
    import com.example.professorjiujitsuadmin.Adapter.AlunosAdapter
    import com.example.professorjiujitsuadmin.Adapter.MensalidadePagaAdapter
    import com.example.professorjiujitsuadmin.Adapter.MensalidadesAdapter
    import com.example.professorjiujitsuadmin.model.Alunos
    import com.example.professorjiujitsuadmin.model.ComprovantesPagamento
    import com.example.professorjiujitsuadmin.model.Mensalidades
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.SetOptions
    import com.google.firebase.storage.FirebaseStorage

    class DB {

        private var db = FirebaseFirestore.getInstance()
        private var storage = FirebaseStorage.getInstance()


        fun salvarDadosProfessor(nome:String, email:String){

            val id = FirebaseAuth.getInstance().currentUser!!.uid

            val professorRef = db.collection("Professor").document(id)
            val professorData = hashMapOf(
                "id" to id,
                "nome" to nome,
                "email" to email,
                     )

            professorRef.set(professorData).addOnSuccessListener {
                Log.d("Firestore", "Sucesso ao salvar no banco")
            }.addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao salvar no banco", e)
            }



        }

        fun cadastrarMensalidadeParaAluno(
            alunoId: String,
            foto: Uri,
            preco: String,
            data: String,
            tituloCobranca: String,
            onSuccess: (String) -> Unit, // Alterado para passar o ID da mensalidade para o callback
            onError: (Exception) -> Unit
        ) {
            val storageReference =
                storage.getReference("/Mensalidades/$alunoId/${foto.lastPathSegment}")
            storageReference.putFile(foto).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    val mensalidadeMap = hashMapOf(
                        "foto" to uri.toString(),
                        "preco" to preco,
                        "data" to data,
                        "titulo_cobranca" to tituloCobranca,
                        "status_pagamento" to "pendente"
                    )
                    val mensalidadesRef =
                        db.collection("Alunos").document(alunoId).collection("Mensalidades")
                    mensalidadesRef.add(mensalidadeMap).addOnSuccessListener { documentReference ->
                        onSuccess(documentReference.id) // Passa o ID da nova mensalidade para o callback
                    }.addOnFailureListener { e ->
                        onError(e)
                    }
                }
            }.addOnFailureListener { e ->
                onError(e)
            }
        }

        fun obterListaAlunos(
            lista_alunos: MutableList<Alunos>,
            alunosAdapter: AlunosAdapter,
            onResult: (Int) -> Unit
        ) {
            db.collection("Alunos").get().addOnCompleteListener { documento ->
                if (documento.isSuccessful) {
                    lista_alunos.clear() // Limpa a lista para garantir que não haverá duplicatas
                    documento.result?.forEach { doc ->
                        val alunos = doc.toObject(Alunos::class.java)
                        lista_alunos.add(alunos)
                    }
                    alunosAdapter.notifyDataSetChanged()
                    onResult(lista_alunos.size) // Chama o callback com a quantidade de alunos
                }
            }
        }

        fun obterListaMensalidade(
            lista_mensalidade: MutableList<Mensalidades>,
            mensalidadesAdapter: MensalidadesAdapter,
            onResult: (Int) -> Unit
        ) {
            // Primeiro, obtenha todos os alunos
            db.collection("Alunos").get().addOnSuccessListener { documentosAlunos ->
                // Limpa a lista de mensalidades para evitar duplicatas
                lista_mensalidade.clear()
                // Variável auxiliar para contar o total de mensalidades pendentes
                var totalMensalidadesPendentes = 0
                var alunosProcessados = 0 // Contador para saber quantos alunos foram processados

                // Verifica se a lista de alunos está vazia
                if (documentosAlunos.isEmpty) {
                    // Chama o callback aqui se não houver alunos
                    onResult(0)

                } else {
                    // Itera sobre cada aluno
                    documentosAlunos.forEach { documentoAluno ->
                        // Para cada aluno, obtenha a subcoleção de mensalidades pendentes
                        documentoAluno.reference.collection("Mensalidades").whereEqualTo("status_pagamento", "pendente")
                            .get()
                            .addOnSuccessListener { documentosMensalidades ->
                                documentosMensalidades.forEach { documentoMensalidade ->
                                    val mensalidade =
                                        documentoMensalidade.toObject(Mensalidades::class.java)
                                            .apply {
                                                id = documentoMensalidade.id
                                                alunoId = documentoAluno.id
                                                nomeAluno =
                                                    documentoAluno.getString("nome") // Supondo que existe um campo 'nome' no documento do aluno
                                            }
                                    lista_mensalidade.add(mensalidade)
                                    totalMensalidadesPendentes++
                                }
                                alunosProcessados++ // Incrementa o contador de alunos processados
                                // Verifica se todos os alunos foram processados
                                if (alunosProcessados == documentosAlunos.size()) {
                                    mensalidadesAdapter.notifyDataSetChanged()
                                    onResult(totalMensalidadesPendentes)
                                    mensalidadesAdapter.notifyDataSetChanged()// Chama o callback com a quantidade de mensalidades pendentes
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Handle any errors here
                                alunosProcessados++
                                if (alunosProcessados == documentosAlunos.size()) {
                                    mensalidadesAdapter.notifyDataSetChanged()
                                    onResult(totalMensalidadesPendentes) // Chama o callback com a quantidade de mensalidades pendentes

                                }
                            }
                    }
                }
            }
                .addOnFailureListener { exception ->
                    // Handle any errors here
                    onResult(0)
                }
        }

        fun obterListaMensalidadePaga(
            lista_mensalidade_paga: MutableList<ComprovantesPagamento>,
            mensalidadesPagaAdapter: MensalidadePagaAdapter,
            onResult: (Int, Double) -> Unit
        ) {
            db.collectionGroup("Mensalidade_Paga")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    lista_mensalidade_paga.clear() // Limpa a lista para garantir que não haverá duplicatas
                    var totalPago = 0.00

                    querySnapshot.documents
                        .mapNotNull { it.toObject(ComprovantesPagamento::class.java) }
                        .sortedByDescending { converterDataParaOrdenacao(it.refMes) }
                        .forEach { mensalidade ->
                            lista_mensalidade_paga.add(mensalidade)
                            totalPago += mensalidade.preco?.toDoubleOrNull() ?: 0.00
                        }

                    mensalidadesPagaAdapter.notifyDataSetChanged()
                    onResult(lista_mensalidade_paga.size, totalPago)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Erro ao obter mensalidades pagas", exception)
                    onResult(0, 0.00)
                }
        }


        fun converterDataParaOrdenacao(data: String?): String {
            val partes = data?.split("/")
            if (partes != null) {
                if (partes.size == 2) { // Garante que a data está no formato esperado
                    return "${partes?.get(1)}${partes?.get(0)?.padStart(2, '0')}" // Preenche o mês com zero à esquerda se necessário
                }
            }
            return data.toString() // Retorna a data original se não estiver no formato esperado
        }

        fun deletarMensalidade(
            alunoId: String,
            mensalidadeId: String,
            onSuccess: () -> Unit,
            onError: (Exception) -> Unit
        ) {

            db.collection("Alunos").document(alunoId).collection("Mensalidades")
                .document(mensalidadeId).delete()
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onError(e)
                }


        }
        fun deletarMensalidadesTodas(
            mensalidadesPorAluno: Map<String, List<String>>,
            onSuccess: () -> Unit,
            onError: (Exception) -> Unit
        ) {
            var totalDeletions = mensalidadesPorAluno.values.sumOf { it.size }
            var deletionsCompleted = 0

            mensalidadesPorAluno.forEach { (alunoId, listaMensalidades) ->
                listaMensalidades.forEach { mensalidadeId ->
                    db.collection("Alunos").document(alunoId).collection("Mensalidades").document(mensalidadeId).delete()
                        .addOnSuccessListener {
                            deletionsCompleted++
                            if (deletionsCompleted == totalDeletions) {
                                onSuccess()

                            }
                        }
                        .addOnFailureListener { e ->
                            onError(e)
                        }
                }
            }
        }




        fun atualizarMensalidadeComFoto(
            alunoId: String,
            mensalidadeId: String,
            tituloEditado: String,
            precoEditado: String,
            dataEditada: String,
            foto: Uri
        ) {

            val storageReference =
                storage.getReference("/Mensalidades/$alunoId/${foto.lastPathSegment}")
            storageReference.putFile(foto).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {uri->
                    db.collection("Alunos").document(alunoId).collection("Mensalidades")
                        .document(mensalidadeId).update(
                        "titulo_cobranca", tituloEditado, "preco", precoEditado, "data", dataEditada, "foto", uri.toString()
                    ).addOnCompleteListener {

                    }

                }

            }

        }
        fun atualizarMensalidadeSemFoto(
            alunoId: String,
            mensalidadeId: String,
            tituloEditado: String,
            precoEditado: String,
            dataEditada: String,
            ) {
                    db.collection("Alunos").document(alunoId).collection("Mensalidades")
                        .document(mensalidadeId).update(
                            "titulo_cobranca", tituloEditado, "preco", precoEditado, "data", dataEditada)
                        .addOnCompleteListener {

                        }

                }

        fun graduarAluno(faixa: String, grau: String, alunoID: String){
            db.collection("Alunos").document(alunoID).update(
                "faixa", faixa,
                "grau", grau
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("graduacao", "Aluno graduado com sucesso")
                    Log.d("graduacao", "a faixa nova é ${faixa}")
                    Log.d("graduacao", "o grau novo é ${grau}")
                } else {
                    task.exception?.let {
                        Log.e("graduacao", "Erro ao graduar aluno", it)
                    }
                }
            }
        }

        fun obterDocumentosColecaoSimples() {
            val db = FirebaseFirestore.getInstance()

            db.collection("Mensalidade_Aluno").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentos = task.result
                    if (documentos != null) {
                        for (documento in documentos) {
                            Log.d("Firestore", "ID do Documento: ${documento.id}, Dados: ${documento.data}")
                        }
                        Log.d("Firestore", "Total de documentos na coleção: ${documentos.size()}")
                    } else {
                        Log.d("Firestore", "Nenhum documento encontrado")
                    }
                } else {
                    task.exception?.let { e ->
                        Log.e("Firestore", "Erro ao acessar documentos", e)
                    }
                }
            }
        }

        fun atualizarStatusPagamento(status_pagamento:String, alunoID:String, id_mensadalidade:String){

            db.collection("Mensalidade_Aluno").document(alunoID)
                .collection("Mensalidade_Paga").document(id_mensadalidade).update(
                    "status_pagamento", status_pagamento
                ).addOnCompleteListener {
                    Log.d("atualizar_status", "status atualizado com sucesso ${status_pagamento}")

                }.addOnFailureListener {
                    Log.d("atualizar_status", "falha ao atualizar")
                }

        }



        fun salvarTokenNoFirestore(token: String) {
            val idUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: return // Retorna se o usuário não estiver logado
            val db = FirebaseFirestore.getInstance()
            val data = hashMapOf("fcmToken" to token)

            // Salva o token na coleção 'Professores', no documento específico do usuário
            db.collection("Professor").document(idUsuario)
                .set(data, SetOptions.merge()) // Usando SetOptions.merge() para atualizar apenas o token sem sobrescrever outros campos
                .addOnCompleteListener {

                }.addOnFailureListener {

                }
        }




    }




