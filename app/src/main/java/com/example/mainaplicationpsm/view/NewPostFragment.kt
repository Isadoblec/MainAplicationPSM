package com.example.mainaplicationpsm.view

import com.bumptech.glide.Glide // <--- Agrega esto con tus otros imports
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mainaplicationpsm.R
import com.example.mainaplicationpsm.adapter.BorradorAdapter
import com.example.mainaplicationpsm.api.RetrofitClient
import com.example.mainaplicationpsm.db.AppDatabase
import com.example.mainaplicationpsm.db.Borrador
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class NewPostFragment : Fragment() {

    // --- VARIABLES DE LA VISTA ---
    private lateinit var ivPostImagePreview: ImageView
    private lateinit var etPostDescription: EditText
    private lateinit var btnPublishPost: Button
    private lateinit var btnSaveDraft: Button

    // Variables para el Carrusel
    private lateinit var btnToggleDrafts: Button
    private lateinit var rvDraftsCarousel: RecyclerView
    private lateinit var adapter: BorradorAdapter

    // Variable para la imagen seleccionada
    private var imageUri: Uri? = null

    // Launcher para abrir galería
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri

            // Usamos GLIDE para cargar la imagen (es más rápido y maneja mejor la memoria)
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(ivPostImagePreview)


            ivPostImagePreview.setColorFilter(null)
            ivPostImagePreview.imageTintList = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_post, container, false)

        // 1. VINCULAR VISTAS
        ivPostImagePreview = view.findViewById(R.id.ivPostImagePreview)
        etPostDescription = view.findViewById(R.id.etPostDescription)
        btnPublishPost = view.findViewById(R.id.btnPublishPost)
        btnSaveDraft = view.findViewById(R.id.btnSaveDraft)
        btnToggleDrafts = view.findViewById(R.id.btnToggleDrafts)
        rvDraftsCarousel = view.findViewById(R.id.rvDraftsCarousel)

        // 2. CONFIGURAR CARRUSEL
        setupCarousel()

        // 3. LISTENERS (CLICKS)

        // Cargar imagen
        ivPostImagePreview.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Publicar en Internet
        btnPublishPost.setOnClickListener {
            subirPublicacion()
        }

        // Guardar en BD Local
        btnSaveDraft.setOnClickListener {
            guardarBorradorLocal()
        }

        // Mostrar / Ocultar Carrusel
        btnToggleDrafts.setOnClickListener {
            if (rvDraftsCarousel.visibility == View.GONE) {
                rvDraftsCarousel.visibility = View.VISIBLE
                cargarBorradoresEnCarrusel() // Cargar datos frescos
                btnToggleDrafts.text = "Ocultar borradores 🔼"
            } else {
                rvDraftsCarousel.visibility = View.GONE
                btnToggleDrafts.text = "Ver mis borradores guardados 📂"
            }
        }

        return view
    }

    // --- LÓGICA DEL CARRUSEL ---
    private fun setupCarousel() {
        // Inicializamos el adaptador vacío.
        // El bloque { borrador -> ... } es lo que pasa cuando tocas una tarjeta.
        adapter = BorradorAdapter(emptyList()) { borradorSeleccionado ->

            // A. Poner Texto
            etPostDescription.setText(borradorSeleccionado.descripcion)

            // B. Poner Imagen (si hay)
            // B. Poner Imagen (si hay)
            borradorSeleccionado.uriFoto?.let { uriString ->
                try {
                    val uri = Uri.parse(uriString)
                    imageUri = uri

                    // Cargar con Glide y quitar filtro gris
                    Glide.with(this)
                        .load(uri)
                        .centerCrop()
                        .into(ivPostImagePreview)

                    ivPostImagePreview.setColorFilter(null)
                    ivPostImagePreview.imageTintList = null

                } catch (e: Exception) {
                    ivPostImagePreview.setImageResource(R.drawable.baseline_add_24)
                }
            }

            Toast.makeText(context, "Borrador cargado ✨", Toast.LENGTH_SHORT).show()
        }

        rvDraftsCarousel.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvDraftsCarousel.adapter = adapter
    }

    private fun cargarBorradoresEnCarrusel() {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                val lista = db.borradorDao().obtenerTodos()

                if (lista.isNotEmpty()) {
                    adapter.actualizarLista(lista)
                } else {
                    Toast.makeText(context, "No tienes borradores aún", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error cargando lista: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- LÓGICA DE GUARDADO LOCAL ---
    private fun guardarBorradorLocal() {
        val descripcion = etPostDescription.text.toString()

        if (descripcion.isEmpty()) {
            Toast.makeText(context, "Escribe algo para guardar...", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val uriString = imageUri?.toString()
                val nuevoBorrador = Borrador(
                    titulo = "Borrador",
                    descripcion = descripcion,
                    uriFoto = uriString
                )

                val db = AppDatabase.getDatabase(requireContext())
                db.borradorDao().insertar(nuevoBorrador)

                Toast.makeText(context, "¡Borrador guardado! 💾", Toast.LENGTH_SHORT).show()

                // Limpiar pantalla
                etPostDescription.setText("")
                ivPostImagePreview.setImageResource(R.drawable.baseline_add_24)
                imageUri = null

                // Si el carrusel está abierto, recargarlo para ver el nuevo
                if (rvDraftsCarousel.visibility == View.VISIBLE) {
                    cargarBorradoresEnCarrusel()
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- LÓGICA DE SUBIDA (RETROFIT) ---
    private fun subirPublicacion() {
        val descripcion = etPostDescription.text.toString()

        if (descripcion.isEmpty()) {
            Toast.makeText(context, "Escribe algo primero...", Toast.LENGTH_SHORT).show()
            return
        }

        btnPublishPost.isEnabled = false
        btnPublishPost.text = "Subiendo..."

        lifecycleScope.launch {
            try {
                val idUsuarioPart = "1".toRequestBody("text/plain".toMediaTypeOrNull())
                val idForoPart = "1".toRequestBody("text/plain".toMediaTypeOrNull())
                val tituloPart = "Publicación de Isacc".toRequestBody("text/plain".toMediaTypeOrNull())
                val descPart = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
                val borradorPart = "0".toRequestBody("text/plain".toMediaTypeOrNull())

                var imagenPart: MultipartBody.Part? = null

                imageUri?.let { uri ->
                    val file = archivoDesdeUri(uri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        imagenPart = MultipartBody.Part.createFormData("foto", file.name, requestFile)
                    }
                }

                val response = RetrofitClient.instance.subirPublicacion(
                    idUsuarioPart, idForoPart, tituloPart, descPart, borradorPart, imagenPart
                )

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "¡Publicado con éxito!", Toast.LENGTH_LONG).show()
                    etPostDescription.setText("")
                    ivPostImagePreview.setImageResource(R.drawable.baseline_add_24)
                    imageUri = null
                } else {
                    Toast.makeText(context, "Error servidor: ${response.body()?.mensaje}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                btnPublishPost.isEnabled = true
                btnPublishPost.text = "Publicar"
            }
        }
    }

    private fun archivoDesdeUri(uri: Uri): File? {
        return try {
            val contentResolver = requireContext().contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", requireContext().cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}