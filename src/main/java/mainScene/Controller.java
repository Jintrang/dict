package mainScene;

import Alerts.Alerts;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import org.jsoup.Jsoup;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;


public class Controller implements Initializable {

    @FXML
    private Button deleteButton;

    @FXML
    private Button ggTranslate;

    @FXML
    private Button fixWord;

    @FXML
    private Button addButton;

    @FXML
    private Button translate;

    @FXML
    private Button closeBtn;

    @FXML
    private Button soundBtn;

    @FXML
    private Button check;

    @FXML
    private ListView<String> listView;

    @FXML
    private TextField engWord;

    @FXML
    private TextArea vnamWord;

    @FXML
    private Button toMeaning;

    ArrayList<String> wordList = new ArrayList<>();
    private Alerts alerts = new Alerts();

    public Controller() throws SQLException {
    }

    // cài đặt nút kết thúc chương trình
    public void Exit (ActionEvent event) {
        System.exit(0);
    }

    private Stage stage;
    private Scene scene;
    private Parent root;


    //chuyển sang giao diện gg translate
    public void ggTranslate (ActionEvent event) throws Exception{
        URL url = new File("C:\\Users\\Admin\\IdeaProjects\\dic_uet2\\src\\main\\resources\\FXML\\ggTranslate.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        File f = new File("C:\\Users\\Admin\\IdeaProjects\\dic_uet2\\src\\main\\resources\\Css\\mainStyle.css");
        root.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        Stage window = (Stage) ggTranslate.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    public void addButton (ActionEvent event) throws Exception{
        URL url = new File("C:\\Users\\Admin\\IdeaProjects\\dic_uet2\\src\\main\\resources\\FXML\\addWord.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        File f = new File("C:\\Users\\Admin\\IdeaProjects\\dic_uet2\\src\\main\\resources\\Css\\mainStyle.css");
        root.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        Stage window = (Stage) ggTranslate.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    @FXML
    private void handleMouseClickAWord( MouseEvent arg0 ) {
        String selectedWord = listView.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            engWord.setText(selectedWord);
        }
    }
    //cài đặt tra từ trong listview
    //nhập vào chữ nào thì hiện ra các từ bắt đầu bằng chữ đấy
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            //myConnect.wordList();
            engWord.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if (!(engWord.getText().equals(null)) || engWord.getText().equals("")) {
                        boolean isNull = true;
                        listView.getItems().clear();
                        wordList.clear();
                        t1 = t1.trim();

                        String finalT = t1;

                        for (String w : mainStart.Controller.myConnect.wordsList) {
                            if (w.indexOf(t1) == 0) {
                                isNull = false;
                                break;
                            }
                        }

                        if (isNull == false) {
                            for (String w :mainStart.Controller.myConnect.wordsList) {
                                if (w.indexOf(finalT) == 0) {
                                    wordList.add(w);
                                }
                            }
                            int n = 0;
                            for (int i = 0; i < wordList.size(); i++) {
                                if (!listView.getItems().contains(wordList.get(i))) {
                                    listView.getItems().add(wordList.get(i));
                                    n++;
                                }
                                if (n == 15) {
                                    break;
                                }
                            }
                        } else {
                            listView.getItems().add("Từ không tồn tại");
                        }
                    } else {
                        listView.getItems().clear();
                    }
                }
            });
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    @FXML
    private void soundBtn(ActionEvent event) {
        String word = engWord.getText();
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        Voice voice = VoiceManager.getInstance().getVoice("kevin16");
        if (voice != null) {
            voice.allocate();
            voice.speak(word);
        } else {
            throw new IllegalStateException("Wrong");
        }
    }
    // cài đặt nút button (dịch từ)
    public void toMeaning (ActionEvent event) throws Exception{
        String word = engWord.getText();
//        myConnect.connect();
        vnamWord.setWrapText(true);
        vnamWord.setEditable(false);
        String transText = (Jsoup.parse(mainStart.Controller.myConnect.showData(word)).text()).replaceAll("\\*", "\n*");
        transText = transText.replaceAll("-", "\n\t-");
        vnamWord.setText(transText);
        check.setVisible(false);
    }

    //chỉnh sửa nghĩa của từ
    public void fixWord (ActionEvent event){
        check.setVisible(true);
        vnamWord.setEditable(true);
        alerts.showAlertInfo("Information" , "Bạn đã cho phép chỉnh sửa nghĩa từ này!");
    }

    // sau khi chỉnh sửa thì dùng nút check để lưu lại nghĩa của từ
    public void check (ActionEvent event) throws SQLException {
            String newMeaning = vnamWord.getText();
            String word = engWord.getText();
            Alert alertConfirmation = alerts.alertConfirmation("Update" ,
                "Nhập nghĩa bạn muốn sửa ?");
        // option != null.
        Optional<ButtonType> option = alertConfirmation.showAndWait();
        if (option.get() == ButtonType.OK) {
            //myConnect.wordList();
            final long startTime = System.currentTimeMillis();
            if(mainStart.Controller.myConnect.fixWord(word, newMeaning))
                alerts.showAlertInfo("Information" , "Cập nhập thành công!");
            else
                alerts.showAlertInfo("Information" , "Không có từ cần sửa!");

//          myConnect.connect();
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime));
        } else{
            alerts.showAlertInfo("Information" , "Thay đổi không được công nhận!");
        }
        // update status
        check.setVisible(false);
        vnamWord.setEditable(false);
    }

    //cài đặt nút xóa từ
    public void deleteButton (ActionEvent event) throws  Exception {
        String word = engWord.getText();
        Alert alertWarning = alerts.alertWarning("Delete" , "Bạn chắc chắn muốn xóa từ này?");
        // option != null.
        alertWarning.getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> option = alertWarning.showAndWait();
        if (option.get() == ButtonType.OK) {
            mainStart.Controller.myConnect.deleteWord(word);
            alerts.showAlertInfo("Information" , "Xóa thành công");
            //myConnect.connect();
        } else {
            alerts.showAlertInfo("Information" , "Thay đổi không được công nhận");
        }
        begin();
    }

    // trả textField và textArea về trạng thái ban đầu
    public void begin() {
        vnamWord.setText("");
        engWord.setText("");
    }
}
