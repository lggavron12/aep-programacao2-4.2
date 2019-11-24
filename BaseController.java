package br.unicesumar.base;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@RestController
public abstract class BaseController<ENTITY extends BaseEntity, REPOSITORY extends BaseRepository<ENTITY>> {
	@Autowired
	private REPOSITORY repo;

	public REPOSITORY getRepo() {
		return repo;
	}

	@GetMapping("/{id}")
	public ResponseEntity getById(@PathVariable("id") String id) {
		if (repo.findById(id).isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(repo.findById(id).get(), HttpStatus.OK);
	}

	@GetMapping
	public List<ENTITY> get() {
		return repo.findAll();
	}

	@PostMapping
	public ResponseEntity post(@RequestBody ENTITY object) {
		if (!repo.findById(object.getId()).isEmpty()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		if (object.toString().isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		beforePost(object);
		repo.save(object);
		afterPost(object);
		return new ResponseEntity<>(object.getId(),HttpStatus.CREATED);
	}

	public void beforePost(ENTITY object) {
	}

	public void afterPost(ENTITY object) {
	}

	// Tratando status HTTP na API REST
	/*
	 * https://www.restapitutorial.com/httpstatuscodes.html
	 * https://httpstatusdogs.com/
	 */

	@PutMapping("/{id}")
	public ResponseEntity<String> put(@PathVariable String id, @RequestBody ENTITY object) {
		if (!Objects.equals(id, object.getId())) {
			// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Requisiçao
			// inválida! Verifique os IDs da entidade!"); //badRequest().build();
			return ResponseEntity.status(400).body("Requisiçao inválida! Verifique os IDs da entidade!"); // badRequest().build();
																											// }
		}
		if (!repo.findById(id).isPresent()) {
			// return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			// //.notFound().build();
			return ResponseEntity.status(404).build(); // .notFound().build();
		}
		repo.save(object);
		return ResponseEntity.status(HttpStatus.OK).build(); // .ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity delete(@PathVariable String id) {
		if (!repo.findById(id).isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		repo.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
