import java.io.*;
import java.sql.*;
import java.util.*;

// This example is bad.  It uses outdated style. but you'll see this so I cover it.
// exceptions not handled
// no try-with-resources
class ManagerInterface {
    public static void Manager(Connection con) throws SQLException, InterruptedException, IOException, java.lang.ClassNotFoundException {
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome Manager!");
        boolean ex = false;
        do {
            int option = menu(in);
            do {
                if (option == 1) {
                    viewStores(con);
                } else if (option == 2) {
                    int storeid = 0;
                    boolean quit = false;
                    in = new Scanner(System.in);
                    do {
                        System.out.println("Please enter the store id you would like to see the inventory of!");
                        if (in.hasNextInt()) {
                            storeid = in.nextInt();
                            if (storeid > 0 && storeid < 21) {
                                break;
                            } else {
                                System.out.println(
                                        storeid + " is not a valid store id, enter 'Q' to exit and view stores");
                            }
                        } else {
                            String v = in.next();
                            if (v.equalsIgnoreCase("Q")) {
                                quit = true;
                                break;
                            }
                            System.out.println(v + " is not a valid option, try again or enter 'Q' to exit");
                        }
                    } while (true);
                    // in.close();
                    if (quit) {
                        break;
                    }
                    viewInventory(con, storeid);

                } else if (option == 3) { // Change Rental/Reservation
                    in = new Scanner(System.in);
                    System.out.println("Enter the store id you are working in:");
                    int myStore = storeIdValidation(in, con);
                    boolean[] check = viewStoresRentals(con, myStore);
                    if(check[0] || check[1]){
                        edMenu(con, myStore, check);
                    }
                } else if (option == 4) { // Change a customer's account
                    int iddd = 0;
                    in = new Scanner(System.in);
                    do {
                        System.out.println("Please enter the customer's ID to update their address:");
                        if (in.hasNextInt()) {
                            iddd = in.nextInt();
                            if (iddd > 0 && iddd < 9999) {
                                if (checkId(con, iddd)) {
                                    edAccount(con, iddd);
                                    break;
                                } else {
                                    System.out.println(
                                            iddd + " is not a valid id, enter 'Q' to exit or try again");
                                }
                            } else {
                                System.out
                                        .println(iddd + " is not a valid id, enter 'Q' to exit or try again");
                            }
                        } else {
                            String v = in.next();
                            if (v.equalsIgnoreCase("Q")) {
                                break;
                            }
                            System.out.println(v + " is not a valid option, try again or enter 'Q' to exit");
                        }
                    } while (true);
                    // in.close();
                } else if(option == 5){
                    //Remove a customer
                    int iddd = 0;
                    do {
                        System.out.println("Please enter the customer's ID to remove");
                        if (in.hasNextInt()) {
                            iddd = in.nextInt();
                            if (iddd > 0 && iddd < 9999) {
                                if (checkId(con, iddd)) {
                                    removeCustomer(con, iddd);
                                    break;
                                } else {
                                    System.out.println(
                                            iddd + " is not a valid id, enter 'Q' to exit or try again");
                                }
                            } else {
                                System.out
                                        .println(iddd + " is not a valid id, enter 'Q' to exit or try again");
                            }
                        } else {
                            String v = in.next();
                            if (v.equalsIgnoreCase("Q")) {
                                break;
                            }
                            System.out.println(v + " is not a valid option, try again or enter 'Q' to exit");
                        }
                    } while (true);
                } else if(option == 6){
                    int storeid = 0;
                    in = new Scanner(System.in);
                    do {
                        System.out.println("Please enter the store id for the vehicle you are removing");
                        if (in.hasNextInt()) {
                            storeid = in.nextInt();
                            if (storeid > 0 && storeid < 21) {
                                break;
                            } else {
                                System.out.println(
                                        storeid + " is not a valid store id, enter 'Q' to exit and view stores");
                            }
                        } else {
                            String v = in.next();
                            if (v.equalsIgnoreCase("Q")) {
                                break;
                            }
                            System.out.println(v + " is not a valid option, try again or enter 'Q' to exit");
                        }
                    } while (true);
                    List<String> comp = viewInventory(con, storeid);
                    String select = "";
                    do{
                        System.out.println("Please enter the plate of the vehicle you would like to remove from this store");
                        select = in.next();
                        if(comp.indexOf(select.toUpperCase()) > -1){
                            break;
                        }else if(select.equalsIgnoreCase("q")){
                            return;
                        }else if(select.equalsIgnoreCase("v")){
                            viewInventory(con, storeid);
                        } else{
                            System.out.println(select + " is not a valid option, try again, enter 'Q' to exit, or 'V' to view this store's inventory");
                        }
                    }while(true);

                    removeVehicle(con, select.toUpperCase());

                } else {
                    System.out.println("MANAGER LOGOUT! Have a nice day!");
                    ex = true;
                }
                break;
            } while (true);
            // s.close();;

        } while (!ex);
        // End Customer Interface


    }

    

