// GOKUL UPADHYAY GURAGAIN (NP069822)
package JavaFiles;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class LandingPage extends JFrame {
    private JLabel titleLabel, lbefLogo, apuLogo;
    private Timer timer;
    private int titleY = 1200;  // Start from the bottom
    private int lbefX = -500;   // Start from the left infinity
    private boolean showLoginForm = false;

    public LandingPage() {
        // Frame properties
        setTitle("University Food Ordering System");
        setSize(1920, 1200);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);

        // Title label
        titleLabel = new JLabel("University Food Ordering System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setBounds(460, titleY, 1000, 60);
        add(titleLabel);

        // LBEF Logo
        lbefLogo = new JLabel(new ImageIcon("src/Images/LBEF.png"));
        lbefLogo.setBounds(lbefX, 100, 200, 100);
        add(lbefLogo);

        // Timer for animations
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int timeElapsed = 0;

            @Override
            public void run() {
                if (timeElapsed < 3000) {
                    titleY -= 10;  // Move title upwards
                    titleLabel.setBounds(460, titleY, 1000, 60);
                } else if (timeElapsed >= 3000 && timeElapsed < 5000) {
                    lbefX += 50;  // Move LBEF logo from left
                    lbefLogo.setBounds(775, 350, 400, 200);
                } else {
                    showLoginForm = true;
                    timer.cancel(); // Stop animation
                    openLoginPage(); // Redirect
                }
                timeElapsed += 50;  // Increase time step
            }
        }, 0, 50);

        setVisible(true);
    }

    private void openLoginPage() {
        dispose(); // Close landing page
        new LoginSignup(); // Open Login/Signup page
    }
}
