package pe.edu.upc.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upc.entity.Usuario;
import pe.edu.upc.repository.UsuarioRepository;
import pe.edu.upc.service.IUsuarioService;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	private UsuarioRepository uR;

	@Override
	@Transactional
	public Integer insertar(Usuario usuario) {

		int rpta = uR.buscarNumeroDni(usuario.getDniUsuario());
		if (rpta == 0) {
			uR.save(usuario);

		}
		return rpta;
	}

	@Override
	@Transactional
	public void modificar(Usuario usuario) {
		uR.save(usuario);
	}

	@Override
	@Transactional
	public void eliminar(int idUsuario) {
		uR.deleteById(idUsuario);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Usuario> listarId(int idUsuario) {
		return uR.findById(idUsuario);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> listar() {
		return uR.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> buscarNombre(String nombreUsuario) {
		return uR.findByNombreUsuario(nombreUsuario);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Usuario> buscarDni(String dniUsuario) {
		return uR.findByDniUsuario(dniUsuario);
	}

	@Override
	public List<Usuario> buscarRol(String nombreRol) {
		return uR.buscarRol(nombreRol);
	}
}
