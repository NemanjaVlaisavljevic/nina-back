package com.nemanjav.back.email;

public interface EmailSender {

    void send(String to , String email);
    void sendOrderDetails(String to, String email);

    void sendCanceledOrder(String s, String buildEmail);
}
