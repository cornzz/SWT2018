package flowershop.help;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Jonas Knobloch
 */
@Controller
public class FlowerShopHelpController {
	@RequestMapping("/help")
	@PreAuthorize("hasRole('ROLE_BOSS')")
	public String help() {
		return "help";
	}
}
