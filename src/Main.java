import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static GUI gui;

    public static void main(String[] Args) {
        gui = new GUI();
        gui.initDisplay(500,30,500,1000,"随便写");
    }
}