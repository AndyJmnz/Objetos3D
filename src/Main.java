import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.Canvas3D;
import javax.swing.*;
import java.awt.*;
public class Main {
    public static void main(String[] args) {
        System.setProperty("sun.awt.noerasebackground", "true");
        JFrame ventana = new JFrame("Iluminacion");
        //iluminacion panel = new iluminacion();
        Practica10 panel = new Practica10();
        //Objetos3D panel = new Objetos3D();
        ventana.add(panel);
        ventana.setSize(800, 400);
        ventana.setLocationRelativeTo(null);
        //ventana.setResizable(false);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);
    }
}
