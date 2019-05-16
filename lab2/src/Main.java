import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;

import java.io.*;
import java.net.InetAddress;
import java.util.List;

public class Main extends ReceiverAdapter {
    private JChannel channel;
    private final StringMap state = new StringMap();

    public void getState(OutputStream output) throws Exception {
        synchronized (state) {
            Util.objectToStream(state.serialize(), new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        String list = (String) Util.objectFromStream(new DataInputStream(input));

        synchronized (state) {
            state.clear();
            state.deserialize(list);
        }

        System.out.println("Received state: \n" + state.toString());
    }

    public void viewAccepted(View view) {
        if (view instanceof MergeView) {
            ViewHandler handler = new ViewHandler(channel, (MergeView) view);
            handler.start();
        }
    }

    public void receive(Message msg) {
        synchronized (state) {
            state.clear();
            state.deserialize(msg.getObject().toString());
        }

        System.out.println("Items synced!");
    }

    private void start() throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");

        channel = new JChannel();

        ProtocolStack stack = new ProtocolStack();
        channel.setProtocolStack(stack);
        stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName("230.100.200.10")))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new FRAG2());

        stack.init();

        channel.setReceiver(this);
        channel.connect("ChatCluster");
        channel.getState(null, 10000);
        eventLoop();
        channel.close();
    }

    private void eventLoop() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("1) 1 'key' 'value' - add an item\n" +
                "2) 2 - show table contents\n" +
                "3) 3 'key' - remove an item by key value\n" +
                "4) 4 'key' - check if a key exists\n" +
                "5) 5 'key' - get an item by the key\n");

        while (true) {
            try {
                String line = in.readLine().toLowerCase();
                String[] lineArray = line.split(" ");

                if (line.startsWith("quit") || line.startsWith("exit"))
                    break;

                if (("1".equals(lineArray[0]) && lineArray.length == 3)) {
                    synchronized (state) {
                        state.put(lineArray[1], Integer.parseInt(lineArray[2]));
                    }

                    System.out.println("Item added!");

                    Message msg = new Message(null, null, state.serialize());
                    channel.send(msg);
                } else if ("2".equals(lineArray[0])) {
                    System.out.print(state.toString());
                } else if ("3".equals(lineArray[0]) && lineArray.length == 2) {
                    synchronized (state) {
                        System.out.println(state.remove(lineArray[1]) + " removed!");
                    }
                } else if ("4".equals(lineArray[0]) && lineArray.length == 2) {
                    if (state.containsKey(lineArray[1]))
                        System.out.println("HashMap contains given key!");
                    else
                        System.out.println("HashMap does not contain given key!");
                } else if ("5".equals(lineArray[0]) && lineArray.length == 2) {
                    System.out.println(state.get(lineArray[1]));
                } else {
                    System.out.println("Unknown command!\n");
                }
            } catch (Exception e) {
                System.out.println("Exception");
            }
        }
    }

    private static class ViewHandler extends Thread {
        JChannel ch;
        MergeView view;

        private ViewHandler(JChannel ch, MergeView view) {
            this.ch = ch;
            this.view = view;
        }

        public void run() {
            List<View> subGroups = view.getSubgroups();
            View firstSub = subGroups.get(0);
            Address local_addr = ch.getAddress();

            if (!firstSub.getMembers().contains(local_addr)) {
                try {
                    ch.getState(null, 30000);
                } catch (Exception ignored) {

                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Main().start();
    }
}