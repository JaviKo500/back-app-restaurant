package com.appetit.service;

import org.springframework.stereotype.Service;

@Service
public class GenerarNumFacturaService {

	public String generarNumeroFAct(Long id) {
		String res = "";
		if (id >= 1000)
			res = "" + id;
		if (id >= 100)
			res = "0" + id;
		if (id >= 10)
			res = "00" + id;
		if (id >= 1)
			res = "000" + id;
		return res;
	}

}