    public static void removeCustomer(Connection con, int id) throws SQLException{
        Scanner in = new Scanner(System.in);
        do{
            System.out.printf("Are you sure you would like to remove Customer #%d from Hurt's?%n[Y/N]: %n", id);
            String resp = in.next();
            if(resp.equalsIgnoreCase("y")){
                System.out.println("Removing customer...");
                break;
            }else if(resp.equalsIgnoreCase("n")){
                System.out.println("Returning to main menu");
            }else{
                System.out.println(resp + " is an invalid response");
            }
        }while(true);

        
        CallableStatement rem = con.prepareCall("{call REMOVECUSTOMER(?)}");
        rem.setInt(1, id);
        rem.executeUpdate();
        System.out.println("CUSTOMER HAS BEEN REMOVED FROM HURT'S\nAny rentals or reservations they had have been removed");
    }

    public static void removeVehicle(Connection con, String plate) throws SQLException{
        String plat = plate.toUpperCase();
        Scanner in = new Scanner(System.in);
        do{
            System.out.printf("Are you sure you would like to remove the vehicle with plate %s from Hurt's?%n[Y/N]: ", plat);
            String resp = in.next();
            if(resp.equalsIgnoreCase("y")){
                break;
            }else if(resp.equalsIgnoreCase("n")){
                System.out.println("Returning to main menu");
            }else{
                System.out.println(resp + " is an invalid response");
            }
        }while(true);

        CallableStatement rem = con.prepareCall("{call REMOVEVEHICLE(?)}");
        rem.setString(1, plat);
        rem.executeUpdate();
        System.out.println("VEHICLE HAS BEEN REMOVED FROM THIS LOCATION\nAny rentals or reservations with this vehicle have been removed");

    }

    public static void edAccount(Connection con, int id) throws SQLException{
        Scanner in = new Scanner(System.in);
        String addr, cit, stat;
        do {
            System.out.println("Enter the new street address:");
                addr = in.nextLine();
                if (addr.length() > 30 || addr == null) {
                    System.out.println("Invalid address (cannot be longer than 30 characters)");
                } else {
                    break;
                }
            
        } while (true);
        do {
            System.out.println("Enter the city:");
                cit = in.nextLine();
                if (cit.length() > 20 || cit == null) {
                    System.out.println("Invalid city (cannot be longer than 20 characters)");
                } else {
                    break;
                }
            

        } while (true);
        do {
            System.out.println("Enter the state abbreviation:");
                stat = in.nextLine();
                if (stat == null || stat.length() != 2) {
                    System.out.println("Invalid state abbreviation (must be 2 characters)");
                } else {
                    break;
                }
            

        } while (true);

        Statement p = con.createStatement();
        String v = String.format("UPDATE customers SET address = '%s', city = '%s', state = '%s' where id = %d", addr, cit, stat, id);
        ResultSet result = p.executeQuery(v);
        if (result.next()) {
            System.out.printf("CUSTOMER ADDRESS SUCCESSFULLY UPDATED%n");
        } else {
            System.out.println("ERROR IN edAccount");
            System.exit(1);
        }
    }





