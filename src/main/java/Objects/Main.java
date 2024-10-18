package Objects;

import java.util.*;

public class Main {
    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("HELLLLLOOOO");
        Scanner scanner = new Scanner(System.in);
        Login loginAttempt = new Login();

        while (true) {
            System.out.println("1. Đăng nhập");
            System.out.println("2. Đăng ký");
            System.out.println("0. Thoát");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    System.out.print("Tên đăng nhập: ");
                    String username = scanner.nextLine();
                    System.out.print("Mật khẩu: ");
                    String password = scanner.nextLine();
                    User user = loginAttempt.login(username, password);

                    if (user == null) {
                        System.out.println("Đăng nhập thất bại");
                    } else {
                        // Giao diện sau đăng nhập sẽ ở trong một vòng lặp riêng, cho đến khi người dùng đăng xuất
                        boolean isLoggedIn = true;
                        while (isLoggedIn) {
                            System.out.println("Thư viện abc");
                            if (user.getUserType().equals("admin")) {
                                System.out.println("1. Thêm sách");
                                System.out.println("2. Cập nhật sách");
                                System.out.println("3. Tìm sách");
                                System.out.println("4. Hiển thị toàn bộ sách");
                                System.out.println("0. Đăng xuất");
                                int adminChoice = scanner.nextInt();
                                scanner.nextLine();
                                switch (adminChoice) {
                                    case 1:
                                        System.out.print("Nhập tên sách: ");
                                        String title = scanner.nextLine();
                                        System.out.print("Nhập tác giả: ");
                                        String author = scanner.nextLine();
                                        System.out.print("Nhập thể loại: ");
                                        String genre = scanner.nextLine();
                                        if (library.findExactlyBook(title, author, genre) != null) {
                                            System.out.println("Sách đã tồn tại trong thư viện");
                                        } else {
                                            System.out.print("Nhập số lượng: ");
                                            int quantity = scanner.nextInt();
                                            scanner.nextLine();
                                            Book newBook = new Book(title, author, genre, quantity);
                                            library.addBook(newBook);
                                            System.out.println("Đã thêm sách.");
                                        }
                                        break;

                                    case 2:
                                        System.out.println("1. Cập nhật toàn bộ thông tin");
                                        System.out.println("2. Cập nhật số lượng");
                                        int updateChoice = scanner.nextInt();
                                        scanner.nextLine();
                                        switch (updateChoice) {
                                            case 1:
                                                System.out.print("Nhập tên sách cũ: ");
                                                String oldTitle = scanner.nextLine();
                                                System.out.print("Nhập tác giả cũ: ");
                                                String oldAuthor = scanner.nextLine();
                                                System.out.print("Nhập thể loại cũ: ");
                                                String oldGenre = scanner.nextLine();
                                                if (library.findExactlyBook(oldTitle, oldAuthor, oldGenre) == null) {
                                                    System.out.println("Sách chưa tồn tại trong thư viện");
                                                } else {
                                                    System.out.print("Nhập tên sách mới: ");
                                                    String newTitle = scanner.nextLine();
                                                    System.out.print("Nhập tác giả mới: ");
                                                    String newAuthor = scanner.nextLine();
                                                    System.out.print("Nhập thể loại mới: ");
                                                    String newGenre = scanner.nextLine();
                                                    System.out.print("Nhập số lượng mới: ");
                                                    int newQuantity = scanner.nextInt();
                                                    scanner.nextLine();
                                                    if (library.findExactlyBook(newTitle, newAuthor, newGenre) != null) {
                                                        System.out.println("Sách mới tồn tại trong thư viện");
                                                    }
                                                    else {
                                                        library.updateBook(oldTitle, oldAuthor, oldGenre, newTitle, newAuthor, newGenre, newQuantity);
                                                        System.out.println("Cập nhật sách thành công.");
                                                    }
                                                }
                                                break;

                                            case 2:
                                                System.out.print("Nhập tên sách: ");
                                                String title1 = scanner.nextLine();
                                                System.out.print("Nhập tác giả: ");
                                                String author1 = scanner.nextLine();
                                                System.out.print("Nhập thể loại: ");
                                                String genre1 = scanner.nextLine();
                                                if (library.findExactlyBook(title1, author1, genre1) == null) {
                                                    System.out.println("Sách chưa tồn tại trong thư viện");
                                                } else {
                                                    Book existingBook = library.findExactlyBook(title1, author1, genre1);
                                                    System.out.println("Số lượng hiện tại của sách này: " + existingBook.getQuantity());
                                                    System.out.print("Nhập số lượng mới: ");
                                                    int newQuantity = scanner.nextInt();
                                                    scanner.nextLine();
                                                    library.updateBook(title1, author1, genre1, title1, author1, genre1, newQuantity);
                                                    System.out.println("Cập nhật số lượng thành công.");
                                                }
                                                break;
                                        }
                                        break;

                                    case 3:
                                        System.out.println("Bạn có thể tìm theo tên sách, tác giả, hoặc thể loại, bỏ trống thứ không muốn tìm theo");
                                        System.out.println("Nhập tên sách");
                                        String findTitle = scanner.nextLine();
                                        System.out.println("Nhập tác giả");
                                        String findAuthor = scanner.nextLine();
                                        System.out.println("Nhập thể loại");
                                        String findGenre = scanner.nextLine();
                                        List<Book> findBookList = library.findBook(findTitle.isEmpty() ? null : findTitle,
                                                                          findAuthor.isEmpty() ? null : findAuthor,
                                                                          findGenre.isEmpty() ? null : findGenre);
                                        if (findBookList.isEmpty()) {
                                            System.out.println("Chưa tồn tại sách này");
                                        } else {
                                            for (Book book : findBookList) {
                                                book.printInfo();
                                            }
                                        }
                                        break;
                                    case 4:
                                        library.displayAllDocuments();
                                        break;
                                    case 0:
                                        isLoggedIn = false;  // Đăng xuất và thoát vòng lặp admin
                                        System.out.println("Đăng xuất thành công.");
                                        break;

                                    default:
                                        System.out.println("Lựa chọn không hợp lệ.");
                                }
                            } else {
                                System.out.println("1. Mượn sách");
                                System.out.println("2. Trả sách");
                                System.out.println("3. In thông tin sách đã mượn");
                                System.out.println("0. Đăng xuất");
                                int userChoice = scanner.nextInt();
                                scanner.nextLine();
                                switch (userChoice) {
                                    case 1:
                                        System.out.println("Nhập tên sách");
                                        String newBookTitle = scanner.nextLine();
                                        System.out.println("Nhập tên tác giả");
                                        String newBookAuthor = scanner.nextLine();
                                        System.out.println("Nhập thể loại sách");
                                        String newBookGenre = scanner.nextLine();
                                        Book newbook = new Book(newBookTitle,newBookAuthor,newBookGenre,1);
                                        user.borrowedBook(newbook);
                                        break;

                                    case 2:
                                        System.out.println("Nhập tên sách");
                                        String newBookTitle1 = scanner.nextLine();
                                        System.out.println("Nhập tên tác giả");
                                        String newBookAuthor1 = scanner.nextLine();
                                        System.out.println("Nhập thể loại sách");
                                        String newBookGenre1 = scanner.nextLine();
                                        Book newbook1 = new Book(newBookTitle1,newBookAuthor1,newBookGenre1,1);
                                        user.returnBook(newbook1);
                                        break;

                                    case 3:
                                        user.printUserInfo();
                                        break;

                                    case 0:
                                        isLoggedIn = false;  // Đăng xuất và thoát vòng lặp người dùng
                                        System.out.println("Đăng xuất thành công.");
                                        break;

                                    default:
                                        System.out.println("Lựa chọn không hợp lệ.");
                                }
                            }
                        }
                    }
                    break;

                case 2:
                    System.out.print("Tên đăng nhập: ");
                    String username1 = scanner.nextLine();
                    System.out.print("Mật khẩu: ");
                    String password1 = scanner.nextLine();
                    System.out.println("Loại tài khoản:    (nhập user hoặc admin)");
                    String userType1 = scanner.nextLine();
                    System.out.print("Mã sinh viên: ");
                    long id = scanner.nextLong();
                    while(!userType1.equals("user") && !userType1.equals("admin")) {
                        System.out.println("yêu cầu nhập đúng");
                        userType1=scanner.nextLine();
                    }
                    boolean registered = Login.register(username1, password1,id, userType1);
                    if (registered) {
                        System.out.println("Đăng ký thành công");
                    } else {
                        System.out.println("Tài khoản đã trùng");
                    }
                    break;
                case 0:
                    System.exit(0);
                    break;
            }
        }
    }
}
