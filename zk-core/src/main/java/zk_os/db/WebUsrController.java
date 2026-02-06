//package zkbae.db;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//@RestController
//public class WebUsrController {
//
//	//		private final Rest rest;
//	@Autowired
//	private WebUsrRepository repository;
//
////	WebUsrController() {
////		this.rest = rest;
////		this.repository = repository;
////	}
//
//
//	// Aggregate root
//	@GetMapping("/wusers")
//	List<WebUsr> all() {
//		return Arrays.asList(new WebUsr());
//	}
//
//
//	@PostMapping("/wusers")
//	WebUsr newWebUsr(@RequestBody WebUsr newWebUsr) {
//		return repository.save(newWebUsr);
//	}
//
//	@GetMapping("/newWebUsr")
//	WebUsr storeEmpoyee() {
//		return repository.save(new WebUsr());
//	}
//	// Single item
//
//	@GetMapping("/wusers/{id}")
//	public WebUsr one(@PathVariable Long id) {
//		return repository.findById(id)
//				.orElseThrow(() -> new WebUsrNotFoundException(id));
//	}
//
//	public class WebUsrNotFoundException extends RuntimeException {
//
//		public WebUsrNotFoundException(Long id) {
//			super("Could not find employee " + id);
//		}
//	}
//
////	@PutMapping("/wusers/{id}")
////	WebUsr replaceWebUsr(@RequestBody WebUsr newWebUsr, @PathVariable Integer id) {
////
////		return repository.findById(id)
////				.map(WebUsr -> {
////					WebUsr.setName(newWebUsr.getName());
////					WebUsr.setRole(newWebUsr.getRole());
////					return repository.save(WebUsr);
////				})
////				.orElseGet(() -> {
////					newWebUsr.setId(id);
////					return repository.save(newWebUsr);
////				});
////	}
//
//	@DeleteMapping("/wusers/{id}")
//	void deleteWebUsr(@PathVariable Long id) {
//		repository.deleteById(id);
//	}
//}
