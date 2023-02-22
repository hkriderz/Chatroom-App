import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client
{
    JFrame frame;
    JScrollPane pane;
    JTextArea chatArea;
    JPanel panel;
    JTextField textInput;
    JButton send;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem exit;
    Scanner in;
    PrintWriter out;
    String server;

    Client(String server)
    {
        this.server = server;
        frame = new JFrame("Chat Client");
        frame.setSize(640,480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea  = new JTextArea(20,20);
        chatArea.setEditable(false);
        pane = new JScrollPane(chatArea);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(pane,BorderLayout.CENTER);

        panel = new JPanel();
        textInput = new JTextField(40);
        textInput.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                textInput.setText("");
            }
        });
        textInput.setEditable(false);
        send = new JButton("Send");
        send.setEnabled(false);
        send.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String text = textInput.getText();
                out.println(text);
                textInput.setText("");
            }
        });
        send.addKeyListener(new KeyListener()
        {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    String text = textInput.getText();
                    out.println(text);
                    textInput.setText("");
                }
            }
            public void keyReleased(KeyEvent e)
            {

            }
        });
        panel.add(textInput);
        panel.add(send);
        frame.add(panel,BorderLayout.SOUTH);


        menu = new JMenu("File");
        exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int result = JOptionPane.showConfirmDialog(null, "Are you sure?", "Exit", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION)
                {
                    frame.dispose();
                }
            }
        });
        menu.add(exit);
        menuBar = new JMenuBar();
        menuBar.add(menu);

        frame.setJMenuBar(menuBar);
        frame.getRootPane().setDefaultButton(send);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private static String getServerAddress()
    {
        String response = JOptionPane.showInputDialog(null, "Server IP:", "Connect to Server", JOptionPane.PLAIN_MESSAGE);
        if (response == null)
        {
            System.exit(0);
        }
        return response;
    }
    private void start() throws IOException {
        try {
            Socket socket = new Socket(server, 1500);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("USERNAME"))
                {
                    JPanel panel = new JPanel();
                    JLabel text = new JLabel("Enter your Username: ");
                    JTextField name = new JTextField(8);
                    panel.add(text);
                    panel.add(name);
                    int result = JOptionPane.showConfirmDialog(null, panel, "Enter Username", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION)
                    {
                        if(name.getText().isEmpty())
                        {
                            Random rand = new Random();
                            int random = rand.nextInt();
                            String rName = "Anonymous-" + random;
                            out.println(rName);
                        }
                        else
                        {
                            out.println(name.getText());
                        }
                    }
                    else
                    {
                        System.exit(0);
                    }
                }
                else if (line.startsWith("NAMECONFIRMED"))
                {
                    textInput.setEditable(true);
                    textInput.setText("Enter Message..");
                    send.setEnabled(true);
                }
                else if (line.startsWith("MESSAGE"))
                {
                    chatArea.append(line.substring(8) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }
    public static void main(String[] args) throws Exception
    {
        Client client = new Client(getServerAddress());
        client.start();
    }

}
