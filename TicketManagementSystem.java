
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
 * ============================================================
 *          TICKET MANAGEMENT SYSTEM
 *          Complete Single-File Java Project
 * ============================================================
 *
 * Features based on the supplied project-review PPT:
 *
 * USER:
 *   Register / Login
 *   Create ticket
 *   View tickets
 *   Track status
 *   Add comments
 *   Give feedback
 *
 * SUPPORT STAFF:
 *   Login
 *   View assigned tickets
 *   Update status
 *   Add comments
 *   Resolve tickets
 *
 * ADMIN:
 *   Login
 *   View all tickets
 *   Assign / reassign tickets
 *   Manage users
 *   Update status
 *   View reports
 *   Monitor ticket history
 *
 * Workflow:
 *   OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED
 *
 * This is a standalone console application.
 * It requires only Java 17+ and has NO external dependencies.
 */

public class TicketManagementSystem {

    // ============================================================
    // CONSTANTS
    // ============================================================

    static final Scanner scanner = new Scanner(System.in);
    static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    static final String RESET = "\u001B[0m";
    static final String BLUE = "\u001B[34m";
    static final String CYAN = "\u001B[36m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String RED = "\u001B[31m";
    static final String WHITE = "\u001B[37m";
    static final String BOLD = "\u001B[1m";

    static final List<User> users = new ArrayList<>();
    static final List<Ticket> tickets = new ArrayList<>();
    static final List<Notification> notifications = new ArrayList<>();

    static int nextUserId = 1;
    static int nextTicketId = 1001;
    static int nextCommentId = 1;
    static int nextHistoryId = 1;
    static int nextNotificationId = 1;

    static User currentUser;

    // ============================================================
    // ENUMS
    // ============================================================

    enum Role {
        USER,
        STAFF,
        ADMIN
    }

    enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    enum Status {
        OPEN,
        ASSIGNED,
        IN_PROGRESS,
        RESOLVED,
        CLOSED
    }

    // ============================================================
    // USER CLASS
    // ============================================================

    static class User {
        int id;
        String name;
        String email;
        String password;
        Role role;
        LocalDateTime createdAt;

        User(int id, String name, String email,
             String password, Role role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = role;
            this.createdAt = LocalDateTime.now();
        }
    }

    // ============================================================
    // COMMENT CLASS
    // ============================================================

    static class Comment {
        int id;
        int userId;
        String userName;
        String text;
        LocalDateTime createdAt;

        Comment(int id, User user, String text) {
            this.id = id;
            this.userId = user.id;
            this.userName = user.name;
            this.text = text;
            this.createdAt = LocalDateTime.now();
        }
    }

    // ============================================================
    // HISTORY CLASS
    // ============================================================

    static class History {
        int id;
        Status status;
        String note;
        String changedBy;
        LocalDateTime changedAt;

        History(int id, Status status,
                String note, String changedBy) {
            this.id = id;
            this.status = status;
            this.note = note;
            this.changedBy = changedBy;
            this.changedAt = LocalDateTime.now();
        }
    }

    // ============================================================
    // FEEDBACK CLASS
    // ============================================================

    static class Feedback {
        int rating;
        String comment;
        String userName;
        LocalDateTime createdAt;

        Feedback(int rating, String comment, String userName) {
            this.rating = rating;
            this.comment = comment;
            this.userName = userName;
            this.createdAt = LocalDateTime.now();
        }
    }

    // ============================================================
    // TICKET CLASS
    // ============================================================

    static class Ticket {
        int id;
        String title;
        String description;
        String category;
        Priority priority;
        Status status;

        int createdBy;
        String createdByName;

        Integer assignedStaffId;
        String assignedStaffName;

        String resolutionNote;

        LocalDateTime createdAt;
        LocalDateTime updatedAt;

        List<Comment> comments = new ArrayList<>();
        List<History> history = new ArrayList<>();

        Feedback feedback;

        Ticket(int id,
               String title,
               String description,
               String category,
               Priority priority,
               User creator) {

            this.id = id;
            this.title = title;
            this.description = description;
            this.category = category;
            this.priority = priority;
            this.status = Status.OPEN;

            this.createdBy = creator.id;
            this.createdByName = creator.name;

            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();

            addHistory(Status.OPEN,
                    "Ticket created",
                    creator.name);
        }

        void addHistory(Status status,
                        String note,
                        String by) {

            history.add(
                    new History(
                            nextHistoryId++,
                            status,
                            note,
                            by
                    )
            );
        }
    }

    // ============================================================
    // NOTIFICATION CLASS
    // ============================================================

    static class Notification {
        int id;
        int userId;
        int ticketId;
        String message;
        boolean read;
        LocalDateTime createdAt;

        Notification(int userId,
                     int ticketId,
                     String message) {

            this.id = nextNotificationId++;
            this.userId = userId;
            this.ticketId = ticketId;
            this.message = message;
            this.read = false;
            this.createdAt = LocalDateTime.now();
        }
    }

    // ============================================================
    // MAIN
    // ============================================================

    public static void main(String[] args) {

        initializeDemoData();

        while (true) {

            clearScreen();
            printHeader();

            if (currentUser == null) {
                guestMenu();
            } else {
                switch (currentUser.role) {

                    case USER:
                        userMenu();
                        break;

                    case STAFF:
                        staffMenu();
                        break;

                    case ADMIN:
                        adminMenu();
                        break;
                }
            }
        }
    }

    // ============================================================
    // DEMO DATA
    // ============================================================

    static void initializeDemoData() {

        users.add(
                new User(
                        nextUserId++,
                        "System Administrator",
                        "admin@ticket.com",
                        "admin123",
                        Role.ADMIN
                )
        );

        users.add(
                new User(
                        nextUserId++,
                        "Support Staff",
                        "staff@ticket.com",
                        "staff123",
                        Role.STAFF
                )
        );

        users.add(
                new User(
                        nextUserId++,
                        "Demo User",
                        "user@ticket.com",
                        "user123",
                        Role.USER
                )
        );
    }

    // ============================================================
    // GUEST MENU
    // ============================================================

    static void guestMenu() {

        System.out.println(BOLD + "  TICKET MANAGEMENT SYSTEM" + RESET);
        System.out.println();
        System.out.println("  1. Login");
        System.out.println("  2. Register");
        System.out.println("  3. Demo Accounts");
        System.out.println("  0. Exit");

        int choice = readInt("  Select option: ");

        switch (choice) {

            case 1:
                login();
                break;

            case 2:
                register();
                break;

            case 3:
                showDemoAccounts();
                pause();
                break;

            case 0:
                exitApplication();
                break;

            default:
                error("Invalid option.");
                pause();
        }
    }

    // ============================================================
    // LOGIN
    // ============================================================

    static void login() {

        clearScreen();
        printTitle("LOGIN");

        String email = readString("Email: ");
        String password = readString("Password: ");

        for (User user : users) {

            if (user.email.equalsIgnoreCase(email)
                    && user.password.equals(password)) {

                currentUser = user;

                success(
                        "Login successful. Welcome, "
                                + user.name + "!"
                );

                pause();
                return;
            }
        }

        error("Invalid email or password.");
        pause();
    }

    // ============================================================
    // REGISTER
    // ============================================================

    static void register() {

        clearScreen();
        printTitle("CREATE ACCOUNT");

        String name = readString("Full name: ");
        String email = readString("Email: ");

        if (findUserByEmail(email) != null) {
            error("An account with this email already exists.");
            pause();
            return;
        }

        String password = readString("Password: ");

        if (password.length() < 6) {
            error("Password must contain at least 6 characters.");
            pause();
            return;
        }

        User user = new User(
                nextUserId++,
                name,
                email,
                password,
                Role.USER
        );

        users.add(user);

        success(
                "Account created successfully."
                        + " You can now login."
        );

        pause();
    }

    // ============================================================
    // USER MENU
    // ============================================================

    static void userMenu() {

        clearScreen();
        printHeader();

        printDashboardSummary();

        System.out.println();
        System.out.println(BOLD + "  USER MENU" + RESET);
        System.out.println();
        System.out.println("  1. Create Ticket");
        System.out.println("  2. My Tickets");
        System.out.println("  3. View Ticket");
        System.out.println("  4. Add Comment");
        System.out.println("  5. Track Ticket History");
        System.out.println("  6. Give Feedback");
        System.out.println("  7. Notifications");
        System.out.println("  8. Profile");
        System.out.println("  9. Logout");
        System.out.println("  0. Exit");

        int choice = readInt("  Select option: ");

        switch (choice) {

            case 1:
                createTicket();
                break;

            case 2:
                viewMyTickets();
                break;

            case 3:
                viewTicketDetails();
                break;

            case 4:
                addComment();
                break;

            case 5:
                viewHistory();
                break;

            case 6:
                giveFeedback();
                break;

            case 7:
                viewNotifications();
                break;

            case 8:
                viewProfile();
                break;

            case 9:
                logout();
                break;

            case 0:
                exitApplication();
                break;

            default:
                error("Invalid option.");
                pause();
        }
    }

    // ============================================================
    // STAFF MENU
    // ============================================================

    static void staffMenu() {

        clearScreen();
        printHeader();

        printDashboardSummary();

        System.out.println();
        System.out.println(BOLD + "  SUPPORT STAFF MENU" + RESET);
        System.out.println();
        System.out.println("  1. Assigned Tickets");
        System.out.println("  2. View Ticket");
        System.out.println("  3. Update Status");
        System.out.println("  4. Add Comment");
        System.out.println("  5. Resolve Ticket");
        System.out.println("  6. Ticket History");
        System.out.println("  7. Notifications");
        System.out.println("  8. Profile");
        System.out.println("  9. Logout");
        System.out.println("  0. Exit");

        int choice = readInt("  Select option: ");

        switch (choice) {

            case 1:
                viewAssignedTickets();
                break;

            case 2:
                viewTicketDetails();
                break;

            case 3:
                staffUpdateStatus();
                break;

            case 4:
                addComment();
                break;

            case 5:
                resolveTicket();
                break;

            case 6:
                viewHistory();
                break;

            case 7:
                viewNotifications();
                break;

            case 8:
                viewProfile();
                break;

            case 9:
                logout();
                break;

            case 0:
                exitApplication();
                break;

            default:
                error("Invalid option.");
                pause();
        }
    }

    // ============================================================
    // ADMIN MENU
    // ============================================================

    static void adminMenu() {

        clearScreen();
        printHeader();

        printDashboardSummary();

        System.out.println();
        System.out.println(BOLD + "  ADMIN MENU" + RESET);
        System.out.println();
        System.out.println("  1. All Tickets");
        System.out.println("  2. Assign Ticket");
        System.out.println("  3. Reassign Ticket");
        System.out.println("  4. Update Ticket Status");
        System.out.println("  5. View Ticket");
        System.out.println("  6. Manage Users");
        System.out.println("  7. Reports");
        System.out.println("  8. Ticket History");
        System.out.println("  9. Notifications");
        System.out.println(" 10. Profile");
        System.out.println(" 11. Logout");
        System.out.println("  0. Exit");

        int choice = readInt("  Select option: ");

        switch (choice) {

            case 1:
                viewAllTickets();
                break;

            case 2:
                assignTicket();
                break;

            case 3:
                assignTicket();
                break;

            case 4:
                adminUpdateStatus();
                break;

            case 5:
                viewTicketDetails();
                break;

            case 6:
                manageUsers();
                break;

            case 7:
                reports();
                break;

            case 8:
                viewHistory();
                break;

            case 9:
                viewNotifications();
                break;

            case 10:
                viewProfile();
                break;

            case 11:
                logout();
                break;

            case 0:
                exitApplication();
                break;

            default:
                error("Invalid option.");
                pause();
        }
    }

    // ============================================================
    // CREATE TICKET
    // ============================================================

    static void createTicket() {

        clearScreen();
        printTitle("CREATE TICKET");

        String title = readString("Title: ");
        String description = readMultiline("Description: ");

        System.out.println();
        System.out.println("Category:");
        System.out.println("1. Technical Issue");
        System.out.println("2. Software Issue");
        System.out.println("3. Hardware Issue");
        System.out.println("4. Account Issue");
        System.out.println("5. Network Issue");
        System.out.println("6. Other");

        int categoryChoice =
                readInt("Choose category: ");

        String category =
                categoryName(categoryChoice);

        System.out.println();
        System.out.println("Priority:");
        System.out.println("1. LOW");
        System.out.println("2. MEDIUM");
        System.out.println("3. HIGH");
        System.out.println("4. CRITICAL");

        int priorityChoice =
                readInt("Choose priority: ");

        Priority priority =
                priorityFromChoice(priorityChoice);

        Ticket ticket =
                new Ticket(
                        nextTicketId++,
                        title,
                        description,
                        category,
                        priority,
                        currentUser
                );

        tickets.add(ticket);

        success(
                "Ticket #" + ticket.id
                        + " created successfully."
        );

        notifyAdmins(
                "New ticket #" + ticket.id
                        + " created by "
                        + currentUser.name
        );

        pause();
    }

    // ============================================================
    // VIEW MY TICKETS
    // ============================================================

    static void viewMyTickets() {

        clearScreen();
        printTitle("MY TICKETS");

        List<Ticket> mine = new ArrayList<>();

        for (Ticket ticket : tickets) {

            if (ticket.createdBy == currentUser.id) {
                mine.add(ticket);
            }
        }

        printTicketTable(mine);

        pause();
    }

    // ============================================================
    // VIEW ASSIGNED TICKETS
    // ============================================================

    static void viewAssignedTickets() {

        clearScreen();
        printTitle("ASSIGNED TICKETS");

        List<Ticket> assigned = new ArrayList<>();

        for (Ticket ticket : tickets) {

            if (ticket.assignedStaffId != null
                    && ticket.assignedStaffId
                    == currentUser.id) {

                assigned.add(ticket);
            }
        }

        printTicketTable(assigned);

        pause();
    }

    // ============================================================
    // VIEW ALL TICKETS
    // ============================================================

    static void viewAllTickets() {

        clearScreen();
        printTitle("ALL TICKETS");

        if (tickets.isEmpty()) {

            System.out.println(
                    "No tickets available."
            );

        } else {

            printTicketTable(tickets);
        }

        pause();
    }

    // ============================================================
    // VIEW TICKET DETAILS
    // ============================================================

    static void viewTicketDetails() {

        clearScreen();
        printTitle("VIEW TICKET");

        int id = readInt("Enter Ticket ID: ");

        Ticket ticket = findTicket(id);

        if (ticket == null) {
            error("Ticket not found.");
            pause();
            return;
        }

        if (!canViewTicket(ticket)) {
            error("You do not have permission to view this ticket.");
            pause();
            return;
        }

        printTicketDetails(ticket);

        pause();
    }

    // ============================================================
    // ASSIGN TICKET
    // ============================================================

    static void assignTicket() {

        clearScreen();
        printTitle("ASSIGN / REASSIGN TICKET");

        int id = readInt("Ticket ID: ");

        Ticket ticket = findTicket(id);

        if (ticket == null) {
            error("Ticket not found.");
            pause();
            return;
        }

        List<User> staff =
                getStaffMembers();

        if (staff.isEmpty()) {
            error("No support staff available.");
            pause();
            return;
        }

        System.out.println();
        System.out.println("Available Support Staff:");

        for (User user : staff) {

            System.out.println(
                    "  " + user.id
                            + " - "
                            + user.name
                            + " ("
                            + user.email
                            + ")"
            );
        }

        int staffId =
                readInt("Enter Staff ID: ");

        User selected =
                findUserById(staffId);

        if (selected == null
                || selected.role != Role.STAFF) {

            error("Invalid support staff ID.");
            pause();
            return;
        }

        ticket.assignedStaffId =
                selected.id;

        ticket.assignedStaffName =
                selected.name;

        if (ticket.status == Status.OPEN) {

            ticket.status =
                    Status.ASSIGNED;
        }

        ticket.updatedAt =
                LocalDateTime.now();

        ticket.addHistory(
                ticket.status,
                "Ticket assigned to "
                        + selected.name,
                currentUser.name
        );

        notifyUser(
                ticket.createdBy,
                ticket.id,
                "Ticket #" + ticket.id
                        + " assigned to "
                        + selected.name
        );

        notifications.add(
                new Notification(
                        selected.id,
                        ticket.id,
                        "Ticket #" + ticket.id
                                + " has been assigned to you."
                )
        );

        success(
                "Ticket #" + ticket.id
                        + " assigned to "
                        + selected.name
        );

        pause();
    }

    // ============================================================
    // STAFF UPDATE STATUS
    // ============================================================

    static void staffUpdateStatus() {

        clearScreen();
        printTitle("UPDATE TICKET STATUS");

        int id = readInt("Ticket ID: ");

        Ticket ticket = findTicket(id);

        if (ticket == null) {
            error("Ticket not found.");
            pause();
            return;
        }

        if (ticket.assignedStaffId == null
                || ticket.assignedStaffId != currentUser.id) {

            error("This ticket is not assigned to you.");
            pause();
            return;
        }

        System.out.println();
        System.out.println("1. IN_PROGRESS");
        System.out.println("2. RESOLVED");

        int choice =
                readInt("Choose status: ");

        if (choice == 1) {

            updateTicketStatus(
                    ticket,
                    Status.IN_PROGRESS,
                    "Ticket work started."
            );

        } else if (choice == 2) {

            String note =
                    readString("Resolution note: ");

            ticket.resolutionNote = note;

            updateTicketStatus(
                    ticket,
                    Status.RESOLVED,
                    note
            );

        } else {

            error("Invalid status.");
        }

        pause();
    }

    // ============================================================
    // RESOLVE TICKET
    // ============================================================

    static void resolveTicket() {

        clearScreen();
        printTitle("RESOLVE TICKET");

        int id =
                readInt("Ticket ID: ");

        Ticket ticket =
                findTicket(id);

        if (ticket == null) {
            error("Ticket not found.");
            pause();
            return;
        }

        if (ticket.assignedStaffId == null
                || ticket.assignedStaffId != currentUser.id) {

            error("This ticket is not assigned to you.");
            pause();
            return;
        }

        String note =
                readMultiline(
                        "Enter resolution details: "
                );

        ticket.resolutionNote = note;

        updateTicketStatus(
                ticket,
                Status.RESOLVED,
                note
        );

        pause();
    }

    // ============================================================
    // ADMIN STATUS UPDATE
    // ============================================================

    static void adminUpdateStatus() {

        clearScreen();
        printTitle("ADMIN STATUS UPDATE");

        int id =
                readInt("Ticket ID: ");

        Ticket ticket =
                findTicket(id);

        if (ticket == null) {
            error("Ticket not found.");
            pause();
            return;
        }

        System.out.println();
        System.out.println("1. OPEN");
        System.out.println("2. ASSIGNED");
        System.out.println("3. IN_PROGRESS");
        System.out.println("4. RESOLVED");
        System.out.println("5. CLOSED");

        int choice =
                readInt("Choose status: ");

        Status status =
                statusFromChoice(choice);

        if (status == null) {
            error("Invalid status.");
            pause();
            return;
        }

        String note =
                readString("Note: ");

        if (status == Status.RESOLVED) {
            ticket.resolutionNote = note;
        }

        updateTicketStatus(
                ticket,
                status,
                note
        );

        pause();
    }

    // ============================================================
    // USER CLOSE TICKET
    // ============================================================

    static void closeTicket(Ticket ticket) {

        if (ticket.status != Status.RESOLVED) {

            error(
                    "Only resolved tickets can be closed."
            );

            return;
        }

        updateTicketStatus(
                ticket,
                Status.CLOSED,
                "Ticket closed by user."
        );
    }

    // ============================================================
    // STATUS UPDATE CORE
    // ============================================================

    static void updateTicketStatus(
            Ticket ticket,
            Status newStatus,
            String note) {

        Status oldStatus =
                ticket.status;

        ticket.status =
                newStatus;

        ticket.updatedAt =
                LocalDateTime.now();

        ticket.addHistory(
                newStatus,
                note,
                currentUser.name
        );

        notifyUser(
                ticket.createdBy,
                ticket.id,
                "Ticket #" + ticket.id
                        + " changed from "
                        + oldStatus
                        + " to "
                        + newStatus
        );

        if (ticket.assignedStaffId != null
                && ticket.assignedStaffId
                != currentUser.id) {

            notifications.add(
                    new Notification(
                            ticket.assignedStaffId,
                            ticket.id,
                            "Ticket #" + ticket.id
                                    + " status changed to "
                                    + newStatus
                    )
            );
        }

        success(
                "Ticket #" + ticket.id
                        + " status updated to "
                        + newStatus
        );
    }

    // ============================================================
    // ADD COMMENT
    // ============================================================

    static void addComment() {

        clearScreen();
        printTitle("ADD COMMENT");

        int id =
                readInt("Ticket ID: ");

        Ticket ticket =
                findTicket(id);

        if (ticket == null) {
            error("Ticket not found.");
            pause();
            return;
        }

        if (!canViewTicket(ticket)) {
            error("You cannot comment on this ticket.");
            pause();
            return;
        }

        String comment =
                readMultiline("Comment: ");

        ticket.comments.add(
                new Comment(
                        nextCommentId++,
                        currentUser,
                        comment
                )
        );

        ticket.updatedAt =
                LocalDateTime.now();

        ticket.addHistory(
                ticket.status,
                "Comment added",
                currentUser.name
        );

        notifyUser(
                ticket.createdBy,
                ticket.id,
                "New comment added to ticket #"
                        + ticket.id
        );

        if (ticket.assignedStaffId != null) {

            notifyUser(
                    ticket.assignedStaffId,
                    ticket.id,
                    "New comment added to ticket #"
                            + ticket.id
            );
        }

        success("Comment added successfully.");

        pause();
    }

    // ============================================================
    // HISTORY
    // ============================================================

    static void viewHistory() {

        clearScreen();
        printTitle("TICKET HISTORY");

        int id =
                readInt("Ticket ID: ");

        Ticket ticket =
                findTicket(id);

        if (ticket == null) {
            error("Ticket not found.");
            pause();
            return;
        }

        if (!canViewTicket(ticket)) {
            error("You cannot view this ticket.");
            pause();
            return;
        }

        System.out.println();

        for (History history : ticket.history) {

            System.out.println(
                    CYAN
                            + history.changedAt.format(DATE)
                            + RESET
                            + "  "
                            + BOLD
                            + history.status
                            + RESET
            );

            System.out.println(
                    "     "
                            + history.note
            );

            System.out.println(
                    "     By: "
                            + history.changedBy
            );

            System.out.println();
        }

        pause();
    }

    // ============================================================
    // FEEDBACK
    // ============================================================

    static void giveFeedback() {

        clearScreen();
        printTitle("TICKET FEEDBACK");

        int id =
                readInt("Ticket ID: ");

        Ticket ticket =
                findTicket(id);

        if (ticket == null) {
            error("Ticket not found.");
            pause();
            return;
        }

        if (ticket.createdBy != currentUser.id) {

            error(
                    "Only the ticket creator can give feedback."
            );

            pause();
            return;
        }

        if (ticket.status != Status.RESOLVED
                && ticket.status != Status.CLOSED) {

            error(
                    "Feedback is available after resolution."
            );

            pause();
            return;
        }

        if (ticket.feedback != null) {

            error("Feedback has already been submitted.");
            pause();
            return;
        }

        int rating =
                readInt("Rating (1-5): ");

        if (rating < 1 || rating > 5) {

            error("Rating must be between 1 and 5.");
            pause();
            return;
        }

        String comment =
                readMultiline("Feedback comment: ");

        ticket.feedback =
                new Feedback(
                        rating,
                        comment,
                        currentUser.name
                );

        success("Thank you for your feedback.");

        pause();
    }

    // ============================================================
    // NOTIFICATIONS
    // ============================================================

    static void viewNotifications() {

        clearScreen();
        printTitle("NOTIFICATIONS");

        List<Notification> mine =
                new ArrayList<>();

        for (Notification n : notifications) {

            if (n.userId == currentUser.id) {
                mine.add(n);
            }
        }

        if (mine.isEmpty()) {

            System.out.println(
                    "No notifications."
            );

        } else {

            for (Notification n : mine) {

                String marker =
                        n.read ? " " : "*";

                System.out.println(
                        YELLOW
                                + marker
                                + " "
                                + n.createdAt.format(DATE)
                                + RESET
                );

                System.out.println(
                        "  "
                                + n.message
                );

                System.out.println();

                n.read = true;
            }
        }

        pause();
    }

    // ============================================================
    // PROFILE
    // ============================================================

    static void viewProfile() {

        clearScreen();
        printTitle("MY PROFILE");

        System.out.println(
                "ID        : "
                        + currentUser.id
        );

        System.out.println(
                "Name      : "
                        + currentUser.name
        );

        System.out.println(
                "Email     : "
                        + currentUser.email
        );

        System.out.println(
                "Role      : "
                        + currentUser.role
        );

        System.out.println(
                "Created   : "
                        + currentUser.createdAt.format(DATE)
        );

        pause();
    }

    // ============================================================
    // ADMIN USER MANAGEMENT
    // ============================================================

    static void manageUsers() {

        while (true) {

            clearScreen();
            printTitle("USER MANAGEMENT");

            for (User user : users) {

                System.out.printf(
                        "%-4d %-24s %-30s %-8s%n",
                        user.id,
                        user.name,
                        user.email,
                        user.role
                );
            }

            System.out.println();
            System.out.println("1. Change User Role");
            System.out.println("2. Search User");
            System.out.println("0. Back");

            int choice =
                    readInt("Select option: ");

            if (choice == 0) return;

            if (choice == 1) {

                int id =
                        readInt("User ID: ");

                User user =
                        findUserById(id);

                if (user == null) {

                    error("User not found.");
                    pause();
                    continue;
                }

                System.out.println(
                        "1. USER"
                );

                System.out.println(
                        "2. STAFF"
                );

                System.out.println(
                        "3. ADMIN"
                );

                int roleChoice =
                        readInt("New role: ");

                Role newRole =
                        roleFromChoice(roleChoice);

                if (newRole == null) {

                    error("Invalid role.");
                    pause();
                    continue;
                }

                user.role = newRole;

                success(
                        "Role updated for "
                                + user.name
                );

                pause();
            }

            else if (choice == 2) {

                String search =
                        readString("Search name/email: ")
                                .toLowerCase();

                for (User user : users) {

                    if (user.name.toLowerCase()
                            .contains(search)
                            || user.email.toLowerCase()
                            .contains(search)) {

                        System.out.println(
                                user.id
                                        + " | "
                                        + user.name
                                        + " | "
                                        + user.email
                                        + " | "
                                        + user.role
                        );
                    }
                }

                pause();
            }
        }
    }

    // ============================================================
    // REPORTS
    // ============================================================

    static void reports() {

        clearScreen();
        printTitle("ADMIN REPORTS");

        int total = tickets.size();

        int open = 0;
        int assigned = 0;
        int inProgress = 0;
        int resolved = 0;
        int closed = 0;
        int critical = 0;

        for (Ticket ticket : tickets) {

            switch (ticket.status) {

                case OPEN:
                    open++;
                    break;

                case ASSIGNED:
                    assigned++;
                    break;

                case IN_PROGRESS:
                    inProgress++;
                    break;

                case RESOLVED:
                    resolved++;
                    break;

                case CLOSED:
                    closed++;
                    break;
            }

            if (ticket.priority == Priority.CRITICAL) {
                critical++;
            }
        }

        System.out.println(
                "Total Tickets     : " + total
        );

        System.out.println(
                "Open              : " + open
        );

        System.out.println(
                "Assigned          : " + assigned
        );

        System.out.println(
                "In Progress       : " + inProgress
        );

        System.out.println(
                "Resolved          : " + resolved
        );

        System.out.println(
                "Closed            : " + closed
        );

        System.out.println(
                "Critical Priority : " + critical
        );

        System.out.println();
        System.out.println(
                BOLD
                        + "CATEGORY REPORT"
                        + RESET
        );

        Map<String,Integer> categories =
                new LinkedHashMap<>();

        for (Ticket ticket : tickets) {

            categories.put(
                    ticket.category,
                    categories.getOrDefault(
                            ticket.category,
                            0
                    ) + 1
            );
        }

        for (Map.Entry<String,Integer> entry
                : categories.entrySet()) {

            System.out.println(
                    String.format(
                            "%-25s %d",
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        System.out.println();
        System.out.println(
                BOLD
                        + "STAFF WORKLOAD"
                        + RESET
        );

        for (User staff : getStaffMembers()) {

            int assignedCount = 0;
            int resolvedCount = 0;

            for (Ticket ticket : tickets) {

                if (ticket.assignedStaffId != null
                        && ticket.assignedStaffId
                        == staff.id) {

                    assignedCount++;

                    if (ticket.status
                            == Status.RESOLVED
                            || ticket.status
                            == Status.CLOSED) {

                        resolvedCount++;
                    }
                }
            }

            System.out.println(
                    String.format(
                            "%-25s Assigned: %-4d Resolved: %d",
                            staff.name,
                            assignedCount,
                            resolvedCount
                    )
            );
        }

        pause();
    }

    // ============================================================
    // DASHBOARD SUMMARY
    // ============================================================

    static void printDashboardSummary() {

        int open = 0;
        int inProgress = 0;
        int resolved = 0;
        int closed = 0;

        for (Ticket ticket : tickets) {

            boolean visible = false;

            if (currentUser.role == Role.ADMIN) {
                visible = true;
            }

            if (currentUser.role == Role.USER
                    && ticket.createdBy
                    == currentUser.id) {
                visible = true;
            }

            if (currentUser.role == Role.STAFF
                    && ticket.assignedStaffId != null
                    && ticket.assignedStaffId
                    == currentUser.id) {
                visible = true;
            }

            if (!visible) continue;

            if (ticket.status == Status.OPEN
                    || ticket.status == Status.ASSIGNED) {
                open++;
            }

            if (ticket.status == Status.IN_PROGRESS) {
                inProgress++;
            }

            if (ticket.status == Status.RESOLVED) {
                resolved++;
            }

            if (ticket.status == Status.CLOSED) {
                closed++;
            }
        }

        System.out.println(
                "┌──────────────────────────────────────────────────────┐"
        );

        System.out.printf(
                "│  OPEN %-8d │ IN PROGRESS %-6d │ RESOLVED %-6d │ CLOSED %-6d │%n",
                open,
                inProgress,
                resolved,
                closed
        );

        System.out.println(
                "└──────────────────────────────────────────────────────┘"
        );
    }

    // ============================================================
    // PRINT TICKET TABLE
    // ============================================================

    static void printTicketTable(
            List<Ticket> list) {

        if (list.isEmpty()) {

            System.out.println(
                    "No tickets found."
            );

            return;
        }

        System.out.println();

        System.out.printf(
                "%-7s %-25s %-18s %-10s %-14s %-18s%n",
                "ID",
                "TITLE",
                "CATEGORY",
                "PRIORITY",
                "STATUS",
                "ASSIGNED"
        );

        System.out.println(
                "------------------------------------------------------------------------------------------"
        );

        for (Ticket ticket : list) {

            System.out.printf(
                    "#%-6d %-25s %-18s %-10s %-14s %-18s%n",
                    ticket.id,
                    truncate(ticket.title, 24),
                    truncate(ticket.category, 17),
                    ticket.priority,
                    ticket.status,
                    truncate(
                            ticket.assignedStaffName == null
                                    ? "Unassigned"
                                    : ticket.assignedStaffName,
                            17
                    )
            );
        }
    }

    // ============================================================
    // PRINT TICKET DETAILS
    // ============================================================

    static void printTicketDetails(
            Ticket ticket) {

        System.out.println();
        System.out.println(
                BOLD
                        + "TICKET #"
                        + ticket.id
                        + RESET
        );

        System.out.println(
                "Title       : "
                        + ticket.title
        );

        System.out.println(
                "Category    : "
                        + ticket.category
        );

        System.out.println(
                "Priority    : "
                        + ticket.priority
        );

        System.out.println(
                "Status      : "
                        + ticket.status
        );

        System.out.println(
                "Created By  : "
                        + ticket.createdByName
        );

        System.out.println(
                "Assigned To : "
                        + (ticket.assignedStaffName == null
                        ? "Unassigned"
                        : ticket.assignedStaffName)
        );

        System.out.println(
                "Created     : "
                        + ticket.createdAt.format(DATE)
        );

        System.out.println(
                "Updated     : "
                        + ticket.updatedAt.format(DATE)
        );

        System.out.println();
        System.out.println(
                BOLD
                        + "DESCRIPTION"
                        + RESET
        );

        System.out.println(
                ticket.description
        );

        if (ticket.resolutionNote != null
                && !ticket.resolutionNote.isBlank()) {

            System.out.println();
            System.out.println(
                    BOLD
                            + "RESOLUTION"
                            + RESET
            );

            System.out.println(
                    ticket.resolutionNote
            );
        }

        System.out.println();
        System.out.println(
                BOLD
                        + "COMMENTS"
                        + RESET
        );

        if (ticket.comments.isEmpty()) {

            System.out.println(
                    "No comments."
            );

        } else {

            for (Comment comment
                    : ticket.comments) {

                System.out.println(
                        comment.userName
                                + " ["
                                + comment.createdAt.format(DATE)
                                + "]"
                );

                System.out.println(
                        "  "
                                + comment.text
                );

                System.out.println();
            }
        }

        if (ticket.feedback != null) {

            System.out.println(
                    BOLD
                            + "FEEDBACK"
                            + RESET
            );

            System.out.println(
                    "Rating : "
                            + ticket.feedback.rating
                            + "/5"
            );

            System.out.println(
                    "Comment: "
                            + ticket.feedback.comment
            );
        }
    }

    // ============================================================
    // PERMISSION CHECK
    // ============================================================

    static boolean canViewTicket(
            Ticket ticket) {

        if (currentUser.role == Role.ADMIN) {
            return true;
        }

        if (currentUser.role == Role.USER) {

            return ticket.createdBy
                    == currentUser.id;
        }

        if (currentUser.role == Role.STAFF) {

            return ticket.assignedStaffId != null
                    && ticket.assignedStaffId
                    == currentUser.id;
        }

        return false;
    }

    // ============================================================
    // FIND METHODS
    // ============================================================

    static Ticket findTicket(int id) {

        for (Ticket ticket : tickets) {

            if (ticket.id == id) {
                return ticket;
            }
        }

        return null;
    }

    static User findUserById(int id) {

        for (User user : users) {

            if (user.id == id) {
                return user;
            }
        }

        return null;
    }

    static User findUserByEmail(
            String email) {

        for (User user : users) {

            if (user.email.equalsIgnoreCase(email)) {
                return user;
            }
        }

        return null;
    }

    static List<User> getStaffMembers() {

        List<User> staff =
                new ArrayList<>();

        for (User user : users) {

            if (user.role == Role.STAFF) {
                staff.add(user);
            }
        }

        return staff;
    }

    // ============================================================
    // NOTIFICATIONS
    // ============================================================

    static void notifyUser(
            int userId,
            int ticketId,
            String message) {

        notifications.add(
                new Notification(
                        userId,
                        ticketId,
                        message
                )
        );
    }

    static void notifyAdmins(
            String message) {

        for (User user : users) {

            if (user.role == Role.ADMIN) {

                notifications.add(
                        new Notification(
                                user.id,
                                0,
                                message
                        )
                );
            }
        }
    }

    // ============================================================
    // INPUT HELPERS
    // ============================================================

    static String readString(
            String prompt) {

        System.out.print(prompt);

        return scanner.nextLine().trim();
    }

    static String readMultiline(
            String prompt) {

        System.out.print(prompt);

        return scanner.nextLine().trim();
    }

    static int readInt(
            String prompt) {

        while (true) {

            try {

                System.out.print(prompt);

                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (Exception e) {

                error("Please enter a valid number.");
            }
        }
    }

    // ============================================================
    // CONVERSION HELPERS
    // ============================================================

    static String categoryName(
            int choice) {

        switch (choice) {

            case 1:
                return "Technical Issue";

            case 2:
                return "Software Issue";

            case 3:
                return "Hardware Issue";

            case 4:
                return "Account Issue";

            case 5:
                return "Network Issue";

            default:
                return "Other";
        }
    }

    static Priority priorityFromChoice(
            int choice) {

        switch (choice) {

            case 1:
                return Priority.LOW;

            case 2:
                return Priority.MEDIUM;

            case 3:
                return Priority.HIGH;

            case 4:
                return Priority.CRITICAL;

            default:
                return Priority.MEDIUM;
        }
    }

    static Status statusFromChoice(
            int choice) {

        switch (choice) {

            case 1:
                return Status.OPEN;

            case 2:
                return Status.ASSIGNED;

            case 3:
                return Status.IN_PROGRESS;

            case 4:
                return Status.RESOLVED;

            case 5:
                return Status.CLOSED;

            default:
                return null;
        }
    }

    static Role roleFromChoice(
            int choice) {

        switch (choice) {

            case 1:
                return Role.USER;

            case 2:
                return Role.STAFF;

            case 3:
                return Role.ADMIN;

            default:
                return null;
        }
    }

    // ============================================================
    // DEMO ACCOUNTS
    // ============================================================

    static void showDemoAccounts() {

        clearScreen();
        printTitle("DEMO ACCOUNTS");

        System.out.println(
                "ADMIN"
        );

        System.out.println(
                "Email    : admin@ticket.com"
        );

        System.out.println(
                "Password : admin123"
        );

        System.out.println();

        System.out.println(
                "SUPPORT STAFF"
        );

        System.out.println(
                "Email    : staff@ticket.com"
        );

        System.out.println(
                "Password : staff123"
        );

        System.out.println();

        System.out.println(
                "USER"
        );

        System.out.println(
                "Email    : user@ticket.com"
        );

        System.out.println(
                "Password : user123"
        );
    }

    // ============================================================
    // UI HELPERS
    // ============================================================

    static void printHeader() {

        System.out.println(
                BLUE
                        + "╔══════════════════════════════════════════════════════════╗"
                        + RESET
        );

        System.out.println(
                BLUE
                        + "║"
                        + RESET
                        + BOLD
                        + "              TICKET MANAGEMENT SYSTEM                 "
                        + RESET
                        + BLUE
                        + "║"
                        + RESET
        );

        System.out.println(
                BLUE
                        + "╚══════════════════════════════════════════════════════════╝"
                        + RESET
        );

        if (currentUser != null) {

            System.out.println(
                    "  Logged in: "
                            + currentUser.name
                            + " | Role: "
                            + currentUser.role
            );
        }

        System.out.println();
    }

    static void printTitle(
            String title) {

        System.out.println(
                CYAN
                        + "\n══════════════════════════════════════════════════════════"
                        + RESET
        );

        System.out.println(
                BOLD
                        + "  "
                        + title
                        + RESET
        );

        System.out.println(
                CYAN
                        + "══════════════════════════════════════════════════════════"
                        + RESET
        );

        System.out.println();
    }

    static void success(
            String message) {

        System.out.println(
                GREEN
                        + "\n✓ "
                        + message
                        + RESET
        );
    }

    static void error(
            String message) {

        System.out.println(
                RED
                        + "\n✗ "
                        + message
                        + RESET
        );
    }

    static String truncate(
            String value,
            int length) {

        if (value == null) {
            return "";
        }

        if (value.length() <= length) {
            return value;
        }

        return value.substring(
                0,
                Math.max(0, length - 3)
        ) + "...";
    }

    static void pause() {

        System.out.println();
        System.out.print(
                "Press ENTER to continue..."
        );

        scanner.nextLine();
    }

    static void clearScreen() {

        // Portable console spacing.
        // Works even when ANSI screen clearing is unsupported.
        System.out.println("\n\n\n");
    }

    static void logout() {

        String name =
                currentUser.name;

        currentUser = null;

        success(
                name
                        + " logged out successfully."
        );

        pause();
    }

    static void exitApplication() {

        System.out.println();
        System.out.println(
                CYAN
                        + "Thank you for using Ticket Management System."
                        + RESET
        );

        System.exit(0);
    }
}
