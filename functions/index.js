const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.deleteUserAndData = functions.https.onCall(async (data, context) => {
  // Remove todas as verificações de autenticação e permissões de admin
  try {
    await admin.auth().deleteUser(data.uid); // Substitua por ID do usuário que será deletado
    await admin.firestore().collection("Alunos").doc(data.uid).delete();
    return {message: "Usuário e dados associados deletados com sucesso"};
  } catch (error) {
    throw new functions.https.HttpsError("internal", error.message);
  }
});

exports.enviarNotificacaoNovaMensalidade = functions.firestore
    .document("Mensalidade_Aluno/{alunoId}/Mensalidade_Paga/{mensalidadePagaId}")
    .onCreate(async (snapshot, context) => {
      // Busca os dados de todos os professores
      const professoresRef = admin.firestore().collection("Professor");
      const professoresSnapshot = await professoresRef.get();

      // Se não existem professores, loga o erro e finaliza a execução
      if (professoresSnapshot.empty) {
        console.log("Nenhum professor encontrado.");
        return null;
      }

      // Assume que só há um professor e utiliza o token dele
      const professorData = professoresSnapshot.docs[0].data();
      const tokenFCM = professorData.fcmToken;


      // Cria a notificação com o token FCM do professor
      const payload = {
        notification: {
          title: "Nova Mensalidade Paga",
          body: `Uma nova mensalidade foi paga. Verifique o aplicativo para mais informações.`,
          // Outros campos da notificação, como ícone, som, etc., podem ser adicionados aqui
        },
        token: tokenFCM,
      };

      // Tenta enviar a notificação usando o FCM
      try {
        const response = await admin.messaging().send(payload);
        console.log("Notificação enviada com sucesso:", response);
        return {success: true};
      } catch (error) {
        console.error("Erro ao enviar notificação:", error);
        return {error: error.code};
      }
    });
