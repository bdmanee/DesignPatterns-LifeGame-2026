
package com.holub.life;

import java.awt.*;
import javax.swing.*;

public final class NeonTheme {

    public static final Color BACKGROUND = new Color(11, 15, 26);
    public static final Color PANEL = new Color(23, 28, 46);
    public static final Color CYAN = new Color(0, 245, 212);
    public static final Color PURPLE = new Color(168, 85, 247);
    public static final Color PINK = new Color(255, 79, 216);
    public static final Color TEXT = new Color(245, 247, 255);
    public static final Color GRID = new Color(54, 43, 84);

    private NeonTheme() {
    }

    public static void applyMenuTheme(JMenuBar menuBar) {
        if (menuBar == null) {
            return;
        }

        menuBar.setOpaque(true);
        menuBar.setBackground(PANEL);
        menuBar.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 2, 0, PURPLE
                )
        );

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);

            if (menu != null) {
                styleMenu(menu);
            }
        }
    }

    private static void styleMenu(JMenu menu) {
        styleItem(menu);

        JPopupMenu popup = menu.getPopupMenu();
        popup.setOpaque(true);
        popup.setBackground(PANEL);
        popup.setBorder(
                BorderFactory.createLineBorder(PURPLE)
        );

        for (Component component : popup.getComponents()) {
            if (component instanceof JMenu) {
                styleMenu((JMenu) component);
            } else if (component instanceof JMenuItem) {
                styleItem((JMenuItem) component);
            } else if (component instanceof JSeparator) {
                component.setBackground(PANEL);
                component.setForeground(PURPLE);
            }
        }
    }

    private static void styleItem(JMenuItem item) {
        item.setOpaque(true);
        item.setBackground(PANEL);
        item.setForeground(TEXT);

        item.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );

        item.setBorderPainted(false);
    }
}

