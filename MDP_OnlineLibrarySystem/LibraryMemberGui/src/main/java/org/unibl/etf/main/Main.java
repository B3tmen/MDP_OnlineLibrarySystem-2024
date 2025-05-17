package org.unibl.etf.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.unibl.etf.util.gui.FxmlPaths;
import org.unibl.etf.util.gui.FxmlViewManager;

public class Main extends Application{

    public static void main(String[] args) {

        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        //String pass = "sifra";
        //System.out.println("sha256 password for 'sifra': " + TextHasher.getHash(pass));
        loadMainView();

    }

    private void loadMainView(){
//        Scanner scanner = new Scanner(System.in);
//        String username = "";
//        String recipient = "";
//        System.out.print("Enter your username: ");
//        username = scanner.nextLine();
//        System.out.print("Enter recipient: ");
//        recipient = scanner.nextLine();

        //LibraryMember member = new LibraryMember(username, "aa", "ivan", "kuruzovic", "adress2", "mail2.com");
        FxmlViewManager viewManager = new FxmlViewManager(FxmlPaths.USER_LOGIN_VIEW, "Library Member");
        viewManager.showView();

//        FxmlViewManager viewManager = new FxmlViewManager(FxmlPaths.USER_LOGIN_VIEW, "Library Member");
//        viewManager.showView();
    }
}
