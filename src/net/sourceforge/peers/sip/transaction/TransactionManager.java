/*
    This file is part of Peers.

    Peers is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    Peers is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    
    Copyright 2007 Yohann Martineau 
*/

package net.sourceforge.peers.sip.transaction;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Timer;

import net.sourceforge.peers.sip.RFC3261;
import net.sourceforge.peers.sip.Utils;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldName;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldValue;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderParamName;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;


public class TransactionManager {

    private static TransactionManager INSTANCE;

    public static TransactionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TransactionManager();
        }
        return INSTANCE;
    }

    protected Timer timer;
    
    private Hashtable<String, ClientTransaction> clientTransactions;
    private Hashtable<String, ServerTransaction> serverTransactions;

    private TransactionManager() {
        clientTransactions = new Hashtable<String, ClientTransaction>();
        serverTransactions = new Hashtable<String, ServerTransaction>();
        timer = new Timer("Transaction timer");
    }

    public ClientTransaction createClientTransaction(SipRequest sipRequest,
            InetAddress inetAddress, int port, String transport,
            ClientTransactionUser clientTransactionUser) {
        String branchId = Utils.getInstance().generateBranchId();
        String method = sipRequest.getMethod();
        ClientTransaction clientTransaction;
        if (RFC3261.METHOD_INVITE.equals(method)) {
            clientTransaction = new InviteClientTransaction(branchId,
                    inetAddress, port, transport, sipRequest, clientTransactionUser);
        } else {
            clientTransaction = new NonInviteClientTransaction(branchId,
                    inetAddress, port, transport, sipRequest, clientTransactionUser);
        }
        clientTransactions.put(getTransactionId(branchId, method),
                clientTransaction);
        return clientTransaction;
    }

    public ServerTransaction createServerTransaction(SipResponse sipResponse,
            int port, String transport,
            ServerTransactionUser serverTransactionUser,
            SipRequest sipRequest) {
        SipHeaderFieldValue via = Utils.getInstance().getTopVia(sipResponse);
        String branchId = via.getParam(new SipHeaderParamName(
                RFC3261.PARAM_BRANCH));
        String cseq = sipResponse.getSipHeaders().get(
                new SipHeaderFieldName(RFC3261.HDR_CSEQ)).toString();
        String method = cseq.substring(cseq.lastIndexOf(' ') + 1);
        ServerTransaction serverTransaction;
        // TODO create server transport user and pass it to server transaction
        if (RFC3261.METHOD_INVITE.equals(method)) {
            serverTransaction = new InviteServerTransaction(branchId, port,
                    transport, sipResponse, serverTransactionUser);
            // serverTransaction = new InviteServerTransaction(branchId);
        } else {
            serverTransaction = new NonInviteServerTransaction(branchId, port,
                    transport, method, serverTransactionUser, sipRequest);
        }
        serverTransactions.put(getTransactionId(branchId, method),
                serverTransaction);
        return serverTransaction;
    }

    public ClientTransaction getClientTransaction(SipResponse sipResponse) {
        SipHeaderFieldValue via = Utils.getInstance().getTopVia(sipResponse);
        String branchId = via.getParam(new SipHeaderParamName(
                RFC3261.PARAM_BRANCH));
        String cseq = sipResponse.getSipHeaders().get(
                new SipHeaderFieldName(RFC3261.HDR_CSEQ)).toString();
        String method = cseq.substring(cseq.lastIndexOf(' ') + 1);
        return clientTransactions.get(getTransactionId(branchId, method));
    }

    public ServerTransaction getServerTransaction(SipRequest sipRequest) {
        SipHeaderFieldValue via = Utils.getInstance().getTopVia(sipRequest);
        String branchId = via.getParam(new SipHeaderParamName(
                RFC3261.PARAM_BRANCH));
        String method = sipRequest.getMethod();
        if (RFC3261.METHOD_ACK.equals(method)) {
            method = RFC3261.METHOD_INVITE;
            // TODO if positive response, ACK does not belong to transaction
            // retrieve transaction and take responses from transaction
            // and check if a positive response has been received
            // if it is the case, a new standalone transaction must be created
            // for the ACK
        }
        return serverTransactions.get(getTransactionId(branchId, method));
    }

    void removeServerTransaction(String branchId, String method) {
        serverTransactions.remove(getTransactionId(branchId, method));
    }
    
    private String getTransactionId(String branchId, String method) {
        StringBuffer buf = new StringBuffer();
        buf.append(branchId);
        buf.append(Transaction.ID_SEPARATOR);
        buf.append(method);
        return buf.toString();
    }

}