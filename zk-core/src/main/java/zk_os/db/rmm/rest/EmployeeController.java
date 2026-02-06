//package zkbae.db.rest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//@RestController
//public class EmployeeController {
//
//	//		private final Rest rest;
//	@Autowired
//	private EmployeeRepository repository;
//
////	EmployeeController() {
////		this.rest = rest;
////		this.repository = repository;
////	}
//
//
//	// Aggregate root
//	@GetMapping("/employees")
//	List<Employee> all() {
//		return Arrays.asList(new Employee("f", "l"));
//	}
//
//
//	@PostMapping("/employees")
//	Employee newEmployee(@RequestBody Employee newEmployee) {
//		return repository.save(newEmployee);
//	}
//
//	@GetMapping("/newemployee")
//	Employee storeEmpoyee() {
//		return repository.save(new Employee("first", "admin"));
//	}
//	// Single item
//
//	@GetMapping("/employees/{id}")
//	public Employee one(@PathVariable Integer id) {
//		return repository.findById(id)
//				.orElseThrow(() -> new EmployeeNotFoundException(id));
//	}
//
//	@PutMapping("/employees/{id}")
//	Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Integer id) {
//
//		return repository.findById(id)
//				.map(employee -> {
//					employee.setName(newEmployee.getName());
//					employee.setRole(newEmployee.getRole());
//					return repository.save(employee);
//				})
//				.orElseGet(() -> {
//					newEmployee.setId(id);
//					return repository.save(newEmployee);
//				});
//	}
//
//	@DeleteMapping("/employees/{id}")
//	void deleteEmployee(@PathVariable Integer id) {
//		repository.deleteById(id);
//	}
//}
