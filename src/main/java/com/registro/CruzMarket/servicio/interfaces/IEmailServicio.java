package com.registro.CruzMarket.servicio.interfaces;

public interface IEmailServicio {
    void enviarEmail(String to, String subject, String content);
}