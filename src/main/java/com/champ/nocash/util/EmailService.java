package com.champ.nocash.util;


import java.io.IOException;

public interface EmailService {
    void sendMIMEMessage(String to, String subject, String text) throws IOException;
}
