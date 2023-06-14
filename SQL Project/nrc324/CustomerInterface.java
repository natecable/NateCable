import java.io.*;
import java.sql.*;
import java.util.*;

class CustomerInterface {
  public static void Customer(Connection con) throws SQLException, IOException, java.lang.ClassNotFoundException {
    Scanner in = new Scanner(System.in);
    System.out.println("Welcome valued customer!");
    // Start Customer Interface
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
                System.out.println(storeid + " is not a valid store id, enter 'Q' to exit and view stores");
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
        } else if (option == 3) {
          in = new Scanner(System.in);
          int iddd = 0;
          do {

            System.out.println("Please enter your registered Hurt's ID:");
            if (in.hasNextInt()) {
              iddd = in.nextInt();
              if (iddd > 0 && iddd < 9999) {
                rentOrReserve(in, con, iddd);
                break;
              } else {
                System.out.println(iddd + " is not a valid id, enter 'Q' to exit and view your account");
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
        } else if (option == 4) {
          int iddd = 0;
          in = new Scanner(System.in);
          do {

            System.out.println("Please enter your registered Hurt's ID:");
            if (in.hasNextInt()) {
              iddd = in.nextInt();
              if (iddd > 0 && iddd < 9999) {
                if (checkId(con, iddd)) {
                  viewMineMenu(con, in, iddd);
                  break;
                } else {
                  System.out.println(iddd + " is not a valid id, enter 'Q' to exit and view your account");
                }
              } else {
                System.out.println(iddd + " is not a valid id, enter 'Q' to exit and view your account");
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
        } else if (option == 5) {
          viewAccount(con);
        } else {
          System.out.println("Thank you for using Hurt's Rent-A-Lemon! Please, come again!");
          ex = true;
        }
        break;
      } while (true);
      // s.close();;

    } while (!ex);
    // End Customer Interface

    con.close();

  }

  public static void viewInventory(Connection con, int id) throws SQLException {
    Statement s = con.createStatement();
    String q;
    ResultSet result;
    q = String.format("select * from inventory natural join vehicles where store_id = %d", id);
    result = s.executeQuery(q);
    if (!result.next()) {
      System.out.println("This store currently has no inventory");
    } else {
      System.out.printf("Inventory for Store #%d%n", id);
      System.out.printf("%-10s\t%-20s\t%-25s\t%-5s%-7s%n", "PLATE", "MAKE", "MODEL", "YEAR", "ODOMETER");
      System.out.println("-----------------------------------------------------------------------------------");
      do {
        System.out.printf("%-10s\t%-20s\t%-25s\t%-5s%-7s%n", result.getString("plate"), result.getString("make"),
            result.getString("model"), result.getString("year"), result.getString("odo"));
      } while (result.next());
    }
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
        System.out.printf("%-10s\t%-30s\t%-20s\t%-2s%n", result.getString("store_id"), result.getString("address"),
            result.getString("city"), result.getString("state"));
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
      System.out
          .println("You are not registered within our database, please make an account before renting/reserving!");
      // s.close();;
      return false;
    } else {
      // s.close();;
      return true;
    }

  }

  public static void rentOrReserve(Scanner in, Connection con, int id) throws SQLException {
    Statement s = con.createStatement();
    String q;
    ResultSet result;
    q = String.format("select * from customers where id = %d", id);
    result = s.executeQuery(q);
    // s.close();;
    if (!result.next()) {
      System.out
          .println("You are not registered within our database, please make an account before renting/reserving!");
    } else {
      System.out.println("Welcome, " + result.getString("first_name") + " " + result.getString("last_name") + "!");
      int option = 0;
      do {
        System.out.println("Please Select an Option:\n[1]: Rent\n[2]: Reserve\n[3]: Exit\n");
        if (in.hasNextInt()) {
          option = in.nextInt();
          if (option > 0 && option < 3) {
            break;
          } else if (option == 3) {
            return;
          } else {
            System.out.println("Please select a valid option");
          }
        } else {
          String yy = in.next();
          System.out.println(yy + " is invalid. Please select a valid option");
        }
      } while (true);
      viewStores(con);
      int storeid = 0;
      if (option == 1) {
        System.out.println("Which store will you be renting from?");
        storeid = storeIdValidation(in, con);
        rent(con, in, storeid, id);
      } else {
        System.out.println("Which store will you make a reservation from?");
        storeid = storeIdValidation(in, con);
        reserve(con, in, storeid, id);
      }
    }
  }

  public static List<String> viewAvailableInventory(Connection con, int id) throws SQLException {
    Statement s = con.createStatement();
    String q;
    ResultSet result;
    ArrayList<String> plates = new ArrayList<>();
    q = String.format(
        "select * from inventory natural join vehicles where store_id = %d AND plate NOT in (select plate from rentals union select plate from reservations)",
        id);
    result = s.executeQuery(q);
    // s.close();;
    if (!result.next()) {
      System.out.println("This store currently has no inventory available for rentals/reservations");
    } else {
      System.out.printf("Available Inventory for Store #%d%n", id);
      System.out.printf("%-10s\t%-20s\t%-25s\t%-5s%-7s%n", "PLATE", "MAKE", "MODEL", "YEAR", "ODOMETER");
      System.out.println("-----------------------------------------------------------------------------------");
      do {
        System.out.printf("%-10s\t%-20s\t%-25s\t%-5s%-7s%n", result.getString("plate"), result.getString("make"),
            result.getString("model"), result.getString("year"), result.getString("odo"));
        plates.add(result.getString("plate"));
      } while (result.next());
    }
    return plates;
  }

  public static void rent(Connection con, Scanner in, int storeid, int id) throws SQLException {
    System.out.println("Vehicles available to rent from this location:");
    Random rand = new Random();
    List<String> plates = viewAvailableInventory(con, storeid);
    if (plates.isEmpty()) {
      return;
    }
    String selection = "";
    do {
      System.out.println("Enter the plate of the vehicle you'd like to rent: ");
      selection = in.next();
      if (selection.equalsIgnoreCase("q")) {
        System.out.println("Returning to main menu");
        return;
      } else if (plates.indexOf(selection.toUpperCase()) == -1) {
        System.out.println("That vehicle is not available to rent from this location, try again or enter 'Q' to exit");
      } else {
        break;
      }
    } while (true);
    int rate = rand.nextInt(30) + 20;
    System.out.printf("Your approved rate for this vehicle is $%d/day%n", rate);

    int period = 0;
    do {
      System.out.println("What is the period of the rental? (Minimum 1 day, maximum 30 days)");
      if (in.hasNextInt()) {
        period = in.nextInt();
        if (period > 0 && period < 31) {
          System.out.printf("Your current total before discounts and add-ons is now $%d.00%n", rate * period);
          break;
        } else {
          System.out.println("Minimum 1 day rental, maximum 30 days. Please try again or enter 'Q' to exit");
        }
      } else {
        String y = in.next();
        if (y.equalsIgnoreCase("q")) {
          return;
        }
        System.out.println(y + " is not a valid option, please try again or enter 'Q' to exit");
      }
    } while (true);

    int rental_num = nextNumber("rental", con);

    Statement s = con.createStatement();
    String q = String.format("INSERT INTO rentals values (%d, %d, %d, '%s', %d, %d)", rental_num, id, period,
        selection.toUpperCase(), rate, storeid);
    ResultSet result;
    result = s.executeQuery(q);
    // s.close();;
    if (result != null) {
      System.out.println("Rental Confirmed! Your total charge will be shown below:");
      totalCharge(con, id, rate, period, 0);
    }
  }

  public static void totalCharge(Connection con, int id, int rate, int period, int res) throws SQLException {
    Statement s = con.createStatement();
    String q = String.format("select discount, title from customers natural join memberships where id = %d", id);
    ResultSet result = s.executeQuery(q);
    // s.close();;
    if (!result.next()) {
      System.out.println("ERROR IN TOTAL CHARGE METHOD");
      System.exit(1);
    }
    String membership = result.getString("title");
    int discount = Integer.parseInt(result.getString("discount"));
    if (res == 0) {
      System.out.printf("%nRENTAL CHARGES%n----------------------%n");
      System.out.printf(
          "Approved Rate: \t\t$%d/day %nRental Period: \t\t%d days%nBase Charge: \t\t$%.2f%nMembership Type: \t%s%nMembership Discount: \t%d%%  %n----------------------%nBALANCE DUE TODAY: \t$%.2f%n----------------------%n",
          rate, period, rate * period * 1.0, membership, discount, rate * period - (rate * period * 0.01 * discount));
    } else {
      System.out.printf("RESERVATION CHARGES%n----------------------%n");
      System.out.printf(
          "Reservation Length: \t%d days%nReservation Rate: \t$2.50/day%nReservation Charge: \t$%.2f%nApproved Rate: \t\t$%d/day %nRental Period: \t\t%d days%nBase Charge: \t\t$%.2f%nMembership Type: \t%s%nMembership Discount: \t%d%%  %n----------------------%nDUE UPON ARRIVAL: \t$%.2f%nBALANCE DUE TODAY: \t$%.2f%n----------------------%n",
          res, res * 2.5, rate, period, rate * period * 1.0, membership, discount,
          rate * period - (rate * period * 0.01 * discount), res * 2.5);
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

  public static void reserve(Connection con, Scanner in, int storeid, int id) throws SQLException {
    System.out.println("Vehicles available to reserve from this location:");
    Random rand = new Random();
    List<String> plates = viewAvailableInventory(con, storeid);
    if (plates.isEmpty()) {
      return;
    }
    String selection = "";
    do {
      System.out.println("Enter the plate of the vehicle you'd like to reserve: ");
      selection = in.next();
      if (selection.equalsIgnoreCase("q")) {
        System.out.println("Returning to main menu");
        return;
      } else if (plates.indexOf(selection.toUpperCase()) == -1) {
        System.out
            .println("That vehicle is not available to reserve from this location, try again or enter 'Q' to exit");
      } else {
        break;
      }
    } while (true);
    int rate = rand.nextInt(30) + 20;
    System.out.printf("Your approved rate for this vehicle is $%d/day plus a $2.50/day reservation fee%n", rate);
    int ttt = 0;
    do {
      System.out.println(
          "How many days would you like to reserve this vehicle before rental? (Minimum 5 days, maximum 150 days)");
      if (in.hasNextInt()) {
        ttt = in.nextInt();
        if (ttt > 4 && ttt < 151) {
          System.out.printf("Your reservation charge will be $%.2f to reserve for %d days%n", ttt * 2.5, ttt);
          break;
        } else {
          System.out.println("Minimum 5 day reservation, maximum 150 days. Please try again or enter 'Q' to exit");
        }
      } else {
        String y = in.next();
        if (y.equalsIgnoreCase("q")) {
          return;
        }
        System.out.println(y + " is not a valid option, please try again or enter 'Q' to exit");
      }
    } while (true);

    int period = 0;
    do {
      System.out.println("What is the period of the rental? (Minimum 1 day, maximum 30 days)");
      if (in.hasNextInt()) {
        period = in.nextInt();
        if (period > 0 && period < 31) {
          System.out.printf("Your current total before discounts and add-ons is now $%.2f%n",
              rate * period + ttt * 2.5);
          break;
        } else {
          System.out.println("Minimum 1 day rental, maximum 30 days. Please try again or enter 'Q' to exit");
        }
      } else {
        String y = in.next();
        if (y.equalsIgnoreCase("q")) {
          return;
        }
        System.out.println(y + " is not a valid option, please try again or enter 'Q' to exit");
      }
    } while (true);

    int res_num = nextNumber("reservation", con);

    Statement s = con.createStatement();
    String q = String.format("INSERT INTO reservations values (%d, %d, %d, '%s', %d, %d)", res_num, id, period,
        selection.toUpperCase(), rate, storeid);
    ResultSet result;
    result = s.executeQuery(q);
    // s.close();;
    if (result != null) {
      System.out.printf("Reservation Confirmed! Your total charge will be shown below and due in %d days:%n", ttt);
      totalCharge(con, id, rate, period, ttt);
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
          "OPTIONS: Please enter the number associated with the option you'd like to choose\n[1]: View Stores \n[2]: View Inventory \n[3]: Rent or Reserve a Vehicle \n[4]: View or Cancel Rentals/Reservations\n[5]: View or Create Account\n[6]: Exit\n");
      if (in.hasNextInt()) {
        option = in.nextInt();
        if (option > 0 && option < 7) {
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

  public static void viewMineMenu(Connection con, Scanner in, int id) throws SQLException {
    System.out
        .printf("Rentals/Reservations:%n[1]: View My Rentals/Reservations%n[2]: Cancel A Reservation%n[3]: Exit%n");
    int option = 0;
    do {
      if (in.hasNextInt()) {
        option = in.nextInt();
        if (option == 3) {
          return;
        } else if (option > 0 && option < 3) {
          break;
        } else {
          System.out.println("Not a valid option, try again or enter '3' to exit");
        }
      } else {
        String t = in.next();
        System.out.println(t + " is not a valid option, try again or enter '3' to exit");
      }
    } while (true);

    if (option == 1) {
      viewMine(con, id);
      return;
    }
    Statement s = con.createStatement();
    String q = String.format("select * from reservations natural join vehicles where id = %d", id);
    ResultSet result = s.executeQuery(q);
    // s.close();;
    List<Integer> myRes = new ArrayList<>();
    if (!result.next()) {
      System.out.println("You currently do not have any reservations made!");
      return;
    } else {
      System.out.println("Here are your current reservations: ");
      System.out.printf("Res #\tVehicle\t\tRate\tPeriod%n");
      do {
        myRes.add(Integer.parseInt(result.getString("res_num")));
        System.out.printf("%-4s\t%-50s\t%-5s\t%-10s%n", result.getString("res_num"),
            result.getString("year") + " " + result.getString("make") + " " + result.getString("model"),
            "$" + result.getString("rate"), result.getString("period") + " days");
      } while (result.next());
    }
    int cancel = 0;
    do {
      System.out.println("Enter the reservation number of the reservation you'd like to cancel, or enter 'Q' to exit:");
      if (in.hasNextInt()) {
        cancel = in.nextInt();
        if (myRes.indexOf(cancel) > -1) {
          System.out
              .printf("Your reservation has been cancelled, there will be no refund for your reservation charge%n");
          break;
        } else {
          System.out.println("Invalid reservation number. Please try again or enter 'Q' to exit");
        }
      } else {
        String y = in.next();
        if (y.equalsIgnoreCase("q")) {
          return;
        }
        System.out.println(y + " is not a valid option, please try again or enter 'Q' to exit");
      }
    } while (true);

    Statement p = con.createStatement();
    String e = String.format("DELETE FROM reservations where res_num = %d", cancel);
    ResultSet resu = p.executeQuery(e);
    // p.close();;
    resu.next();
  }

  public static void viewMine(Connection con, int id) throws SQLException {
    Statement s = con.createStatement();
    String q = String.format("select * from reservations natural join vehicles where id = %d", id);
    ResultSet result = s.executeQuery(q);
    // s.close();;
    System.out.println("RESERVATIONS\n---------------------------------");
    if (!result.next()) {
      System.out.println("You currently do not have any reservations made");
    } else {
      System.out.println("Here are your current reservations: ");
      System.out.printf("Res #\tVehicle\t\t\tRate\t\tPeriod%n");
      do {
        System.out.printf("%-4s\t%-30s\t%-5s\t%-10s%n", result.getString("res_num"),
            result.getString("year") + " " + result.getString("make") + " " + result.getString("model"),
            "$" + result.getString("rate"), result.getString("period") + " days");
      } while (result.next());
    }
    System.out.println("---------------------------------\n");

    Statement p = con.createStatement();
    String v = String.format("select * from rentals natural join vehicles where id = %d", id);
    ResultSet result2 = p.executeQuery(v);
    // p.close();;
    System.out.println("ACTIVE RENTALS\n---------------------------------");
    if (!result2.next()) {
      System.out.println("You currently do not have any active rentals");
    } else {
      System.out.println("Here are your current rentals: ");
      System.out.printf("Rental #\tVehicle\t\tRate\tPeriod%n");
      do {
        System.out.printf("%-4s\t\t%-20s%-5s\t%-10s%n", result2.getString("rental_num"),
            result2.getString("year") + " " + result2.getString("make") + " " + result2.getString("model"),
            "$" + result2.getString("rate"), result2.getString("period") + " days");
      } while (result2.next());
    }
    System.out.println("---------------------------------\n");
  }

  public static void viewAccount(Connection con) throws SQLException {
    Scanner in = new Scanner(System.in);
    System.out.printf("View Account Information:%n[1]: View My Account%n[2]: Create a New Account%n[3]: Exit%n");
    int option = 0;
    do {
      if (in.hasNextInt()) {
        option = in.nextInt();
        if (option == 3) {
          // in.close();
          return;
        } else if (option > 0 && option < 3) {
          break;
        } else {
          System.out.println("Not a valid option, try again or enter '3' to exit");
        }
      } else {
        String t = in.next();
        System.out.println(t + " is not a valid option, try again or enter '3' to exit");
      }
    } while (true);

    if (option == 1) {
      int iddd = 0;
      do {
        System.out.println("Please enter your registered Hurt's ID:");
        if (in.hasNextInt()) {
          iddd = in.nextInt();
          if (iddd > 0 && iddd < 9999) {
            if (checkId(con, iddd)) {
              break;
            } else {
              System.out.println(iddd + " is not a valid id, enter 'Q' to exit and view your account");
            }
          } else {
            System.out.println(iddd + " is not a valid id, enter 'Q' to exit and view your account");
          }
        } else {
          String v = in.next();
          if (v.equalsIgnoreCase("Q")) {
            // in.close();
            return;
          }
          System.out.println(v + " is not a valid option, try again or enter 'Q' to exit");
        }
      } while (true);
      Statement p = con.createStatement();
      String v = String.format("select * from customers natural join memberships where id = %d", iddd);
      ResultSet result = p.executeQuery(v);
      // p.close();;
      if (!result.next()) {
        System.out.println("ERROR IN VIEW ACCOUNT");
        System.exit(1);
      } else {
        System.out.println("ACCOUNT DETAILS\n---------------------");
        System.out.printf(
            "USER ID: \t%s%nFIRST NAME: \t%s%nLAST NAME: \t%s%nADDRESS: \t%s%nCITY:\t\t%s%nSTATE:  \t%s%nDLN:\t\t%s%nMEMBERSHIP: \t%s%n---------------------%n",
            result.getString("id"), result.getString("first_name"), result.getString("last_name"),
            result.getString("address"), result.getString("city"), result.getString("state"), result.getString("dln"),
            result.getString("title"));
      }
    } else {
      // in.close();
      newCustomer(con);
    }
    // in.close();
  }

  public static void newCustomer(Connection con) throws SQLException {
    Scanner in = new Scanner(System.in);
    int id = nextNumber("customer", con);
    String fname, lname, addr, cit, stat, dln;
    do {
      System.out.println("Enter your first name:");
        fname = in.nextLine();
        if (fname.contains(" ") || fname.length() > 20 || fname.equals("")) {
          System.out.println("Invalid first name (cannot contain a space or be longer than 20 characters)");
        } else {
          break;
        }
    } while (true);
    do {
      System.out.println("Enter your last name:");
        lname = in.nextLine();
        if (lname.contains(" ") || lname.length() > 20 || lname == null) {
          System.out.println("Invalid last name (cannot contain a space or be longer than 20 characters)");
        } else {
          break;
        }
    } while (true);
    do {
      System.out.println("Enter your street address:");
        addr = in.nextLine();
        if (addr.length() > 30 || addr == null) {
          System.out.println("Invalid address (cannot be longer than 30 characters)");
        } else {
          break;
        }
    } while (true);
    do {
      System.out.println("Enter your city:");
        cit = in.nextLine();
        if (cit.length() > 20 || cit == null) {
          System.out.println("Invalid city (cannot be longer than 20 characters)");
        } else {
          break;
        }

    } while (true);
    do {
      System.out.println("Enter your state abbreviation:");
        stat = in.nextLine();
        if (stat == null || stat.length() != 2) {
          System.out.println("Invalid state abbreviation (must be 2 characters)");
        } else {
          break;
        }

    } while (true);
    do {
      System.out.println("Enter your drivers license number:");
        dln = in.nextLine();
        if (!dln.matches("[0-9]{8}")) {
          System.out.println("Invalid DLN (must be 8 digits 0-9)");
        } else {
          break;
        }
    } while (true);
    Statement p = con.createStatement();
    String v = String.format("insert into customers values (%d, '%s', '%s', '%s', '%s', '%s', %d, 0)", id, addr, fname,
        lname, cit, stat, Integer.parseInt(dln));
    ResultSet result = p.executeQuery(v);
    if (result.next()) {
      System.out.printf("New User added! Your Hurt's ID is %d!%n", id);
    } else {
      System.out.println("ERROR IN VIEW ACCOUNT");
      System.exit(1);
    }
    // in.close();
  }

}
