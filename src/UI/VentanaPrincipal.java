package UI;

import Classes.Users.Administrator;
import Classes.Users.Artist;
import Classes.Users.User;
import DataBase.Service.UsuarioService;
import Enum.RolUsuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// menu principal de la app. muestra los botones segun el rol del usuario logueado
public class VentanaPrincipal extends JFrame {
    private User user;

    public VentanaPrincipal(User user) {
        this.user = user;
        setTitle("pix - hola " + user.getNombre());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1, 10, 10));

        JButton botonFeed = new JButton("ver feed");
        botonFeed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VentanaFeed(user, false).setVisible(true);
            }
        });
        add(botonFeed);

        // solo los artistas pueden publicar y tener obras propias
        if (user.getRol() == RolUsuario.ARTISTA) {
            JButton botonPublicar = new JButton("publicar obra");
            botonPublicar.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new VentanaPublicar(user).setVisible(true);
                }
            });
            add(botonPublicar);

            JButton botonMisObras = new JButton("mis obras");
            botonMisObras.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new VentanaFeed(user, true).setVisible(true);
                }
            });
            add(botonMisObras);
        }

        JButton botonColecciones = new JButton("mis colecciones");
        botonColecciones.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new VentanaColecciones(user).setVisible(true);
            }
        });
        add(botonColecciones);

        JButton botonPerfil = new JButton("editar mis datos");
        botonPerfil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarMisDatos();
            }
        });
        add(botonPerfil);

        // el administrador tiene su propio panel
        if (user.getRol() == RolUsuario.ADMINISTRADOR) {
            JButton botonAdmin = new JButton("panel de administrador");
            botonAdmin.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new VentanaAdmin(user).setVisible(true);
                }
            });
            add(botonAdmin);
        }

        JButton botonSalir = new JButton("cerrar sesion");
        botonSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new VentanaLogin().setVisible(true);
            }
        });
        add(botonSalir);

        setSize(340, 460);
        setLocationRelativeTo(null);
    }

    // el usuario cambia su nombre, email y password. el servicio revisa que solo toque su propia cuenta
    private void editarMisDatos() {
        String nombre = JOptionPane.showInputDialog(this, "nombre:", user.getNombre());
        String email = JOptionPane.showInputDialog(this, "email:", user.getEmail());
        String password = JOptionPane.showInputDialog(this, "password:", user.getPassword());

        if (nombre == null || email == null || password == null) {
            return;
        }

        User modificado;
        if (user instanceof Artist) {
            String bio = JOptionPane.showInputDialog(this, "bio:", ((Artist) user).getBio());
            modificado = new Artist(user.getId(), nombre.trim(), email.trim(), password, user.getRol(), bio);
        } else {
            modificado = new Administrator(user.getId(), nombre.trim(), email.trim(), password, user.getRol());
            ((Administrator) modificado).setNivelAcceso(((Administrator) user).getNivelAcceso());
        }
        modificado.setFechaRegistro(user.getFechaRegistro());

        try {
            UsuarioService usuarios = new UsuarioService();
            usuarios.actualizar(user, modificado);
            user = modificado;
            JOptionPane.showMessageDialog(this, "datos actualizados");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
