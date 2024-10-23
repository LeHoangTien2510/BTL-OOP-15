package Objects;

import java.util.ArrayList;

public class User {
    private String name;
    private String username;
    private String password;
    private String userType;
    private long id;
    ArrayList<Book> borrowedBooks = new ArrayList<>();

    public User(String name, String username, String password, long id, String userType) {
        this.name = name;
        this.username = username; // Thêm dòng này
        this.password = password;
        this.id = id;
        this.userType = userType;
        this.borrowedBooks = new ArrayList<>();
    }


    public String getName(){
        return name;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getUserType(){
        return userType;
    }

    public long getId(){
        return id;
    }


    public void borrowedBook(Book book){
        if(borrowedBooks.contains(book)){
            System.out.println("Bạn đã mượn quyển sách này rồi");
        }
        else{
            borrowedBooks.add(book);
            System.out.println("Mượn sách thành công");
        }
    }

    public void returnBook(Book book){
        if(borrowedBooks.contains(book)) {
            borrowedBooks.remove(book);
            System.out.println("Đã trả sách");
        }
        else{
            System.out.println("Bạn chưa mượn cuốn sách này");
        }
    }

    public void printUserInfo() {
        int numberOfBooks = borrowedBooks.size();
        if(numberOfBooks == 0) {
            System.out.println("Bạn chưa mượn cuốn sách nào");
        }
        else{
        System.out.println("Tên: " + name);
        System.out.println("Sách đã mượn:");
        for (Book b : borrowedBooks) {
            b.printInfo();
        }
        }
    }
}

