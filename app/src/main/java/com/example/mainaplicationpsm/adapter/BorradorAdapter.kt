package com.example.mainaplicationpsm.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // <--- Importante: Usamos Glide
import com.example.mainaplicationpsm.R
import com.example.mainaplicationpsm.db.Borrador

class BorradorAdapter(
    private var listaBorradores: List<Borrador>,
    private val onBorradorClick: (Borrador) -> Unit
) : RecyclerView.Adapter<BorradorAdapter.BorradorViewHolder>() {

    class BorradorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivDraftImage)
        val tvDesc: TextView = view.findViewById(R.id.tvDraftDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorradorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_borrador, parent, false)
        return BorradorViewHolder(view)
    }

    override fun onBindViewHolder(holder: BorradorViewHolder, position: Int) {
        val borrador = listaBorradores[position]

        // 1. Poner texto
        holder.tvDesc.text = borrador.descripcion

        // 2. Poner imagen (si existe)
        if (borrador.uriFoto != null) {
            try {
                // Usamos GLIDE para cargar la miniatura suavemente
                Glide.with(holder.itemView.context)
                    .load(Uri.parse(borrador.uriFoto))
                    .centerCrop()
                    .into(holder.ivImage)

                // 🔥 LA CLAVE: Quitamos la pintura gris
                holder.ivImage.setColorFilter(null)
                holder.ivImage.imageTintList = null

            } catch (e: Exception) {
                // Si falla, mostramos el icono por defecto
                holder.ivImage.setImageResource(R.drawable.baseline_add_24)
            }
        } else {
            // Si NO tiene foto, mostramos el icono "+"
            holder.ivImage.setImageResource(R.drawable.baseline_add_24)
            // Aquí NO quitamos el filtro, para que el "+" se vea gris (desactivado)
        }

        // 3. Click
        holder.itemView.setOnClickListener {
            onBorradorClick(borrador)
        }
    }

    override fun getItemCount() = listaBorradores.size

    fun actualizarLista(nuevaLista: List<Borrador>) {
        listaBorradores = nuevaLista
        notifyDataSetChanged()
    }
}