    public static boolean[] viewStoresRentals(Connection con, int storeid) throws SQLException {
        List<Integer> res = new ArrayList<>();
        List<Integer> ren = new ArrayList<>();
        Statement s = con.createStatement();
        String q = String.format("select * from reservations natural join vehicles where store_id = %d", storeid);
        ResultSet result = s.executeQuery(q);
        // s.close();;
        System.out.println("RESERVATIONS\n---------------------------------");
        if (!result.next()) {
            System.out.println("This location has no current reservations");
        } else {
            System.out.println("This location's current reservations:");
            System.out.printf("Res #\tVehicle\t\t\tRate\tPeriod%n");
            do {
                res.add(Integer.parseInt(result.getString("res_num")));
                System.out.printf("%-4s\t%-30s\t%-5s\t%-10s%n", result.getString("res_num"),
                        result.getString("year") + " " + result.getString("make") + " " + result.getString("model"),
                        "$" + result.getString("rate"), result.getString("period") + " days");
            } while (result.next());
        }
        System.out.println("---------------------------------\n");

        Statement p = con.createStatement();
        String v = String.format("select * from rentals natural join vehicles where store_id = %d", storeid);
        ResultSet result2 = p.executeQuery(v);
        // p.close();;
        System.out.println("ACTIVE RENTALS\n---------------------------------");
        if (!result2.next()) {
            System.out.println("This location does not have any active rentals");
        } else {
            System.out.println("This location's current rentals: ");
            System.out.printf("Rental #\tVehicle\t\t\t\tRate\tPeriod%n");
            do {
                ren.add(Integer.parseInt(result2.getString("rental_num")));
                System.out.printf("%-4s\t\t%-30s\t%-5s\t%-10s%n", result2.getString("rental_num"),
                        result2.getString("year") + " " + result2.getString("make") + " " + result2.getString("model"),
                        "$" + result2.getString("rate"), result2.getString("period") + " days");
            } while (result2.next());
        }
        System.out.println("---------------------------------\n");
        if(res.isEmpty() && ren.isEmpty()){
            System.out.println("This store currently has no rentals or reservations, returning to main menu...");
            return new boolean[]{false, false};
        }else{
            return new boolean[]{!ren.isEmpty(), !res.isEmpty()};
        }
    }

    public static void edMenu(Connection con, int storeid, boolean[] check) throws SQLException{
        Scanner in = new Scanner(System.in);
        int sel = 0;
        do{
            System.out.println("Select an Option:\n[1]: Edit/Delete Rental\n[2]: Edit/Delete Reservation\n[3]: View This Location's Rentals/Reservations\n[4]: Exit");
            if(in.hasNextInt()){
                sel = in.nextInt();
                if(sel == 4){
                    return;
                }else if(sel == 3){
                    viewStoresRentals(con, storeid);
                }else if(sel > 0 && sel < 3){
                    break;
                }else{
                    System.out.println(sel + " is not a valid option");
                }
            }else{
                String t = in.next();
                System.out.println(t + " is not a valid option");
            }
        }while(true);
        if(sel == 1){
            if(check[0]){
                edRentals(con, storeid);
            }else{
                System.out.println("This location has no active rentals, returning to menu");
            }
        }else if(sel == 2){
            if(check[1]){
                edReserve(con, storeid);
            }else{
                System.out.println("This location has no active reservations, returning to menu");
            }
        }else{
            System.out.println("ERROR IN VIEW STORES RENTALS");
            System.exit(1);
        }
    }

