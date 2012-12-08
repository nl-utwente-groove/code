/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id$
 */
package groove.io.conceptual.lang;

public class Message {
    public enum MessageType {
        MSG,
        WARN,
        ERROR
    }

    private String m_message;
    private MessageType m_messageType;

    public Message(String message) {
        m_message = message;
        m_messageType = MessageType.MSG;
    }

    public Message(String message, MessageType messageType) {
        m_message = message;
        m_messageType = messageType;
    }

    @Override
    public String toString() {
        String message = "";
        switch (m_messageType) {
            case MSG:
                message += "[MSG] ";
                break;
            case WARN:
                message += "[WARN] ";
                break;
            case ERROR:
                message += "[ERROR] ";
                break;
        }
        message += m_message;
        return message;
    }
}
