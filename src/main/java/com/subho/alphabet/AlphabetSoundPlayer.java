package com.subho.alphabet;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AlphabetSoundPlayer {
    private static final Color BACKGROUND = new Color(245, 247, 251);
    private static final Color BUTTON_COLOR = new Color(71, 119, 219);
    private static final Color BUTTON_HOVER = new Color(53, 96, 186);
    private static final Color BUTTON_SELECTED = new Color(238, 154, 51);
    private Clip currentClip;
    private JButton selectedButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AlphabetSoundPlayer().showWindow());
    }

    private void showWindow() {
        JFrame window = new JFrame("Alphabet Sound Player");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setMinimumSize(new Dimension(600, 570));
        window.setSize(680, 620);
        window.setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(28, 34, 30, 34));

        JLabel title = new JLabel("Alphabet Sound Player", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(35, 48, 75));
        content.add(title, BorderLayout.NORTH);

        JLabel selectedLetter = new JLabel("Select a letter", SwingConstants.CENTER);
        selectedLetter.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        selectedLetter.setForeground(new Color(82, 94, 116));
        content.add(selectedLetter, BorderLayout.SOUTH);

        JPanel grid = new JPanel(new GridLayout(4, 7, 12, 12));
        grid.setOpaque(false);
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            grid.add(createLetterButton(letter, selectedLetter));
        }
        grid.add(new JLabel());
        grid.add(new JLabel());
        content.add(grid, BorderLayout.CENTER);

        window.setContentPane(content);
        installKeyboardShortcuts(window, selectedLetter);
        window.setVisible(true);
    }

    private JButton createLetterButton(char letter, JLabel selectedLetter) {
        JButton button = new JButton(String.valueOf(letter));
        button.setFont(new Font("Segoe UI", Font.BOLD, 26));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        button.setOpaque(true);
        button.addActionListener(event -> {
            selectAndPlay(letter, selectedLetter, button);
        });
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent event) {
                if (button != selectedButton) button.setBackground(BUTTON_HOVER);
            }
            @Override public void mouseExited(MouseEvent event) {
                if (button != selectedButton) button.setBackground(BUTTON_COLOR);
            }
        });
        return button;
    }

    private void installKeyboardShortcuts(JFrame window, JLabel selectedLetter) {
        JComponent root = window.getRootPane();
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            final char mappedLetter = letter;
            int keyCode = KeyEvent.VK_A + (letter - 'A');
            String actionName = "play-letter-" + mappedLetter;
            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(keyCode, 0), actionName);
            root.getActionMap().put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent event) {
                    selectAndPlay(mappedLetter, selectedLetter, findButton(window, mappedLetter));
                }
            });
        }
    }

    private JButton findButton(JFrame window, char letter) {
        for (java.awt.Component component : window.getContentPane().getComponents()) {
            if (component instanceof JPanel panel) {
                for (java.awt.Component child : panel.getComponents()) {
                    if (child instanceof JButton button && button.getText().equals(String.valueOf(letter))) {
                        return button;
                    }
                    if (child instanceof JPanel grid) {
                        for (java.awt.Component key : grid.getComponents()) {
                            if (key instanceof JButton button && button.getText().equals(String.valueOf(letter))) {
                                return button;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void selectAndPlay(char letter, JLabel selectedLetter, JButton button) {
        selectedLetter.setText("Selected letter: " + letter);
        if (selectedButton != null) selectedButton.setBackground(BUTTON_COLOR);
        selectedButton = button;
        if (selectedButton != null) selectedButton.setBackground(BUTTON_SELECTED);
        playLetter(letter);
    }

    private void playLetter(char letter) {
        stopCurrentClip();
        String resourceName = "/audio/" + letter + ".wav";
        try (InputStream resource = AlphabetSoundPlayer.class.getResourceAsStream(resourceName)) {
            if (resource == null) {
                System.err.println("Audio file is missing: " + resourceName);
                return;
            }
            try (AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(resource))) {
                currentClip = AudioSystem.getClip();
                currentClip.open(audio);
                currentClip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException exception) {
            System.err.println("Could not play " + letter + ": " + exception.getMessage());
        }
    }

    private void stopCurrentClip() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }
    }
}
