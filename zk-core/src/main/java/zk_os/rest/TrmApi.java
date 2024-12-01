package zk_os.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//@Tag(
//		name = TrmApi.TAG_API,
//		description = "Run Command")
//@Validated
@RequestMapping("/trm")
public interface TrmApi {

//	String TAG_API = "trm-api";

	//	@Operation(summary = "Run Command ", description = "", tags = {TAG_API})
//	@ApiResponses(value = {//
//			@ApiResponse(responseCode = "200", description = "Успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),//
//			@ApiResponse(responseCode = "404", description = API.NOT_FOUND_404)//
//	})
	@GetMapping(value = "exe", produces = {MediaType.APPLICATION_JSON_VALUE})
	ResponseEntity exe(@RequestParam String q) throws Throwable;

	@GetMapping(value = "ping", produces = {MediaType.APPLICATION_JSON_VALUE})
	ResponseEntity ping() throws Throwable;

//	@Operation(summary = "Gen Rest Entity", description = "", tags = {TAG_API})
//	@ApiResponses(value = {
//			@ApiResponse(responseCode = "200", description = "Успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),//
//			@ApiResponse(responseCode = "404", description = API.NOT_FOUND_404)//
//	})
////	@PostMapping(value = "gen", produces = {MediaType.APPLICATION_JSON_VALUE})
////	ResponseEntity gen(@Valid @RequestBody GenEntity genEntity);
//	@PostMapping(value = "gen", produces = {MediaType.TEXT_PLAIN_VALUE})
//	ResponseEntity gen(@Valid @RequestBody String genEntity);


}
