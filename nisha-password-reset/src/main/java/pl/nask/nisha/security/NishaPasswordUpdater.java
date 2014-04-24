/*******************************************************************************
 * Copyright (c) 2012 Research and Academic Computer Network.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * With financial support from the Prevention, Preparedness and Consequence
 * Management of Terrorism and other Security Related Risks Programme
 * European Commission - Directorate-General Home Affairs
 *
 * Contributors:
 *     Research and Academic Computer Network
 ******************************************************************************/
package pl.nask.nisha.security;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.nisha.commons.config.ConfigPropertyName;
import pl.nask.nisha.commons.config.NodeManagerFileConfig;
import pl.nask.nisha.commons.node.Operator;
import pl.nask.nisha.commons.security.PasswordValidator;

public class NishaPasswordUpdater implements PasswordUpdater{

    public static final Logger LOG = LoggerFactory.getLogger(NishaPasswordUpdater.class);
    private Operator operator;
    private String operatorId;
    private char[] password1;
    private char[] password2;

    private Console console;
    private CouchDbClient operatorClient;
    private NodeManagerFileConfig nodeConfig = NodeManagerFileConfig.getNodeManagerFileConfig(true);
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private String dbname;

    private final String operatorLabel = "type operator id:";
    private final String passwordLabel1 = "type password:";
    private final String passwordLabel2 = "type password again:";

    public NishaPasswordUpdater(String nishaLocalDatabaseUri, Console console, boolean devEnabled) {
        constructConsole(console, devEnabled);
        constructDbClient(nishaLocalDatabaseUri);
    }

    @Override
    public boolean processUpdatePasswordHash() {
        boolean success = loadAndValidateCredentials();
        if (success) {
            return doUpdate();
        } else {
            commentAndDecide("cannot update password for operator: " + operatorId);
        }
        return false;
    }

    public void constructConsole(Console console, boolean devEnabled) {
        if (console == null) {
            String consoleStatus = "Console NOT available";
            if (devEnabled) {
                LOG.info(consoleStatus + " - working in development mode");
            }
            else{
                commentAndExit(consoleStatus + "\nExit.");
            }
        } else {
            LOG.info("Console IS available");
        }
        this.console = console;

    }

    public void constructDbClient(String dbUri) {
        try {
            String protocol, host, user, pass;
            int port;
            String uriFormatProblemInfo = "Unexpected database uri format: " + dbUri
                                    + " (expected format is \"protocol://host:port/dbname)\"";
            String[] protocolAndRest = dbUri.split("://");
            if (protocolAndRest.length != 2) {
                throw new IllegalArgumentException(uriFormatProblemInfo);
            } else {
                protocol = protocolAndRest[0];
                String[] hostPortAndRest = protocolAndRest[1].split("/");
                if (hostPortAndRest.length != 2) {
                    throw new IllegalArgumentException(uriFormatProblemInfo);
                }
                else {
                    dbname = hostPortAndRest[1];
                    String[] hostAndPort = hostPortAndRest[0].split(":");
                    if (hostAndPort.length != 2) {
                        throw new IllegalArgumentException(uriFormatProblemInfo);
                    } else {
                        host = hostAndPort[0];
                        port = Integer.parseInt(hostAndPort[1]);
                        user = nodeConfig.getPropertyValue(ConfigPropertyName.COUCHDB_NISHA_USER, "nisha");
                        pass = nodeConfig.getPropertyValue(ConfigPropertyName.COUCHDB_NISHA_PASSWORD, "nisha");

                        operatorClient = new CouchDbClient(dbname, false, protocol, host, port, user, pass);
                    }
                }
            }
        } catch (Exception e) {
            commentAndExit(e.getMessage());
        }
    }

    public boolean loadAndValidateCredentials() {
        resetMembers();
        if (console != null) {
            loadCredentialsFromConsole();
        } else {
            loadCredentialsFromInputStream();
        }
        return validateOperatorCredentials();
    }

    public void loadCredentialsFromConsole() {
        String stringFormat = "%s";
        operatorId = console.readLine(stringFormat, operatorLabel);
        password1 = console.readPassword(stringFormat, passwordLabel1);
        password2 = console.readPassword(stringFormat, passwordLabel2);
    }

    public void loadCredentialsFromInputStream() {
        try{
            LOG.info(operatorLabel);
            operatorId = reader.readLine();
            LOG.info(passwordLabel1);
            password1 = reader.readLine().toCharArray();
            LOG.info(passwordLabel2);
            password2 = reader.readLine().toCharArray();
        }
        catch (IOException e) {
            commentAndExit("Cannot read from input stream. Exit.");
        }
    }

    public boolean validateOperatorCredentials() {
        boolean success;
        success = validateOperator();
        if (success) {
            success = validatePassword();
            return success;
        } else {
            return false;
        }

    }

    public boolean validateOperator() {
        if (operatorId == null || operatorId.isEmpty()) {
            commentAndDecide("operator id cannot be null nor empty");
        }
        List<Operator> operatorList = operatorClient.view(dbname + "/by_type_operators").key(operatorId)
                                    .includeDocs(true).query(Operator.class);
        if(operatorList.size() == 1) {
            LOG.info(operatorId + " - operator exists in database");
            operator = operatorList.get(0);
            return true;
        } else {
            LOG.info(operatorId + " - cannot find operator in database");
            return false;
        }
    }

    public boolean validatePassword() {
        if (inputValidation()){
            boolean externalValidationSuccess = false;
            try {
                externalValidationSuccess = PasswordValidator.validatePassword(new String(password1));
            } catch (IllegalArgumentException e) {
                commentAndDecide("Password validation failure.");
            }
            if (externalValidationSuccess) {
                LOG.info("Password validated - success.");
                return true;
            } else {
                commentAndDecide("Password validation failure.");
            }
        }
        return false;
    }

    private boolean inputValidation() {
        if (password1 == null || password1.length <=0 || password2 == null || password2.length <= 0) {
            commentAndDecide("Password cannot be empty.");
        }
        if (!Arrays.equals(password1, password2)) {
            commentAndDecide("Passwords are not equal.");
        }
        return true;
    }

    public boolean doUpdate() {
        try {
            LOG.info("update start...");
            operator.hashAndSavePassword(new String(password1));
            operatorClient.update(operator);
            commentAndExit("password updated - success.\nExit.");
        } catch (Exception e) {
            commentAndExit(operatorId + " - cannot update operator: " + e.getMessage());
        }
        return true;
    }

    public void commentAndDecide(String comment) {
        LOG.info(comment);
        String question = "Try again? [y/n] ";
        String decision = getUserDecision(question);

        if (decision.equalsIgnoreCase("y")) {
            processUpdatePasswordHash();
        } else {
            commentAndExit("Exit.");
        }
    }

    private String getUserDecision (String question) {
        String decision = "";
        if (console != null) {
            decision = console.readLine("%s", question);
        } else {
            LOG.info(question);
            try {
                decision = reader.readLine();
            } catch (IOException e) {
                commentAndExit(e.getMessage());
            }
        }
        return decision;
    }

    public static void commentAndExit(String comment) {
        LOG.info(comment);
        System.exit(-1);
    }

    public void resetMembers() {
        operator = null;
        operatorId = null;
        password1 = null;
        password2 = null;
    }

}

