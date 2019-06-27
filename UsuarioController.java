package pe.edu.upc.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.upc.entity.Usuario;
import pe.edu.upc.service.IRolService;
import pe.edu.upc.service.IUploadFileService;
import pe.edu.upc.service.IUsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
	@Autowired
	private IUsuarioService uService;

	@Autowired
	private IRolService rService;

	@Autowired
	private IUploadFileService uploadFileService;

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;

		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	@RequestMapping("/bienvenido")
	public String irBienvenido() {
		return "bienvenido";
	}

	@GetMapping("/nuevo")
	public String nuevoUsuario(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("listaRoles", rService.listar());
		return "usuario/usuario";
	}

	@PostMapping("/guardar")
	public String guardarCategoria(@Valid Usuario usuario, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) throws Exception {
		if (result.hasErrors()) {
			model.addAttribute("listaRoles", rService.listar());
			return "usuario/usuario";
		} else {
			if (!foto.isEmpty()) {

				if (usuario.getIdUsuario() > 0 && usuario.getFotoUsuario() != null
						&& usuario.getFotoUsuario().length() > 0) {

					uploadFileService.delete(usuario.getFotoUsuario());
				}

				String uniqueFilename = null;
				try {
					uniqueFilename = uploadFileService.copy(foto);
				} catch (IOException e) {
					e.printStackTrace();
				}

				flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFilename + "'");
				usuario.setFotoUsuario(uniqueFilename);
			}
			int rpta = uService.insertar(usuario);
			if (rpta > 0) {
				model.addAttribute("listaRoles", rService.listar());
				model.addAttribute("mensaje", "Ya existe");
				return "/usuario/usuario";
			} else {
				model.addAttribute("mensaje", "Se guardó correctamente");
				status.setComplete();
			}

		}
		model.addAttribute("listaUsuarios", uService.listar());

		return "/usuario/listaUsuario";
	}

	@GetMapping("/listar")
	public String listarUsuarios(Model model) {
		try {
			model.addAttribute("usuario", new Usuario());
			model.addAttribute("listaUsuarios", uService.listar());
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "/usuario/listaUsuario";
	}

	@GetMapping("/detalle/{id}")
	public String detailsUsuario(@PathVariable(value = "id") int id, Model model) {
		try {
			Optional<Usuario> usuario = uService.listarId(id);
			if (!usuario.isPresent()) {
				model.addAttribute("info", "Usuario no existe");
				return "redirect:/usuarios/listar";
			} else {
				model.addAttribute("usuario", usuario.get());
			}

		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "/usuario/usuario";
	}

	@RequestMapping("/eliminar")
	public String eliminar(Map<String, Object> model, @RequestParam(value = "id") Integer id) {
		try {
			if (id != null && id > 0) {
				uService.eliminar(id);
				model.put("mensaje", "Se eliminó correctamente");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			model.put("mensaje", "No se puede eliminar un usuario");
		}
		model.put("listaUsuarios", uService.listar());

		return "redirect:/usuarios/listar";
	}

	@RequestMapping("/buscar")
	public String buscar(Map<String, Object> model, @ModelAttribute Usuario usuario) throws ParseException {

		List<Usuario> listaUsuarios;

		usuario.setNombreUsuario(usuario.getNombreUsuario());
		listaUsuarios = uService.buscarNombre(usuario.getNombreUsuario());
		usuario.setDniUsuario(usuario.getDniUsuario());
		listaUsuarios = uService.buscarDni(usuario.getDniUsuario());

		if (listaUsuarios.isEmpty()) {
			listaUsuarios = uService.buscarNombre(usuario.getNombreUsuario());
		}
		if (listaUsuarios.isEmpty()) {
			listaUsuarios = uService.buscarDni(usuario.getNombreUsuario());
		}
		if (listaUsuarios.isEmpty()) {
			listaUsuarios = uService.buscarRol(usuario.getNombreUsuario());
		}
		if (listaUsuarios.isEmpty()) {
			model.put("mensaje", "No se encontró");
		}
		model.put("listaUsuarios", listaUsuarios);
		return "usuario/listaUsuario";

	}
	
	@GetMapping(value = "/verusu/{id}")
	public String ver(@PathVariable(value = "id") Integer id, Map<String, Object> model, RedirectAttributes flash) {

		Optional<Usuario> usuario = uService.listarId(id);
		if (usuario == null) {
			flash.addFlashAttribute("error", "El Usuario no existe en la base de datos");
			return "redirect:/usuarios/listar";
		}

		model.put("usuario", usuario.get());

		return "usuario/verusu";
	}

	@RequestMapping("/modificar/{id}")
	public String modificar(@PathVariable int id, Model model, RedirectAttributes objRedir) {
		Optional<Usuario> objUsu = uService.listarId(id);

		if (objUsu == null) {
			objRedir.addFlashAttribute("mensaje", "Ocurrió un error");
			return "redirect:/usuarios/listar";
		} else {
			model.addAttribute("listaRoles", rService.listar());
			model.addAttribute("usuario", objUsu.get());
			return "usuario/usuario";
		}
	}

}
