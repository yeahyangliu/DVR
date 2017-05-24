import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map.Entry;

public class Dvr {

    private char nodeN;
    private int portN;
    private String fileN;
    private Hashtable<Character, Neighbour> neighbours;
    private DatagramSocket socket;

    public Dvr(char nodeName, int portNum, String fileName) throws IOException {

        nodeN = nodeName;
        portN = portNum;
        fileN = fileName;
        neighbours = new Hashtable<Character, Neighbour>();

        try {
            if (!loadFile()) {
                System.err.println("Error loadFile failed");
            } else {

                for (Entry<Character, Neighbour> current : neighbours.entrySet()) {
                    System.out.println("neighbour:" + current.getKey() + " : " + current.getValue().getLinkLength() + " : " + current.getValue().getPortNum());

                }
                System.out.println("Constructor closing");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean loadFile() throws IOException {

        //create seperate method for poison reverse (i.e 2 values);

        File configFile = new File("./" + fileN);
        if (!configFile.exists()) {

            System.err.println("Error: File Not Found");
            return false;

        } else {

            String line = null;

            try {

                BufferedReader reader = new BufferedReader(new FileReader(configFile));
                line = reader.readLine();
                System.out.println("number of neighbours = 2");

                while ((line = reader.readLine()) != null) {

                    String neighbourData[] = line.split(" ");

                    for (String temp : neighbourData) {
                        System.out.println(temp);
                    }

                    char neighbourName = neighbourData[0].charAt(0);
                    int neighbourLink = Integer.parseInt(neighbourData[1]);
                    int neighbourPortNum = Integer.parseInt(neighbourData[2]);
                    neighbours.put(neighbourName, new Neighbour(neighbourName, neighbourLink, neighbourPortNum));

                }
                reader.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void runDvr() throws IOException {

        socket = new DatagramSocket(portN);
        BufferedReader fromStdin = new BufferedReader(new InputStreamReader(System.in));

        try {
            String commands;
            while (!(commands = fromStdin.readLine()).equals("end")) {

                //sending out data to neighbours
                if (commands.equals("send")) {
                    SendDV();
                }

                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                //socket will wait indefinitely until a packet is received
                System.out.println("waiting on packets....");
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("message recieved from: " + packet.getPort() + " : " + received);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SendDV() throws UnknownHostException {
        try {
            for (Entry<Character, Neighbour> current : neighbours.entrySet()) {
                int portNum = current.getValue().getPortNum();
                StringBuilder builder = new StringBuilder();
                String message = builder.append(nodeN + " on " + portN + " with distance: " + current.getValue().getLinkLength()).toString();
                byte[] buffer = message.getBytes();
                //InetAddress address = 127.0.0.1;
                InetAddress address = InetAddress.getLocalHost();
                System.out.println("===");
                System.out.println(message);
                System.out.println("===");
                DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address, portNum);

                try {
                    socket.send(sendPacket);
                    System.out.println("message sent successfully");
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
            System.out.println("sending concluded");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {

        char nodeName = args[0].charAt(0);
        int portNum = Integer.parseInt(args[1]);
        String fileName = args[2];
        System.out.println(nodeName + " " + portNum + " " + fileName);
        Dvr node = new Dvr(nodeName, portNum, fileName);
        System.out.println("running dvr");
        node.runDvr();


    }
}
