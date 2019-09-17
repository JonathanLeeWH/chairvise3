package sg.edu.nus.comp.cs3219.viz.ui.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.nus.comp.cs3219.viz.logic.MailLogic;

import java.util.logging.Logger;

@RestController
public class MailController extends BaseRestController {
    private static final Logger log = Logger.getLogger(MailController.class.getSimpleName());

    private MailLogic mailLogic;

    public MailController(MailLogic mailLogic) {
        this.mailLogic = mailLogic;
    }

    @RequestMapping("/send-mail")
    public ResponseEntity<?> sendMail() {
        this.mailLogic.sendMessage();

        return ResponseEntity.accepted().build();
    }
}
