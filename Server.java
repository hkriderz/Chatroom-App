import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {

    //For storing client names, checks for duplicates
    private static Set<String> clientNames = new HashSet<>();
    // This is a set of all the client's print writers
    private static Set<PrintWriter> clientsPW = new HashSet<>();

    public static void main(String[] args) throws Exception
    {
        System.out.println("Server waiting for Clients on port 1500");
        try (ServerSocket serverSocket = new ServerSocket(1500))
        {
            while (true)
            {
                Thread thread = new Thread(new ClientHandler(serverSocket.accept()));
                thread.start();
            }
        }
    }
    private static class ClientHandler implements Runnable
    {
        private String userName;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        String time;

        public ClientHandler(Socket socket)
        {
            this.socket = socket;
        }
        public void run()
        {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true)
                {
                    out.println("USERNAME");
                    userName = in.nextLine();
                    if (userName == null)
                    {
                        return;
                    }
                    //If the name is not blank and if it is unique add it to names
                    if (!userName.isEmpty() && !clientNames.contains(userName))
                    {
                        clientNames.add(userName);
                        break;
                    }
                }
                out.println("NAMECONFIRMED " + userName);
                time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                System.out.println("[" + time + "] " + userName + " has joined the server.");
                //Using the set of print writers, broadcast to all clients
                for (PrintWriter writer : clientsPW)
                {
                    time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                    writer.println("MESSAGE " + "[" + time + "] " + userName + " has joined");
                }
                clientsPW.add(out);

                //Broadcast welcome message to the new client
                for (PrintWriter writer : clientsPW)
                {
                    if(writer == out)
                    {
                        time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                        writer.println("MESSAGE " + "Welcome to the server " + userName + ".");
                    }
                }

                while (true)
                {
                    String input = in.nextLine();
                    time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                    System.out.println("[" + time + "] " + userName + ": " + input);

                    for (PrintWriter writer : clientsPW)
                    {
                        time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                        writer.println("MESSAGE " + "[" + time + "] " + userName + ": " + input);
                    }
                    if (input.equals("."))
                    {
                        return;
                    }
                    else if(input.equals("!users"))
                    {
                        for (PrintWriter writer : clientsPW)
                        {
                            if(writer == out)
                            {
                                for (String clients : clientNames)
                                {
                                    writer.println("MESSAGE " + clients);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            finally
            {
                // Removing the client's name and print writer from the sets
                if (out != null)
                {
                    clientsPW.remove(out);
                }
                if (userName != null)
                {
                    time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                    System.out.println("[" + time + "] " + userName + " has left the server.");
                    clientNames.remove(userName);
                    for (PrintWriter writer : clientsPW)
                    {
                        time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
                        writer.println("MESSAGE " + "[" + time + "] " + ": " + userName + " has left the server.");
                    }
                }
                try
                {
                    //close the socket
                    socket.close();
                }
                catch (IOException e)
                {

                }
            }
        }
    }
}