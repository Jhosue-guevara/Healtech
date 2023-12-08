package com.example.healtech

data class DatosMedic(
    override var dui: String? = null,
    override var nombre: String? = null,
    override var direccion: String? = null,
    override var telefono: String? = null,
    override var email: String? = null,
    override var clave: String? = null,
    var jvpm: String? = null
) : Datos
