package com.registro.CruzMarket.servicio; // Asegúrate de que el paquete sea correcto

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage; // Necesitas importar esto
import org.springframework.mail.javamail.JavaMailSender; // Necesitas importar esto
import org.springframework.stereotype.Service;
import com.registro.CruzMarket.servicio.interfaces.IEmailServicio;

@Service
public class EmailServicio implements IEmailServicio {

    // 1. INYECTAR LA INTERFAZ CLAVE DE SPRING
    // Spring inyectará automáticamente el bean configurado con 'spring.mail.*'
    @Autowired
    private JavaMailSender mailSender;

    // Opcional: El correo remitente se puede leer de la configuración
    // @Value("${spring.mail.username}")
    // private String fromEmail;

    @Override
    public void enviarEmail(String to, String subject, String content) {

        try {
            // 2. CREAR EL MENSAJE SIMPLE
            SimpleMailMessage email = new SimpleMailMessage();

            // Si no especificas el remitente, Spring Mail usa el valor de 'spring.mail.username'
            // email.setFrom(fromEmail);

            email.setTo(to);
            email.setSubject(subject);
            email.setText(content); // El contenido es texto plano por defecto con SimpleMailMessage

            // 3. ENVIAR EL CORREO
            mailSender.send(email);

            // Registro de éxito
            System.out.println("------------------------------------------------------------------");
            System.out.println("✅ CRUZ MARKET: Correo Enviado Exitosamente.");
            System.out.println("Destinatario: " + to);
            System.out.println("Asunto: " + subject);
            System.out.println("------------------------------------------------------------------");

        } catch (Exception e) {
            // Registro de error
            System.err.println("------------------------------------------------------------------");
            System.err.println("❌ ERROR CRUZ MARKET: Fallo al enviar correo a " + to);
            System.err.println("Error: " + e.getMessage());
            System.err.println("Asegúrate de configurar spring.mail.* y verificar la 'Contraseña de Aplicación' si usas Gmail.");
            System.err.println("------------------------------------------------------------------");
            // Puedes relanzar la excepción o manejarla según la política de tu aplicación
            // throw new RuntimeException("Fallo al enviar el correo", e);
        }
    }
}