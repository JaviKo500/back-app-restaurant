package com.appetit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.appetit.models.ArqueoCaja;
import com.appetit.models.Caja;
import com.appetit.models.Cliente;
import com.appetit.models.Combo;
import com.appetit.models.Usuario;

@Service
public class ValidacionService {

	public Boolean Email(String email) {
		Pattern pattern = Pattern.compile(
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher mather = pattern.matcher(email);
		if (mather.find() != true) {
			return false; // si es falso si contiene
		}
		return true;
	}

	public Boolean CamposConEspacios(String valor) {
		Pattern p = Pattern.compile("^[a-zA-ZñÑ ]*$");
		Matcher val = p.matcher(valor);
		if (val.find() != true) {
			return false; // si es falso contiene caracteres especiales
		}
		return true;
	}

	public Boolean CamposNumericos(String valor) {
		Pattern p = Pattern.compile("^[0-9]*$");
		Matcher val = p.matcher(valor);
		if (val.find() != true) {
			return false; // si es falso contiene caracteres especiales
		}
		return true;
	}

	public Boolean CamposConEspaciosYNumeros(String valor) {
		Pattern p = Pattern.compile("^[a-zA-ZñÑ0-9 ]*$");
		Matcher val = p.matcher(valor);
		if (val.find() != true) {
			return false; // si es falso contiene caracteres especiales
		}
		return true;
	}

	public Boolean CamposSinEspacios(String valor) {
		Pattern p = Pattern.compile("^[a-zA-ZñÑ0-9]*$");
		Matcher val = p.matcher(valor);
		if (val.find() != true) {
			return false;
		}
		return true;
	}

	// validacion de arqueo
	public List<String> camposArqueo(ArqueoCaja a) {
		List<String> lista = new ArrayList<>();
		if (a.getCaja() == null) {
			lista.add("Es necesario seleccionar una caja.");
		}
		if (a.getUsuario() == null) {
			lista.add("El usuario responsable no existe");
		}
		return lista;
	}

	// validacion de caja
	public List<String> camposCaja(Caja c) {
		List<String> errores = new ArrayList<>();
		if (c.getNombreCaja().length() < 2) {
			errores.add("Ingrese un nombre válido.");
		} else {
			if (CamposConEspaciosYNumeros(c.getNombreCaja()) == false) {
				errores.add("El nombre debe contener solo caracteres alfabeticos.");
			}
		}

		if (c.getNumeroCaja().length() == 0) {
			errores.add("Ingrese un código/número de caja válido.");
		} else {
			if (CamposSinEspacios(c.getNumeroCaja()) == false) {
				errores.add("El código/número de caja debe contener caracteres alfabeticos.");
			}
		}
		return errores;
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
		if (u.getRoles().size() == 0) {
			lista.add("Debe seleccionar un rol para el usuario.");
		}
		if (u.getSexo() == null) {
			lista.add("Debe seleccionar un genero para el usuario.");
		}
		return lista;
	}

	// validar cliente
	public List<String> camposCliente(Cliente c) {
		List<String> lista = new ArrayList<>();
		if (c.getNombres().length() < 3) {
			lista.add("El nombre debe contener almenos 3 letras.");
		}
		if (CamposConEspacios(c.getNombres()) == false) {
			lista.add("El nombre no debe contener caracteres especiales.");
		}
		if (c.getApellidos().length() < 3) {
			lista.add("El apellido debe contener almenos 3 caracteres.");
		}
		if (CamposConEspacios(c.getApellidos()) == false) {
			lista.add("El Apellido no debe contener caracteres especiales.");
		}
		if (c.getCedula().length() < 10) {
			lista.add("La cédula debe contener 10 dígitos.");
		}
		if (Email(c.getEmail()) == false) {
			lista.add("El email es inválido.");
		}
		if (c.getDireccion().length() < 3) {
			lista.add("Ingrese una dirección correcta.");
		}
		if (c.getCelular().length() < 10) {
			lista.add("El Celular debe contener almenos 10 dígitos.");
		}
		return lista;
	}

	// validar productos

	// validar combo
	public List<String> camposCombo(Combo c) {
		List<String> errores = new ArrayList<>();
		if (c.getCategoria() == null) {
			errores.add("Seleccionar una categoría.");
		}
		if (c.getImagen() == null && c.getId() != null) {
			errores.add("Seleccionar una imágen");
		}
		if (c.getItemsCombo().size() == 0) {
			errores.add("Seleccionar productos para el combo.");
		}
		if (c.getNombre().length() < 3) {
			errores.add("Ingresar un nombre válido");
		}
		if (c.getPrecio() == 0) {
			errores.add("Ingresar un precio válido");
		}
		if (CamposConEspaciosYNumeros(c.getNombre()) == false) {
			errores.add("No ingresar caracteres especiales en el nombre.");
		}

		return errores;
	}
}
