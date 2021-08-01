package dotsandboxes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class DnBClient {
	String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 50);
    public  int DOT_NUMBER;

    /**
     * Constructs the client by laying out the GUI and registering a listener with
     * the textfield so that pressing Return in the listener sends the textfield
     * contents to the server. Note however that the textfield is initially NOT
     * editable, and only becomes editable AFTER the client receives the
     * NAMEACCEPTED message from the server.
     */
    public DnBClient(String serverAddress) {
        this.serverAddress = serverAddress;

//        textField.setEditable(false);
//        messageArea.setEditable(false);
//        frame.getContentPane().add(textField, BorderLayout.SOUTH);
//        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
//        frame.pack();
//
//        // Send on enter then clear to prepare for next message
//        textField.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                out.println(textField.getText());
//                textField.setText("");
//            }
//        });
    }

    private String getUserName() {
        return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection",
                JOptionPane.PLAIN_MESSAGE);
    }
    private int getDotSize() {
        return Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of dots in Rows and Colums:", "Screen name selection",
                JOptionPane.PLAIN_MESSAGE));
    }

    private void run() throws IOException {
        try {
        	Socket socket = new Socket(serverAddress, 5000);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("USERNAME")) {
                    out.println(getUserName());
                } else if (line.startsWith("ASKDOTSIZE")) {
                	int dotnum=getDotSize();
                	out.println(dotnum);
                } else if (line.startsWith("DOTSIZE")) {
                	this.DOT_NUMBER = Integer.parseInt(line.substring(8));
                	System.out.println(DOT_NUMBER);
                }
            }

        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
       
        DnBClient client = new DnBClient("127.0.0.1");
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
