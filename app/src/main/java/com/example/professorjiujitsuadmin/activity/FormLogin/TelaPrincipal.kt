package com.example.professorjiujitsuadmin.activity.FormLogin

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.professorjiujitsuadmin.Adapter.AlunosAdapter
import com.example.professorjiujitsuadmin.Adapter.MensalidadePagaAdapter
import com.example.professorjiujitsuadmin.Adapter.MensalidadesAdapter
import com.example.professorjiujitsuadmin.DB.DB
import com.example.professorjiujitsuadmin.R
import com.example.professorjiujitsuadmin.activity.FormLogin.Fragments.alunos.AlunosFragment
import com.example.professorjiujitsuadmin.databinding.ActivityTelaPrincipalBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class TelaPrincipal : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTelaPrincipalBinding
    private lateinit var alunosAdapter:AlunosAdapter
    private lateinit var mensalidadesAdapter: MensalidadesAdapter
    private lateinit var mensalidadePagaAdapter: MensalidadePagaAdapter
    private val db=DB()

    companion object {

        private const val REQUEST_CAMERA_PERMISSION = 101

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarTelaPrincipal.toolbar)
        verificarPermissoes()
        salvarTokenNotificacao()


        val email = "admin@gmail.com"
        val nome = "admin"

        db.salvarDadosProfessor(nome, email)

        binding.appBarTelaPrincipal.fab.setOnClickListener { view ->
            // Obtenha o FragmentManager e encontre o NavHostFragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_tela_principal) as NavHostFragment
            // Encontre o fragmento atual que está sendo exibido
            val currentFragment = navHostFragment.childFragmentManager.fragments[0]
            // Verifique se o fragmento atual é AlunosFragment e chame o método
            if (currentFragment is AlunosFragment) {
                currentFragment.cadastrarMensalidadeParaAlunosSelecionados()
            } else {

                Toast.makeText(this, "Negado , para cadastrar é necessario estar na tela de alunos!", Toast.LENGTH_LONG).show()
            }
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_tela_principal)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun setAlunosAdapter(adapter: AlunosAdapter) {
        this.alunosAdapter = adapter
        invalidateOptionsMenu() // Chama onCreateOptionsMenu novamente
    }
    fun setMensalidadeAdapter(adapter: MensalidadesAdapter) {
        Log.d("TelaPrincipal", "setMensalidadeAdapter chamado")
        this.mensalidadesAdapter = adapter
        invalidateOptionsMenu() // Força a recriação do menu, que por sua vez reinicializa a SearchView
    }

    fun setMensalidadePagaAdapter(adapter: MensalidadePagaAdapter){
        this.mensalidadePagaAdapter = adapter
        invalidateOptionsMenu()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.tela_principal, menu)

        // Configura a SearchView
        val searchItem = menu.findItem(R.id.buscar)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Tratar o evento quando o usuário confirma a busca
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("Search", "Texto digitado: $newText")
                if (this@TelaPrincipal::alunosAdapter.isInitialized) {
                    alunosAdapter.filter(newText ?: "")
                }
                if(this@TelaPrincipal::mensalidadesAdapter.isInitialized){
                    Log.d("Search", "Chamando filter no MensalidadesAdapter")
                    mensalidadesAdapter.filter(newText ?: "")
                }
                if(this@TelaPrincipal::mensalidadePagaAdapter.isInitialized){
                    mensalidadePagaAdapter.filtro(newText ?: "")
                }


                return true
            }
        })
        return true
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.sair->sairLogin()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_tela_principal)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun sairLogin(){
        FirebaseAuth.getInstance().signOut()
        finish()
        startActivity(Intent(this, FormLogin::class.java))
    }

    private fun verificarPermissoes() {
        val permissoesNecessarias = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val todasPermissoesConcedidas = permissoesNecessarias.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!todasPermissoesConcedidas) {
            // Se não, solicite as permissões
            ActivityCompat.requestPermissions(this, permissoesNecessarias, REQUEST_CAMERA_PERMISSION)
        } else {
            // Permissões já concedidas, continue com o fluxo do aplicativo
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                // Verifica se todas as permissões foram concedidas
                val todasPermissoesConcedidas = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

                // Se todas as permissões foram concedidas, nada precisa ser feito
                if (todasPermissoesConcedidas) {
                    // Permissões concedidas, continuar o fluxo normal do app
                } else {
                    // Se alguma permissão foi negada, verifica se o usuário escolheu "Não permitir"
                    val algumaPermissaoPermanentementeNegada = grantResults.indices
                        .filter { grantResults[it] != PackageManager.PERMISSION_GRANTED }
                        .any { !shouldShowRequestPermissionRationale(permissions[it]) }

                    if (algumaPermissaoPermanentementeNegada) {
                        // Se o usuário escolheu "Não permitir" para alguma permissão, deslogar
                        //  Toast.makeText(this, "Você precisa permitir o acesso para utilizar o app.", Toast.LENGTH_LONG).show()
                        // deslogarUsuario()
                    } else {
                        // Se o usuário não escolheu "Não permitir" permanentemente, não deslogar
                        //  Toast.makeText(this, "Permissão negada. Você pode permitir o acesso nas Configurações.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }
    private fun salvarTokenNotificacao(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)

            // Se necessário, salve o token no Firestore
            db.salvarTokenNoFirestore(token)
        })

    }

}