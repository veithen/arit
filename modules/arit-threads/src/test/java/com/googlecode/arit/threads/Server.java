/*
 * Copyright 2010-2011 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.arit.threads;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private ServerSocket serverSocket;
    private Thread thread;
    
    public void start() throws IOException {
        serverSocket = new ServerSocket(0);
        // Note: the purpose of this code is to check that the plugin also works
        // for inner classes that access the ServerSocket in the outer class.
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    serverSocket.accept();
                } catch (IOException ex) {
                    // We get here when the socket is closed.
                }
            }
        });
        thread.start();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }
    
    // A normal acceptor thread wouldn't have this method, but we need it for the test
    public Thread getThread() {
        return thread;
    }
    
    public void stop() throws IOException {
        serverSocket.close();
    }
}
