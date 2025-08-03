module pro.tremblay {
    requires java.desktop;
    requires javafx.controls;
    requires javafaker;
    // needed by javafaker
    requires java.sql;
    exports pro.tremblay.multimemory;
}
