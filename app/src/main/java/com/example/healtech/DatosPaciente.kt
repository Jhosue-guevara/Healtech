package com.example.healtech

data class DatosPaciente(
    override var dui: String? = null,
    override var nombre: String? = null,
    override var direccion: String? = null,
    override var telefono: String? = null,
    override var email: String? = null,
    override var clave: String? = null
) : Datos
