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

public class PasswordResetService {

    private static String nishaLocalDatabaseUri;
    private static boolean devEnabled;

    public static void main(String[] args) {
        printHeader();
        usage(args);
        managePasswordUpdate();
        printEnding();
    }

    public static void printHeader() {
        System.out.println("*****************************************");
        System.out.println("*   Welcome to NISHA Password Reset Service   *");
        System.out.println("*****************************************");
    }

    private static void usage(String[] args) {
        if ((args.length == 2 || args.length == 3 ) && args[0].equalsIgnoreCase("-u") && !args[1].isEmpty()) {
            nishaLocalDatabaseUri = args[1];
            String info = "Service running with operators' database uri: " + nishaLocalDatabaseUri;
            if (args.length == 3 && args[2].equals("-dev")) {
                info += " - dev mode";
                devEnabled = true;
            } else {
                devEnabled = false;
            }
            System.out.println(info);
        }
        else {
            System.out.println("Invalid usage:\n Password Reset Service [flags]");
            System.out.println("Available flags:\n -u - uri of database that stores operators like http://localhost:5984/nisha-local [REQUIRED]");
//            System.out.println(" -dev - development mode use system input stream if console is not available [OPTIONAL]");
            System.out.println("Exit.");
            System.exit(-1);
        }
    }

    public static void managePasswordUpdate() {
        PasswordUpdater passwordUpdater = new NishaPasswordUpdater(nishaLocalDatabaseUri, System.console(), devEnabled);
        passwordUpdater.processUpdatePasswordHash();
    }

    public static void printEnding() {
        System.out.println("Bye. End.");
    }
}
