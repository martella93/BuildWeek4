import Dao.*;
import Dao.SellerDao.SellerDao;
import Dao.SellerDao.ShopDao;
import Dao.SellerDao.VendingMachineDao;
import Dao.ServicesDao.SubscriptionDao;
import Dao.ServicesDao.TicketDao;
import Entities.*;
import Entities.Sellers.Seller;
import Entities.Sellers.Shop;
import Entities.Sellers.VendingMachine;
import Entities.Services.Subscription;
import Entities.Services.Ticket;
import enums.SubscriptionDuration;
import enums.VehicleType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bus");
        EntityManager em = emf.createEntityManager();

        boolean exit = false;

        while (!exit) {
            displayMenu();
            int choice = getChoice();
            switch (choice) {
                case 1:
                    createSeller();
                    break;
                case 2:
                    createUser();
                    break;
                case 3:
                    buyCard();
                    break;
                case 4:
                    buyService();
                    break;
                case 5:
                    checkInUser();
                    break;
                case 6:
                    calculateTicketsAndSubscriptions();
                    break;
                case 7:
                    createRouteAndAssignVehicle();
                    break;
                case 8:
                    changeVehicleState();
                    break;
                case 9:
                    printMaintenanceAndOperationPeriods();
                    break;
                case 10:
                    vehicleDeparture();
                    break;
                case 11:
                    calculateTripNumbersAndTotalTravelTime();
                    break;
                case 12:
                    calculateStampedTickets();
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        System.out.println("Program terminated.");
    }

    private static void displayMenu() {
        System.out.println("Menu:");
        System.out.println("1 - Create Seller");
        System.out.println("2 - Create User");
        System.out.println("3 - Buy Card");
        System.out.println("4 - Buy Service");
        System.out.println("5 - Check-in User on Vehicle");
        System.out.println("6 - Calculate number of Tickets and/or Subscriptions issued by a Seller");
        System.out.println("7 - Create Route and (optional) assign Vehicle to this Route");
        System.out.println("8 - Modify Vehicle State");
        System.out.println("9 - Print maintenance and operation periods of a Vehicle");
        System.out.println("10 - Vehicle Departure (new Trip)");
        System.out.println("11 - Calculate number of Trips of a specific Vehicle on each Route and its total travel time");
        System.out.println("12 - Calculate number of stamped Tickets in a given time period or on a specific Vehicle");
        System.out.println("0 - Exit");
    }

    private static int getChoice() {
        System.out.print("Enter your choice: ");
        return scanner.nextInt();
    }

    private static void createSeller() {
        System.out.println("Creating a new seller...");
        System.out.println("Enter seller type (1 for Shop, 2 for Vending Machine): ");
        int type = scanner.nextInt();
        scanner.nextLine();
        Seller seller = null;
        if (type == 1) {
            seller = new Shop();
            ShopDao.save((Shop) seller);
        } else if (type == 2) {
            System.out.println("Enter vending machine state (true/false): ");
            boolean operative = scanner.nextBoolean();
            seller = new VendingMachine(operative);
            VendingMachineDao.save((VendingMachine) seller);
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.println("Seller created successfully.");
    }


    private static void createUser() {
        System.out.println("Creating a new user...");
        System.out.println("Enter user name: ");
        String name = scanner.nextLine();
        System.out.println("Enter user last name: ");
        String lastName = scanner.nextLine();

        User user = new User(name, lastName);

        UserDao.save(user);

        System.out.println("User created successfully.");
    }

    private static void buyCard() {
        System.out.println("Creating a new card...");
        System.out.println("Enter user ID: ");
        long userId = scanner.nextLong();
        scanner.nextLine();

        User user = UserDao.getById((int) userId);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        if (user.getCard() != null) {
            System.out.println("User already has a card.");
            return;
        }

        Card card = new Card(user);

        CardDao.save(card);

        System.out.println("Card created successfully.");
    }

    private static void buyService() {
        System.out.println("Buying a new service...");
        System.out.println("Choose service type (1 for Ticket, 2 for Subscription): ");
        int serviceType = scanner.nextInt();
        scanner.nextLine();

        if (serviceType == 1) {
            buyTicket();
        } else if (serviceType == 2) {
            buySubscription();
        } else {
            System.out.println("Invalid service type.");
        }
    }

    private static void buyTicket() {
        System.out.println("Buying a new ticket...");
        System.out.println("Enter user ID: ");
        long userId = scanner.nextLong();
        scanner.nextLine();

        User user = UserDao.getById((int) userId);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("Choose a seller (1 for Shop, 2 for Vending Machine): ");
        int sellerType = scanner.nextInt();
        scanner.nextLine();

        Seller seller = null;
        if (sellerType == 1) {
            List<Shop> shops = ShopDao.getAllShops();
            if (shops.isEmpty()) {
                System.out.println("No shops available.");
                return;
            }
            System.out.println("Available shops: ");
            for (Shop shop : shops) {
                System.out.println(shop.getSellerId() + " - " + shop.getAddress());
            }
            System.out.println("Choose a shop: ");
            int shopId = scanner.nextInt();
            scanner.nextLine();
            seller = ShopDao.getById(shopId);
        } else if (sellerType == 2) {
            List<VendingMachine> vendingMachines = VendingMachineDao.getAllVendingMachines();
            if (vendingMachines.isEmpty()) {
                System.out.println("No vending machines available.");
                return;
            }
            System.out.println("Available vending machines: ");
            for (VendingMachine machine : vendingMachines) {
                System.out.println(machine.getSellerId() + " - Operative: " + machine.isState());
            }
            System.out.println("Choose a vending machine: ");
            int machineId = scanner.nextInt();
            scanner.nextLine();
            seller = VendingMachineDao.getById(machineId);
        } else {
            System.out.println("Invalid seller type.");
            return;
        }

        Ticket ticket = new Ticket(seller, user);


        TicketDao.save(ticket);

        System.out.println("Ticket purchased successfully.");
    }

    private static void buySubscription() {
        System.out.println("Buying a new subscription...");
        System.out.println("Enter user ID: ");
        long userId = scanner.nextLong();
        scanner.nextLine();

        User user = UserDao.getById((int) userId);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        if (!user.checkUserCard()) {
            System.out.println("To buy a subscription, you must first buy a card.");
            System.out.println("Would you like to buy a card? (Y/N)");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("Y")) {
                buyCard();
            } else {
                System.out.println("You can alternatively buy a ticket.");
                return;
            }
        }

        Card card = user.getCard();


        System.out.println("Choose subscription duration (1 for Weekly, 2 for Monthly): ");
        int durationChoice = scanner.nextInt();
        scanner.nextLine();
        SubscriptionDuration duration = (durationChoice == 1) ? SubscriptionDuration.WEEKLY : SubscriptionDuration.MONTHLY;

        System.out.println("Choose a seller (1 for Shop, 2 for Vending Machine): ");
        int sellerType = scanner.nextInt();
        scanner.nextLine();

        Seller seller;
        if (sellerType == 1) {
            List<Shop> shops = ShopDao.getAllShops();
            if (shops.isEmpty()) {
                System.out.println("No shops available.");
                return;
            }
            System.out.println("Available shops: ");
            for (Shop shop : shops) {
                System.out.println(shop.getSellerId() + " - " + shop.getAddress());
            }
            System.out.println("Choose a shop: ");
            int shopId = scanner.nextInt();
            scanner.nextLine();
            seller = ShopDao.getById(shopId);
        } else if (sellerType == 2) {
            List<VendingMachine> vendingMachines = VendingMachineDao.getAllVendingMachines();
            if (vendingMachines.isEmpty()) {
                System.out.println("No vending machines available.");
                return;
            }
            System.out.println("Available vending machines: ");
            for (VendingMachine machine : vendingMachines) {
                System.out.println(machine.getSellerId() + " - Operative: " + machine.isState());
            }
            System.out.println("Choose a vending machine: ");
            int machineId = scanner.nextInt();
            scanner.nextLine();
            seller = VendingMachineDao.getById(machineId);
        } else {
            System.out.println("Invalid seller type.");
            return;
        }

        Subscription subscription = new Subscription(seller, duration, card);


        SubscriptionDao.save(subscription);

        System.out.println("Subscription purchased successfully.");
    }

    private static void checkInUser() {
        User user = selectUser();
        if (user == null) {
            return;
        }

        Route route = selectRoute();
        if (route == null) {
            return;
        }

        checkIn(user, route);
    }

    public static User selectUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter user ID:");
        long userId = scanner.nextLong();
        User user = UserDao.getById((int) userId);
        if (user == null) {
            System.out.println("User not found.");
            return null;
        }
        return user;
    }

    public static Route selectRoute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter route ID:");
        long routeId = scanner.nextLong();
        Route route = RouteDao.getById((int) routeId);
        if (route == null) {
            System.out.println("Route not found.");
            return null;
        }
        return route;
    }

    private static void calculateTicketsAndSubscriptions() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter start date (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        System.out.println("Enter end date (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        System.out.println("Select service type:");
        System.out.println("1 - Ticket");
        System.out.println("2 - Subscription");
        System.out.println("3 - Both");
        int serviceTypeChoice = scanner.nextInt();

        System.out.println("Select seller type:");
        System.out.println("1 - Shop");
        System.out.println("2 - Vending Machine");
        System.out.println("3 - Both");
        int sellerTypeChoice = scanner.nextInt();

        Map<Seller, Map<String, Integer>> soldServicesMap = SellerDao.soldServices(startDate, endDate, serviceTypeChoice, sellerTypeChoice);

        // Stampa dei risultati
        System.out.println("Services sold:");
        for (Map.Entry<Seller, Map<String, Integer>> entry : soldServicesMap.entrySet()) {
            Seller seller = entry.getKey();
            Map<String, Integer> serviceCounts = entry.getValue();
            System.out.println("Seller ID: " + seller.getSellerId());
            for (Map.Entry<String, Integer> serviceEntry : serviceCounts.entrySet()) {
                System.out.println("  " + serviceEntry.getKey() + ": " + serviceEntry.getValue());
            }
        }
    }


    private static void createRouteAndAssignVehicle() {
        // crea route e (opzionale?) assegnale vehicle
    }

    private static void changeVehicleState() {
        List<Vehicle> allVehicles = VehicleDao.getAllVehicles();
        System.out.println("Lista dei veicoli:");

        for (Vehicle vehicle : allVehicles) {
            System.out.println(vehicle.getVehicleId() + " - " + vehicle.getVehicleType() + " - STATO: " + VehicleStateDao.getVehicleState(vehicle));
        }

        System.out.print("Inserisci l'ID del veicolo di cui vuoi cambiare lo stato di manutenzione: ");
        int vehicleId = scanner.nextInt();
        scanner.nextLine();

        Vehicle vehicle = VehicleDao.getById(vehicleId);
        if (vehicle != null) {
            boolean currentMaintenanceStatus = VehicleStateDao.getVehicleState(vehicle);
            boolean newMaintenanceStatus = !currentMaintenanceStatus;
            VehicleStateDao.updateVehicleMaintenanceStatus(vehicleId, newMaintenanceStatus);
            System.out.println("Stato di manutenzione del veicolo " + vehicleId + " cambiato con successo.");
        } else {
            System.out.println("Veicolo non trovato.");
        }
    }

    private static void printMaintenanceAndOperationPeriods() {
        // Stampa periodi di manutenzione/operatività
    }

    private static void vehicleDeparture() {
        // Fai partire il veicolo (e quindi il trip?)
    }

    private static void calculateTripNumbersAndTotalTravelTime() {
        // calcola numero viaggi e tempo totale di viaggio per veicolo
    }

    private static void calculateStampedTickets() {
        // Calcola ticket timbrati
    }

    private static void checkIn(User user, Route route) {

        Vehicle vehicle = VehicleDao.checkVehicleAvailabilityByRoute(route);

        if (vehicle == null) {
            List<Vehicle> availableVehicles = VehicleDao.getAvailableVehicles();
            if (availableVehicles.isEmpty()) {
                System.out.println("No vehicles available on this route.");
            }
            vehicle = availableVehicles.get(0);
        }

        if (vehicle.checkMaxCapacity()) {
            System.out.println("We are full, wait for another vehicle.");
            return;
        }


        if (user.checkUserCard()) {
            // se true ha la card
            Card card = user.getCard();
            if (card.getSubscription() != null) {
                Subscription subscription = card.getSubscription();
                if (subscription.checkSubscriptionValidity()) {
                    System.out.println("Welcome on board!");
                    vehicle.setUsersOnBoard(vehicle.getUsersOnBoard() + 1);
                }
            }
        } else {
            if (UserDao.getTicketsByUser(user).isEmpty()){
                System.out.println("Ticket/Subscription not found.");
                return;
            } else {
                Ticket ticket = UserDao.getTicketsByUser(user).getFirst();
                TicketDao.checkTicket(ticket);
                System.out.println("Welcome on board");
                vehicle.setUsersOnBoard(vehicle.getUsersOnBoard() + 1);
            }
        }
    }
}