    public static void edRentals(Connection con, int storeid) throws SQLException{
        int sel = 0;
        Scanner in = new Scanner(System.in);
        do{
            System.out.println("Select a Rental Option:\n[1]: Edit\n[2]: Delete\n[3]: View All\n[4]: Exit");
            if(in.hasNextInt()){
                sel = in.nextInt();
                if(sel == 4){
                    return;
                }else if(sel == 3){
                    viewStoresRentals(con, storeid);
                }else if(sel > 0 && sel < 3){
                    break;
                }else{
                    System.out.println(sel + " is not a valid option");
                }
            }else{
                String t = in.next();
                System.out.println(t + " is not a valid option");
            }
        }while(true);

        if(sel == 1){
            editR(con, "rental", storeid);
        }else if(sel == 2){
            deleteR(con, "rental", storeid);
        }
        
    }

    public static void edReserve(Connection con, int storeid) throws SQLException{
        int sel = 0;
        Scanner in = new Scanner(System.in);
        do{
            System.out.println("Select a Reservation Option:\n[1]: Edit\n[2]: Delete\n[3]: View All\n[4]: Exit");
            if(in.hasNextInt()){
                sel = in.nextInt();
                if(sel == 4){
                    return;
                }else if(sel == 3){
                    viewStoresRentals(con, storeid);
                }else if(sel > 0 && sel < 3){
                    break;
                }else{
                    System.out.println(sel + " is not a valid option");
                }
            }else{
                String t = in.next();
                System.out.println(t + " is not a valid option");
            }
        }while(true);

        if(sel == 1){
            editR(con, "reserve", storeid);
        }else if(sel == 2){
            deleteR(con, "reserve", storeid);
        }
    }

    public static void editR(Connection con, String type, int storeid) throws SQLException{
        String table = "";
        String change = "";
        if(type.equals("rental")){
            table = "rentals";
        }else if(type.equals("reserve")){
            table = "reservations";
        }else{
            System.out.println("ERROR IN EDITR");
            System.exit(1);
        }
        List<Integer> av = new ArrayList<>();
        Statement s = con.createStatement();
        String q;
        ResultSet result;
        q = String.format("select * from %s where store_id = %d", table, storeid);
        result = s.executeQuery(q);
        if(!result.next()){
            System.out.println("ERROR IN EDITR");
            System.exit(1);
        }
        do{
            av.add(Integer.parseInt(result.getString(1)));
        }while(result.next());
        Scanner in = new Scanner(System.in);
        int sel = 0;
        do{
            System.out.printf("Enter the %s number to edit, enter 'Q' to exit or 'V' to view this location's rentals and reservations%n", table);
            if(in.hasNextInt()){
                sel = in.nextInt();
                if(av.indexOf(sel) > -1){
                    break;
                }else{
                    System.out.println(sel + " is not a valid " + table + " number, try again");
                }
            }else{
                String t = in.next();
                if(t.equalsIgnoreCase("q")){
                    return;
                }else if(t.equalsIgnoreCase("v")){
                    viewStoresRentals(con, storeid);
                }else{
                    System.out.println(t + " is not a valid option");
                }
            }
        }while(true);
        int sel2 = 0;
        String col = "";
        do{
            System.out.println("Which value would you like to change?\n[1]: Daily Rate\n[2]: Rental Period\n[3]: View Locations Rentals and Reservations\n[4]: Exit");
            if(in.hasNextInt()){
                sel2 = in.nextInt();
                if(sel2 == 4){
                    return;
                }else if(sel2 == 3){
                    viewStoresRentals(con, storeid);
                }else if(sel2 == 1){
                    col = "rate";
                    break;
                }else if(sel2 == 2){
                    col = "period";
                    break;
                }
            }else{
                String t = in.next();
                System.out.println(t + " is not a valid option");
            }
        }while(true);


        int newVal = 0;
        do{
            System.out.printf("Please enter the new %s:%n", col);
            if(in.hasNextInt()){
                newVal = in.nextInt();
                if(newVal > 0 && newVal < 99){
                    break;
                }else{
                    System.out.printf("Cannot have a %s less than 1 or more than 99%n", col);
                }
            }else{
                String t = in.next();
                System.out.println(t + " is not a valid option");
            }
        }while(true);
        String opt = "";
        if(table.equalsIgnoreCase("rentals")){
            opt = "rental_num";
        }else{
            opt = "res_num";
        }

        q = String.format("update %s set %s = %d where %s = %d", table, col, newVal, opt, sel);
        Statement p = con.createStatement();
        ResultSet result2 = p.executeQuery(q);
        if(result2.next()){
            System.out.println("SUCCESSFULLY UPDATED!");
        }
    }

