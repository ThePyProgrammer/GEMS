module GEMS {
    requires javafx.controls;
    requires java.desktop;
    requires javafx.fxml;
    requires javafx.web;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    opens application.Model;
    opens application;
    opens application.Controller;
    opens application.Model.util;
}