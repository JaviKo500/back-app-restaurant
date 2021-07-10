package com.appetit.WebSocketController;

import java.util.Date;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.appetit.models.Notificacion;

@Controller
public class SocketsController {
	@MessageMapping("/mensaje")
	@SendTo("/chat/mensaje")
	public Notificacion recibemensaje(Notificacion notificacion) {
		notificacion.setFecha(new Date().getTime());
		System.out.println("llego" + notificacion.getTexto());
		return notificacion;
	}
}
