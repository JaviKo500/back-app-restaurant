package com.appetit.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appetit.models.ArqueoCaja;
import com.appetit.service.ArqueoService;
import com.appetit.service.ValidacionService;

@RestController
@CrossOrigin("*")
@RequestMapping("/")
public class ArqueoRestCotroller {
	@Autowired
	ArqueoService arqueoService;
	@Autowired
	ValidacionService validacion;

	@Secured({"ROLE_ADMIN"})
	@GetMapping("get/arqueos-caja/from/{desde}/to/{hasta}/page/{page}")
	public ResponseEntity<?> listarArqueosEntreFechas(@PathVariable Date desde, @PathVariable Date hasta,
			@PathVariable Integer page) {
		Map<String, Object> response = new HashMap<>();
		Page<ArqueoCaja> arqueos;
		try {
			Pageable pageable = PageRequest.of(page, 10);
			arqueos = arqueoService.ArqueosEntreFechas(pageable, desde, hasta);
		} catch (DataAccessException e) {
			response.put("mensaje", "No se listó los arqueos");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "lista obtenida");
		response.put("arqueos", arqueos);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	@Secured({"ROLE_ADMIN"})
	@PostMapping("register/new/arqueo-caja")
	public ResponseEntity<?> registrarNuevoArqueo(@RequestBody ArqueoCaja arqueo) {
		Map<String, Object> response = new HashMap<>();
		List<String> errores = validacion.camposArqueo(arqueo);
		if (errores.size() != 0) {
			response.put("mensaje", "No se resgistró el arqueo");
			response.put("error", "campos requeridos");
			response.put("errores", errores);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CONFLICT);
		}
		 
		// validar si existe un arqueo abierto
		if (arqueo.getCaja().getEstado() == false) {
			response.put("mensaje", "Error de apertura de caja.");
			response.put("error", "Error al registrar el arqueo la caja esta deshabilitada");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		ArqueoCaja arqueoRev = arqueoService.obtenerArqByCajaUserIsOpen(arqueo.getCaja(), arqueo.getUsuario());
		if (arqueoRev != null) {
			response.put("mensaje", "Error de apertura de caja.");
			response.put("error", "Ya se encuenta habilitado un arqueo para la caja: "
					+ arqueo.getCaja().getNombreCaja() + " Y usuario: " + arqueo.getUsuario().getNombre());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		arqueoRev = arqueoService.getArqueoByUsuarioIsOpen(arqueo.getUsuario());
		if (arqueoRev != null) {
			response.put("mensaje", "Error de apertura de caja.");
			response.put("error", "El usuario: " + arqueo.getUsuario().getNombre() + " Ya tiene una caja habilitada.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			arqueo.setEfectivo(arqueo.getMontoInicial());
			arqueo.setMontoFinal(arqueo.getMontoInicial());
			arqueo.setIsOpen(true);
			arqueo.setFecha(new Date());
			arqueo.setFechaApertura(new Date());
			arqueoService.registrarNuevoArqueo(arqueo);
		} catch (DataAccessException e) {
			response.put("mensaje", "No se resgistró el arqueo");
			response.put("error", e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Arqueo exitos.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// cerrar una caja
	@Secured({"ROLE_ADMIN"})
	@PutMapping("cerrar/arqueo")
	public ResponseEntity<?> CerrarCaja(@RequestBody ArqueoCaja arqueo) {
		Map<String, Object> response = new HashMap<>();
		// validar que no se vuelva a cerrar
		ArqueoCaja arqueoRegistrado = arqueoService.obtenerArqueoPorId(arqueo.getId());
		if (arqueoRegistrado == null) {
			response.put("mensaje", "No se cerró el arqueo.");
			response.put("error", "Error de id de arqueo");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		if (arqueoRegistrado.getIsOpen() == true) {
			try {
				arqueoRegistrado.setFechaCierre(new Date());
				arqueoRegistrado.setIsOpen(false);
				arqueoRegistrado.setDescripcionCierre(arqueo.getDescripcion());
				arqueoService.registrarNuevoArqueo(arqueoRegistrado);
			} catch (DataAccessException e) {
				response.put("mensaje", "No se cerró el arqueo.");
				response.put("error", e.getMostSpecificCause().getMessage());
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			response.put("mensaje", "Caja cerrada correctamente.");
		} else {
			response.put("mensaje", "La caja ya fué cerrada.");
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

}
