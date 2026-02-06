package zk_os;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardingController {
//	@RequestMapping(value = "/{path:[^.]*}")
//	@RequestMapping("/{path:[^\\.]+}/**")
	@RequestMapping("/{path:.+}/**")
	public String forward() {
		return "forward:/";
	}
}