    public static void deleteR(Connection con, String type, int storeid) throws SQLException{
        String table = "";
        String change = "";
        if(type.equals("rental")){
            table = "rentals";
        }else if(type.equals("reserve")){
            table = "reservations";
        }else{
            System.out.println("ERROR IN EDITR");
            System.exit(1);
        }
        List<Integer> av = new ArrayList<>();
        Statement s = con.createStatement();
        String q;
        ResultSet result;
        q = String.format("select * from %s where store_id = %d", table, storeid);
        result = s.executeQuery(q);
        if(!result.next()){
            System.out.println("ERROR IN EDITR");
            System.exit(1);
        }
        do{
            av.add(Integer.parseInt(result.getString(1)));
        }while(result.next());
        Scanner in = new Scanner(System.in);
        int sel = 0;
        do{
            System.out.printf("Enter the %s number to delete, enter 'Q' to exit or 'V' to view this location's rentals and reservations%n", table);
            if(in.hasNextInt()){
                sel = in.nextInt();
                if(av.indexOf(sel) > -1){
                    break;
                }else{
                    System.out.println(sel + " is not a valid " + table + " number, try again");
                }
            }else{
                String t = in.next();
                if(t.equalsIgnoreCase("q")){
                    return;
                }else if(t.equalsIgnoreCase("v")){
                    viewStoresRentals(con, storeid);
                }else{
                    System.out.println(t + " is not a valid option");
                }
            }
        }while(true);

        String opt = "";
        if(table.equalsIgnoreCase("rentals")){
            opt = "rental_num";
        }else{
            opt = "res_num";
        }

        q = String.format("delete from %s where %s = %d", table, opt, sel);
        Statement p = con.createStatement();
        ResultSet result2 = p.executeQuery(q);
        if(result2.next()){
            System.out.println("SUCCESSFULLY DELETED");
        }
    }


    public static List<String> viewInventory(Connection con, int id) throws SQLException {
        Statement s = con.createStatement();
        String q;
        ResultSet result;
        List<String> ret = new ArrayList<>();
        q = String.format("select * from inventory natural join vehicles where store_id = %d", id);
        result = s.executeQuery(q);
        if (!result.next()) {
            System.out.println("This store currently has no inventory");
        } else {
            System.out.printf("Inventory for Store #%d%n", id);
            System.out.printf("%-10s\t%-20s\t%-25s\t%-5s%-7s%n", "PLATE", "MAKE", "MODEL", "YEAR", "ODOMETER");
            System.out.println("-----------------------------------------------------------------------------------");
            do {
                ret.add(result.getString("plate"));
                System.out.printf("%-10s\t%-20s\t%-25s\t%-5s%-7s%n", result.getString("plate"),
                        result.getString("make"), result.getString("model"), result.getString("year"),
                        result.getString("odo"));
            } while (result.next());
        }
        return ret;
        // s.close();;
    }

    public static void viewStores(Connection con) throws SQLException {
        Statement s = con.createStatement();
        String q;
        ResultSet result;
        q = "select * from stores order by store_id";
        result = s.executeQuery(q);
        if (!result.next()) {
            System.out.println("Error");
        } else {
            System.out.printf("%-10s\t%-30s\t%-20s\t%-2s%n", "STORE_ID", "ADDRESS", "CITY", "STATE");
            System.out.println("-----------------------------------------------------------------------------------");
            do {
                System.out.printf("%-10s\t%-30s\t%-20s\t%-2s%n", result.getString("store_id"),
                        result.getString("address"), result.getString("city"), result.getString("state"));
            } while (result.next());
        }
        // s.close();;
    }

