package UI;

import Classes.Users.User;
import DataBase.Service.UsuarioService;
import Exceptions.UsuarioNoEncontradoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// ventana para entrar a la app o registrarse como artista. usa el usuarioservice contra la base
public class VentanaLogin extends JFrame {
    private JTextField campoEmail;
    private JPasswordField campoPassword;

    public VentanaLogin() {
        setTitle("pix - iniciar sesion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("email:"));
        campoEmail = new JTextField();
        add(campoEmail);

        add(new JLabel("password:"));
        campoPassword = new JPasswordField();
        add(campoPassword);

        JButton botonLogin = new JButton("iniciar sesion");
        botonLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iniciarSesion();
            }
        });
        add(botonLogin);

        JButton botonRegistro = new JButton("registrarme");
        botonRegistro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarme();
            }
        });
        add(botonRegistro);

        setSize(360, 160);
        setLocationRelativeTo(null);
    }

    // busca el usuario en la base y abre el menu principal con su rol
    private void iniciarSesion() {
        String email = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword());

        try {
            UsuarioService usuarios = new UsuarioService();
            User user = usuarios.login(email, password);
            dispose();
            new VentanaPrincipal(user).setVisible(true);
        } catch (UsuarioNoEncontradoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "login", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "no se pudo conectar con la base de datos", "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // registro abierto: crea una cuenta de artista pidiendo los datos
    private void registrarme() {
        String nombre = JOptionPane.showInputDialog(this, "nombre:");
        String email = JOptionPane.showInputDialog(this, "email:");
        String password = JOptionPane.showInputDialog(this, "password (minimo 4 caracteres):");
        String bio = JOptionPane.showInputDialog(this, "bio (puede quedar vacia):");

        if (nombre == null || email == null || password == null) {
            return;
        }
        try {
            UsuarioService usuarios = new UsuarioService();
            usuarios.registrarArtista(nombre, email, password, bio);
            JOptionPane.showMessageDialog(this, "cuenta creada, ya podes iniciar sesion");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "registro", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "no se pudo conectar con la base de datos", "error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
