package com.appetit.imagenes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {
	public Resource cargar(String nombreFoto, String ruta) throws MalformedURLException;

	public String copiar(MultipartFile archivo, String ruta) throws IOException;

	public boolean eliminar(String nombreFoto, String ruta);

	public Path getPath(String nombreFoto,String ruta);
}