    public static boolean checkId(Connection con, int id) throws SQLException {
        Statement s = con.createStatement();
        String q;
        ResultSet result;
        q = String.format("select * from customers where id = %d", id);
        result = s.executeQuery(q);
        if (!result.next()) {
            System.out.println(
                    "There is no customer with this ID");
            // s.close();;
            return false;
        } else {
            System.out.println("ACCOUNT INFORMATION\n---------------------------------------------------");
            System.out.printf(
            "USER ID: \t%s%nFIRST NAME: \t%s%nLAST NAME: \t%s%nADDRESS: \t%s%nCITY:\t\t%s%nSTATE:  \t%s%nDLN: \t\t%s%n---------------------------------------------------%n",
            result.getString("id"), result.getString("first_name"), result.getString("last_name"),
            result.getString("address"), result.getString("city"), result.getString("state"), result.getString("dln"));
            return true;
        }

    }


    public static int nextNumber(String type, Connection con) throws SQLException {
        Statement s = con.createStatement();
        String q = "";
        ResultSet result;
        if (type.equalsIgnoreCase("rental")) {
            q = "select max(rental_num) from rentals";
            result = s.executeQuery(q);
            // s.close();;
            if (!result.next()) {
                System.exit(1);
            }
            if (result.getString(1) == null) {
                return 1;
            } else {
                return Integer.parseInt(result.getString(1)) + 1;
            }
        } else if (type.equalsIgnoreCase("reservation")) {
            q = "select max(res_num) from reservations";
            result = s.executeQuery(q);
            // s.close();;
            if (!result.next()) {
                System.exit(1);
            }
            if (result.getString(1) == null) {
                return 1;
            } else {
                return Integer.parseInt(result.getString(1)) + 1;
            }
        } else if (type.equalsIgnoreCase("customer")) {
            q = "select max(id) from customers";
            result = s.executeQuery(q);
            // s.close();;
            if (!result.next()) {
                System.exit(1);
            }
            if (result.getString(1) == null) {
                return 1;
            } else {
                return Integer.parseInt(result.getString(1)) + 1;
            }
        } else {
            System.out.println("ERROR: Incorrect input for nextNumber method");
            return -1;
        }

    }

    public static int storeIdValidation(Scanner in, Connection con) throws SQLException {
        int option = 0;
        do {
            System.out.println("Please enter a valid store ID:");
            if (in.hasNextInt()) {
                option = in.nextInt();
                if (option > 0 && option < 21) {
                    break;
                } else {
                    System.out.println("Please select a valid option, enter 'V' to view stores");
                }
            } else {
                String yy = in.next();
                if (yy.equalsIgnoreCase("v")) {
                    viewStores(con);
                } else {
                    System.out.println(yy + " is invalid. Please select a valid option, enter 'V' to view stores");
                }
            }
        } while (true);
        return option;
    }

    public static int menu(Scanner in) {
        int option = 0;
        do {
            System.out.println(
                    "OPTIONS: Please enter the number associated with the option you'd like to choose\n[1]: View Stores \n[2]: View Inventory \n[3]: Change A Rental/Reservation \n[4]: Update A Customer's Address\n[5]: Remove a Customer/Staff Member\n[6]: Remove a Vehicle\n[7]: Exit\n");
            if (in.hasNextInt()) {
                option = in.nextInt();
                if (option > 0 && option < 8) {
                    break;
                } else {
                    System.out.println(option + " is not a valid option, try again");
                }
            } else {
                String s = in.next();
                System.out.println(s + " is not a valid option, try again");
            }
        } while (true);
        return option;
    }

}