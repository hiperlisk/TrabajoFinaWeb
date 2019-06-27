package pe.edu.upc.service;

import java.util.List;
import java.util.Optional;

import pe.edu.upc.entity.Usuario;

public interface IUsuarioService {
	//public void insertar(Usuario usuario);

	public void modificar(Usuario usuario);

	public void eliminar(int idUsuario);

	Optional<Usuario> listarId(int idUsuario);

	List<Usuario> listar();

	List<Usuario> buscarDni(String dniUsuario);

	List<Usuario> buscarNombre(String nombreUsuario);

	List<Usuario> buscarRol(String nombreRol);

	public Integer insertar(Usuario usuario);
}
