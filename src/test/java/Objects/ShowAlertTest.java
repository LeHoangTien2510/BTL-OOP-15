package Objects;

import javafx.scene.control.Alert;
import org.junit.jupiter.api.Test;

class ShowAlertTest {

    @Test
    void testShowAlert() {
        // This method is difficult to unit test because it involves UI.
        // Typically, we mock or ignore this kind of method in unit tests.
        ShowAlert.showAlert(Alert.AlertType.INFORMATION, "Test Title", "Test Message");
    }
}
