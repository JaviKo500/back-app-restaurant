package com.appetit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.appetit.models.Usuario;

@Service
public class ValidacionService {

	public Boolean Email(String email) {
		Pattern pattern = Pattern.compile(
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher mather = pattern.matcher(email);
		if (mather.find() != true) {
			return false;
		}
		return true;
	}

	public Boolean CamposConEspacios(String valor) {
		Pattern p = Pattern.compile("^[a-zA-Z ]*$");
		Matcher val = p.matcher(valor);
		if (val.find() != true) {
			return false;
		}
		return true;
	}

	public Boolean CamposSinEspacios(String valor) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]*$");
		Matcher val = p.matcher(valor);
		if (val.find() != true) {
			return false;
		}
		return true;
	}

	// validacion campos usuario
	public List<String> camposUsuario(Usuario u) {
		List<String> lista = new ArrayList<>();
		if (u.getCedula().length() < 10) {
			lista.add("La cédula debe contener almenos 10 dígitos.");
		}
		if (u.getTelefono().length() < 10) {
			lista.add("El teléfono debe contener almenos 10 dígitos.");
		}
		if (u.getNombre().length() < 3) {
			lista.add("El nombre debe contener almenos 3 dígitos.");
		} else {
			if (CamposConEspacios(u.getNombre()) == false) {
				lista.add("El nombre debe contener solo caracteres alfabeticos.");
			}
		}
		if (u.getUsername().length() < 4) {
			lista.add("El username debe contener almenos 3 dígitos.");
		} else {
			if (CamposSinEspacios(u.getUsername()) == false) {
				lista.add("El username debe contener solo caracteres alfabeticos sin espacios.");
			}
		}
		if (Email(u.getEmail()) == false) {
			lista.add("El email ingresado es inválido.");
		}
		return lista;
	}

}
