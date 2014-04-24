/**
 * *****************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the GNU Public License v2.0 which accompanies this
 * distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme European
 * Commission - Directorate-General Home Affairs
 *
 * Contributors: Research and Academic Computer Network
 * ****************************************************************************
 */
package pl.nask.nisha.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketProxy implements Runnable {

    private ServerSocket serverSocket;
    private static final Logger logger = LoggerFactory.getLogger(SocketProxy.class);
//    private int index = 0;
//    private NodeReader nodeReader;
//    private boolean forwarding;

    public SocketProxy(int proxyPort) {
        try {
            serverSocket = new ServerSocket(proxyPort);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    /*public SocketProxy(String couchdbHost, int couchdbPort, String username, String password, int proxyPort, boolean forwarding) {
        this.nodeReader = new NodeReader(couchdbHost, couchdbPort, username, password);
        this.forwarding = forwarding;
        try {
            serverSocket = new ServerSocket(proxyPort);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }*/

    //TODO: make it thread-safe, super-node removal can cause hard to detect problems
    @Override
    public final void run() {
        logger.info("SocketProxy started");
//        if (forwarding) {
//            runEnabled();
//        } else {
            runDisabled();
//        }
    }

//    private void runEnabled() {
//        while (true) {
//            try {
//                Socket socket = serverSocket.accept();
//                List<NodeRingInfo> nodes = nodeReader.loadSuperNodeUris();
//                if (nodes.isEmpty()) {
//                    logger.warn("list of supernodes is empty");
//                    socket.close();
//                    continue;
//                }
//
//                NodeRingInfo nodeRingInfo = nodes.get(index++ % nodes.size());
//                String nodeUri = nodeRingInfo.getUri();
//                URI uri = new URI(nodeUri);
//                Runnable channel = new HttpChannel(socket, uri.getHost());
//                Thread t = new Thread(channel);
//                t.start();
//            } catch (URISyntaxException ex) {
//                logger.warn(ex.getMessage());
//            } catch (IOException ex) {
//                logger.error("socket I/O error: {}", ex);
//            }
//        }
//    }
    private void runDisabled() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                socket.close();
            } catch (IOException ex) {
                logger.error("socket I/O error: {}", ex);
            }
        }
    }
}
