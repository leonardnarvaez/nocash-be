package com.champ.nocash.util.impl;

import com.champ.nocash.util.EmailService;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class EmailServiceImpl implements EmailService {
    @Value("${sendgrid.api.key}")
    private String apiKey;
    @Override
    public void sendMIMEMessage(String to, String subject, String text) throws IOException {
        Email from = new Email("cwallet.notification@gmail.com");
        Email receiver = new Email(to);
        Content content = new Content("text/html", text);
        Mail mail = new Mail(from, subject, receiver, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
    }
}
