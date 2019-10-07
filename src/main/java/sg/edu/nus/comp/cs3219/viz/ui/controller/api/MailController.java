package sg.edu.nus.comp.cs3219.viz.ui.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sg.edu.nus.comp.cs3219.viz.common.datatransfer.UserInfo;
import sg.edu.nus.comp.cs3219.viz.common.entity.Mail;
import sg.edu.nus.comp.cs3219.viz.common.exception.MailMessageException;
import sg.edu.nus.comp.cs3219.viz.logic.GateKeeper;
import sg.edu.nus.comp.cs3219.viz.logic.MailLogic;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
public class MailController extends BaseRestController {
    private static final Logger log = Logger.getLogger(MailController.class.getSimpleName());

    private final MailLogic mailLogic;

    private final GateKeeper gateKeeper;

    public MailController(MailLogic mailLogic, GateKeeper gateKeeper) {
        this.mailLogic = mailLogic;
        this.gateKeeper = gateKeeper;
    }

    @PostMapping("/send-mail")
    public ResponseEntity<?> sendMail(@RequestParam("mail") String mailStringJson, @RequestParam(value = "files", required=false) MultipartFile attachment) {
        UserInfo currentUser = gateKeeper.verifyLoginAccess();

        log.info(currentUser.getUserEmail() + " request to send mail received.");

        try {
            Mail mailRequest = new ObjectMapper().readValue(mailStringJson, Mail.class);
            mailRequest.setAttachment(attachment);
            this.mailLogic.sendMessage(mailRequest);
        } catch (IOException ex) {
            log.info("Error sending mail: " + ex.getMessage());
            throw new MailMessageException();
        }

        return ResponseEntity.ok().body("Mail has been sent successfully.");
    }
}
